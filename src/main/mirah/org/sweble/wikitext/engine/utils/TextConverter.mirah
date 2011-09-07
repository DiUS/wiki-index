###
#
# Copyright 2011 The Open Source Research Group,
#                University of Erlangen-Nürnberg
#
# Licensed under the Apache License, Version 2.0 (the "License")
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
package org.sweble.wikitext.engine.utils

import java.util.LinkedList
import java.util.regex.Pattern

import org.sweble.wikitext.engine.Page
import org.sweble.wikitext.engine.PageTitle
import org.sweble.wikitext.engine.utils.EntityReferences
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration
import org.sweble.wikitext.lazy.LinkTargetException
import org.sweble.wikitext.lazy.encval.IllegalCodePoint
import org.sweble.wikitext.lazy.parser.Bold
import org.sweble.wikitext.lazy.parser.ExternalLink
import org.sweble.wikitext.lazy.parser.HorizontalRule
import org.sweble.wikitext.lazy.parser.ImageLink
import org.sweble.wikitext.lazy.parser.Itemization
import org.sweble.wikitext.lazy.parser.ItemizationItem
import org.sweble.wikitext.lazy.parser.InternalLink
import org.sweble.wikitext.lazy.parser.Italics
import org.sweble.wikitext.lazy.parser.MagicWord
import org.sweble.wikitext.lazy.parser.Paragraph
import org.sweble.wikitext.lazy.parser.Section
import org.sweble.wikitext.lazy.parser.Url
import org.sweble.wikitext.lazy.parser.Whitespace
import org.sweble.wikitext.lazy.parser.XmlElement
import org.sweble.wikitext.lazy.preprocessor.TagExtension
import org.sweble.wikitext.lazy.preprocessor.Template
import org.sweble.wikitext.lazy.preprocessor.TemplateArgument
import org.sweble.wikitext.lazy.preprocessor.TemplateParameter
import org.sweble.wikitext.lazy.preprocessor.XmlComment
import org.sweble.wikitext.lazy.utils.XmlCharRef
import org.sweble.wikitext.lazy.utils.XmlEntityRef

import de.fau.cs.osr.ptk.common.Visitor
import de.fau.cs.osr.ptk.common.ast.AstNode
import de.fau.cs.osr.ptk.common.ast.NodeList
import de.fau.cs.osr.ptk.common.ast.Text
import de.fau.cs.osr.utils.StringUtils

class TextConverter < Visitor
  
  def self.split_pattern
    @split_pattern ||= Pattern.compile("\\s+")
  end
  
  # private config: SimpleWikiConfiguration
  # 
  # private wrapCol: int
  # 
  # private sb: StringBuilder
  # 
  # private line: StringBuilder
  # 
  # private extLinkNum: int
  # 
  # private pastBod: boolean
  # 
  # private needNewlines: int
  # 
  # private needSpace: boolean 
  # 
  # private noWrap: boolean
  # 
  # private sections: LinkedList;
  
  def initialize config:SimpleWikiConfiguration, wrapCol:int
    @config = config
    @wrapCol = wrapCol
  end
    
  protected
  
  def before(node: AstNode):boolean
    # This method is called by go() before visitation starts
    @sb = StringBuilder.new
    @line = StringBuilder.new
    
    @extLinkNum = 1
    @pastBod = false
    @needNewlines = 0
    @needSpace = false
    @noWrap = false
    @sections = LinkedList.new
    
    super(node)
  end
  
  def after(node: AstNode, result: Object)
    finishLine()
    
    # This method is called by go() after visitation has finished
    # The return value will be passed to go() which passes it to the caller
    
    @sb.toString()
  end
  
  # =========================================================================
  
  public 
  
  def visit(n: AstNode):void
    # Fallback for all nodes that are not explicitly handled below
    write("<UNSUPPORTED ")
    write(n.getNodeName())
    write(" />")
  end
  
  def visit(i: Itemization):void
    iterate(i)
  end
  
  def visit(i: ItemizationItem):void
    write("\t* ")
    iterate(i.getContent)
    newline(1)
  end
  
  def visit(n: NodeList):void
    iterate(n)
  end
  
  def visit(p: Page):void
    iterate(p.getContent())
  end
  
  def visit(text: Text):void
    write(text.getContent())
  end
  
  def visit(w: Whitespace):void
    write(" ")
  end
  
  def visit(b: Bold):void
#    write("**")
    iterate(b.getContent())
#    write("**")
  end
  
  def visit(i: Italics):void
#    write("//")
    iterate(i.getContent())
