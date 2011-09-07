package com.springsense.wikiindex

import java.io.StringWriter

import org.sweble.wikitext.engine.Compiler
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration
import org.sweble.wikitext.engine.PageId
import org.sweble.wikitext.engine.PageTitle
import org.sweble.wikitext.engine.utils.TextConverter

class ArticleRenderer

  def config:SimpleWikiConfiguration
    @config ||= SimpleWikiConfiguration.new(
		        "classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
  end
  
  def compiler:Compiler
    @compiler ||= Compiler.new(config)
  end
  
  def render_as_text(article: Article)
    render_as_text(article.id, article.title, article.wikitext)
  end
  
  def render_as_text(id: int, title: string, wikitext: string):string
		pageTitle = PageTitle.make(config, title)
		pageId = PageId.new(pageTitle, id)

		cp = compiler.postprocess(pageId, wikitext, nil)
    w = StringWriter.new()
    
  	# p = HtmlPrinter.new(w, pageTitle.getFullTitle)
		# p.setCssResource("/org/sweble/wikitext/engine/utils/HtmlPrinter.css", "")
		# p.setStandaloneHtml(true, "")
		# p.go(cp.getPage)
    
  	TextConverter p = TextConverter.new(config, 80)
		p.go(cp.getPage).toString
  end
  
end