#    write("//")
  end
  
  def visit(cr: XmlCharRef):void   
    write(Character.toChars(cr.getCodePoint()))
  end
  
  def visit(er: XmlEntityRef):void   
    ch = EntityReferences.resolve(er.getName())
    
    if (ch.nil?)
      write('&')
      write(er.getName())
      write(';')
    else
      write(ch)
    end
  end
    
  def visit(url: Url):void    
    write(url.getProtocol())
    write(':')
    write(url.getPath())
  end
  
  def visit(link: ExternalLink):void    
    write('[')
    write((@extLinkNum = @extLinkNum + 1))
    write(']')
  end
  
  def visit(link: InternalLink):void    
    begin
      page = PageTitle.make(@config, link.getTarget())
      if (page.getNamespace().equals(@config.getNamespace("Category"))) then
        return
      end
        
    rescue LinkTargetException => e
    ensure 
    end
    
    write(link.getPrefix())
    if (link.getTitle().getContent().nil? || link.getTitle().getContent().isEmpty())
      write(link.getTarget())
    else
      iterate(link.getTitle())
    end
    write(link.getPostfix())
  end
  
  def visit(s: Section):void
    
    finishLine()
    
    savesb = @sb
    saveNoWrap = @noWrap
    
    @sb = StringBuilder.new
    @noWrap = true
    
    iterate(s.getTitle())
    finishLine()
    
    title = @sb.toString().trim()
    
    @sb = savesb
    
    if (s.getLevel() >= 1) then

      while (@sections.size() > s.getLevel())
        @sections.removeLast()
      end
        
      while (@sections.size() < s.getLevel())
        @sections.add(Integer.new(1))
      end
      
      StringBuilder @sb2 = StringBuilder.new
      size = (@sections.size)
      i = 0
      for section in @sections
        if ( i >= 1) then
          @sb2.append(section)
          @sb2.append('.')
        end
        i = i + 1
      end
      
      @sb2.append(' ') if (@sb2.length() > 0)
        
      @sb2.append(title)
      title = @sb2.toString()
    end
    
    newline(2)
    write(title)
    
    newline(1)
    write(StringUtils.strrep('-', title.length()))
    newline(2)
    
    @noWrap = saveNoWrap
    
    iterate(s.getBody())
    
    while (@sections.size() > s.getLevel())
      @sections.removeLast()
    end
    last = Integer(@sections.removeLast())
    newSection = Integer.new(last.intValue + 1) 
    @sections.add(newSection)
  end
  
  def visit(p: Paragraph):void # 
    iterate(p.getContent())
    newline(2)
  end
  
  def visit(hr: HorizontalRule):void

    newline(1)
    write(StringUtils.strrep('-', @wrapCol))
    newline(2)
  end
  
  def visit(e: XmlElement):void

    if (e.getName().equalsIgnoreCase("br"))
      newline(1)
    else
      iterate(e.getBody())
    end
  end
  
  # =========================================================================
  # Stuff we want to hide
  
  def visit(n: ImageLink) # 
    returns void
  end
  
  def visit(n: IllegalCodePoint):void
  end
  
  def visit(n: XmlComment):void
  end
  
  def visit(n: Template):void
  end
  
  def visit(n: TemplateArgument):void
  end
  
  def visit(n: TemplateParameter):void
  end
  
  def visit(n: TagExtension):void
  end
  
  def visit(n: MagicWord):void
  end
  
  # =========================================================================
  
  private 
  
  def newline(num: int):void

    if (@pastBod)
      if (num > @needNewlines)
        @needNewlines = num
      end
    end
  end
  
  def wantSpace():void
    
    if (@pastBod) 
      (@needSpace = true) 
    end
  end
  
  def finishLine():void

    @sb.append(@line.toString())
    @line.setLength(0)
  end
  
  def writeNewlines(num: int):void

    finishLine()
    @sb.append(StringUtils.strrep("\n", num))
    
    @needNewlines = 0
    @needSpace = false
  end
  
  def write(cs: char[]):void
    write(String.valueOf(cs))
  end
  
  def write(ch: char):void
    writeWord(String.valueOf(ch))
  end
  
  def write(num: int):void
    writeWord(String.valueOf(num))
  end
  
  def writeWord(s: String):void

    length = s.length()
    if (length == 0)
      return 
    end

    if ((not @noWrap) and (@needNewlines <= 0)) then
      if (@needSpace) then
        length = (length + 1)
      end
      
      if (((@line.length() + length) >= @wrapCol) and (@line.length() > 0))
        writeNewlines(1) 
      end
      
    end
      
    if (@needSpace && @needNewlines <= 0)
      @line.append(' ') 
    end
    
    if (@needNewlines > 0) 
      writeNewlines(@needNewlines) 
    end
      
    @needSpace = false
    @pastBod = true
    
    @line.append(s)
  end
  
  def write(s: String):void
    
    if s.isEmpty()
      return
    end
    
    if Character.isSpaceChar(s.charAt(0))
      wantSpace() 
    end
    
    words = TextConverter.split_pattern.split(s)

    i = 0
    for word in words
      writeWord(word)
      if (i + 1 < words.length)
        wantSpace()
      end
      
      i = i + 1
    end    
    
    if Character.isSpaceChar(s.charAt(s.length() - 1))
      wantSpace() 
    end
  end
  
end