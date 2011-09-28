// Generated from TextConverter.mirah
package org.sweble.wikitext.engine.utils;
public class TextConverter extends de.fau.cs.osr.ptk.common.Visitor {
  private static java.util.regex.Pattern split_pattern;
  private org.sweble.wikitext.engine.utils.SimpleWikiConfiguration config;
  private int wrapCol;
  private java.lang.StringBuilder sb;
  private java.lang.StringBuilder line;
  private int extLinkNum;
  private boolean pastBod;
  private int needNewlines;
  private boolean needSpace;
  private boolean noWrap;
  private java.util.LinkedList sections;
  private java.lang.StringBuilder sb2;

public static java.util.regex.Pattern split_pattern() {
    if ((TextConverter.split_pattern != null)) {
      return TextConverter.split_pattern;
    }
    else {
      return TextConverter.split_pattern = java.util.regex.Pattern.compile("\\s+");
    }
  }
  public  TextConverter(org.sweble.wikitext.engine.utils.SimpleWikiConfiguration config, int wrapCol) {
    this.config = config;
    this.wrapCol = wrapCol;
  }
  protected boolean before(de.fau.cs.osr.ptk.common.ast.AstNode node) {
    this.sb = new java.lang.StringBuilder();
    this.line = new java.lang.StringBuilder();
    this.extLinkNum = 1;
    this.pastBod = false;
    this.needNewlines = 0;
    this.needSpace = false;
    this.noWrap = false;
    this.sections = new java.util.LinkedList();
    return super.before(node);
  }
  protected java.lang.Object after(de.fau.cs.osr.ptk.common.ast.AstNode node, java.lang.Object result) {
    this.finishLine();
    return this.sb.toString();
  }
  public void visit(de.fau.cs.osr.ptk.common.ast.AstNode n) {
    this.write("<UNSUPPORTED ");
    this.write(n.getNodeName());
    this.write(" />");
  }
  public void visit(org.sweble.wikitext.lazy.parser.SemiPre s) {
    this.iterate(s);
  }
  public void visit(org.sweble.wikitext.lazy.parser.SemiPreLine l) {
    this.iterate(l.getContent());
    this.newline(1);
  }
  public void visit(org.sweble.wikitext.lazy.parser.Itemization i) {
    this.iterate(i);
  }
  public void visit(org.sweble.wikitext.lazy.parser.ItemizationItem i) {
    this.write("\t* ");
    this.iterate(i.getContent());
    this.newline(1);
  }
  public void visit(de.fau.cs.osr.ptk.common.ast.NodeList n) {
    this.iterate(n);
  }
  public void visit(org.sweble.wikitext.engine.Page p) {
    this.iterate(p.getContent());
  }
  public void visit(de.fau.cs.osr.ptk.common.ast.Text text) {
    this.write(text.getContent());
  }
  public void visit(org.sweble.wikitext.lazy.parser.Whitespace w) {
    this.write(" ");
  }
  public void visit(org.sweble.wikitext.lazy.parser.Bold b) {
    this.iterate(b.getContent());
  }
  public void visit(org.sweble.wikitext.lazy.parser.Italics i) {
    this.iterate(i.getContent());
  }
  public void visit(org.sweble.wikitext.lazy.utils.XmlCharRef cr) {
    this.write(java.lang.Character.toChars(cr.getCodePoint()));
  }
  public void visit(org.sweble.wikitext.lazy.utils.XmlEntityRef er) {
    java.lang.String ch = null;
    ch = org.sweble.wikitext.engine.utils.EntityReferences.resolve(er.getName());
    if ((ch == null)) {
      this.write("&");
      this.write(er.getName());
      this.write(";");
    }
    else {
      this.write(ch);
    }
  }
  public void visit(org.sweble.wikitext.lazy.parser.Url url) {
    this.write(url.getProtocol());
    this.write(":");
    this.write(url.getPath());
  }
  public void visit(org.sweble.wikitext.lazy.parser.ExternalLink link) {
    this.write("[");
    int temp$1 = 0;
    temp$1 = this.extLinkNum = (this.extLinkNum + 1);
    this.write(temp$1);
    this.write("]");
  }
  public void visit(org.sweble.wikitext.lazy.parser.InternalLink link) {
    org.sweble.wikitext.engine.PageTitle page = null;
    boolean __xform_tmp_1 = false;
    try {
      page = org.sweble.wikitext.engine.PageTitle.make(this.config, link.getTarget());
      if (page.getNamespace().equals(this.config.getNamespace("Category"))) {
        return;
      }
    }
    catch (org.sweble.wikitext.lazy.LinkTargetException e$2000) {
    }
    this.write(link.getPrefix());
    boolean temp$1 = false;
    __xform_tmp_1 = (link.getTitle().getContent() == null);
    temp$1 = __xform_tmp_1 ? (__xform_tmp_1) : (link.getTitle().getContent().isEmpty());
    if (temp$1) {
      this.write(link.getTarget());
    }
    else {
      this.iterate(link.getTitle());
    }
    this.write(link.getPostfix());
  }
  public void visit(org.sweble.wikitext.lazy.parser.Section s) {
    java.lang.StringBuilder savesb = null;
    boolean saveNoWrap = false;
    java.lang.String title = null;
    int size = 0;
    int i = 0;
    java.lang.Integer last = null;
    java.lang.Integer newSection = null;
    java.util.Iterator __xform_tmp_2 = null;
    java.lang.Object section = null;
    this.finishLine();
    savesb = this.sb;
    saveNoWrap = this.noWrap;
    this.sb = new java.lang.StringBuilder();
    this.noWrap = true;
    this.iterate(s.getTitle());
    this.finishLine();
    title = this.sb.toString().trim();
    this.sb = savesb;
    if ((s.getLevel() >= 1)) {
      label1:
      while ((this.sections.size() > s.getLevel())) {
        label2:
         {
          this.sections.removeLast();
        }
      }
      label3:
      while ((this.sections.size() < s.getLevel())) {
        label4:
         {
          this.sections.add(new java.lang.Integer(1));
        }
      }
      java.lang.StringBuilder temp$5 = null;
      temp$5 = this.sb2 = new java.lang.StringBuilder();

      size = this.sections.size();
      i = 0;
      __xform_tmp_2 = this.sections.iterator();
      label6:
      while (__xform_tmp_2.hasNext()) {
        section = __xform_tmp_2.next();
        label7:
         {
          if ((i >= 1)) {
            this.sb2.append(section);
            this.sb2.append(".");
          }
          i = (i + 1);
        }
      }
      if ((this.sb2.length() > 0)) {
        this.sb2.append(" ");
      }
      this.sb2.append(title);
      title = this.sb2.toString();
    }
    this.newline(2);
    this.write(title);
    this.newline(1);
    this.write(de.fau.cs.osr.utils.StringUtils.strrep("-", title.length()));
    this.newline(2);
    this.noWrap = saveNoWrap;
    this.iterate(s.getBody());
    label8:
    while ((this.sections.size() > s.getLevel())) {
      label9:
       {
        this.sections.removeLast();
      }
    }
    last = ((java.lang.Integer)(this.sections.removeLast()));
    newSection = new java.lang.Integer((last.intValue() + 1));
    this.sections.add(newSection);
  }
  public void visit(org.sweble.wikitext.lazy.parser.Paragraph p) {
    this.iterate(p.getContent());
    this.newline(2);
  }
  public void visit(org.sweble.wikitext.lazy.parser.HorizontalRule hr) {
    this.newline(1);
    this.write(de.fau.cs.osr.utils.StringUtils.strrep("-", this.wrapCol));
    this.newline(2);
  }
  public void visit(org.sweble.wikitext.lazy.parser.XmlElement e) {
    if (e.getName().equalsIgnoreCase("br")) {
      this.newline(1);
    }
    else {
      this.iterate(e.getBody());
    }
  }
  public void visit(org.sweble.wikitext.lazy.parser.ImageLink n) {
  }
  public void visit(org.sweble.wikitext.lazy.encval.IllegalCodePoint n) {
  }
  public void visit(org.sweble.wikitext.lazy.preprocessor.XmlComment n) {
  }
  public void visit(org.sweble.wikitext.lazy.preprocessor.Template n) {
  }
  public void visit(org.sweble.wikitext.lazy.preprocessor.TemplateArgument n) {
  }
  public void visit(org.sweble.wikitext.lazy.preprocessor.TemplateParameter n) {
  }
  public void visit(org.sweble.wikitext.lazy.preprocessor.TagExtension n) {
  }
  public void visit(org.sweble.wikitext.lazy.parser.MagicWord n) {
  }
  private void newline(int num) {
    if (this.pastBod) {
      if ((num > this.needNewlines)) {
        this.needNewlines = num;
      }
    }
  }
  private void wantSpace() {
    if (this.pastBod) {
      this.needSpace = true;
    }
  }
  private void finishLine() {
    this.sb.append(this.line.toString());
    java.lang.StringBuilder temp$1 = this.line;
    temp$1.setLength(0);
  }
  private void writeNewlines(int num) {
    this.finishLine();
    this.sb.append(de.fau.cs.osr.utils.StringUtils.strrep("\n", num));
    this.needNewlines = 0;
    this.needSpace = false;
  }
  private void write(char[] cs) {
    this.write(java.lang.String.valueOf(cs));
  }
  private void write(char ch) {
    this.writeWord(java.lang.String.valueOf(ch));
  }
  private void write(int num) {
    this.writeWord(java.lang.String.valueOf(num));
  }
  private void writeWord(java.lang.String s) {
    int length = 0;
    length = s.length();
    if ((length == 0)) {
      return;
    }
    if (this.noWrap ? (false) : (true) ? ((this.needNewlines <= 0)) : (false)) {
      if (this.needSpace) {
        length = (length + 1);
      }
      if (((this.line.length() + length) >= this.wrapCol) ? ((this.line.length() > 0)) : (false)) {
        this.writeNewlines(1);
      }
    }
    if (this.needSpace ? ((this.needNewlines <= 0)) : (false)) {
      this.line.append(" ");
    }
    if ((this.needNewlines > 0)) {
      this.writeNewlines(this.needNewlines);
    }
    this.needSpace = false;
    this.pastBod = true;
    this.line.append(s);
  }
  private void write(java.lang.String s) {
    java.lang.String[] words = null;
    int i = 0;
    int __xform_tmp_3 = 0;
    java.lang.String[] __xform_tmp_4 = null;
    java.lang.String word = null;
    if (s.isEmpty()) {
      return;
    }
    if (java.lang.Character.isSpaceChar(s.charAt(0))) {
      this.wantSpace();
    }
    words = org.sweble.wikitext.engine.utils.TextConverter.split_pattern().split(s);
    i = 0;
    __xform_tmp_3 = 0;
    __xform_tmp_4 = words;
    label1:
    while ((__xform_tmp_3 < __xform_tmp_4.length)) {
      word = __xform_tmp_4[__xform_tmp_3];
      label2:
       {
        this.writeWord(word);
        if (((i + 1) < words.length)) {
          this.wantSpace();
        }
        i = (i + 1);
      }
      __xform_tmp_3 = (__xform_tmp_3 + 1);
    }
    if (java.lang.Character.isSpaceChar(s.charAt((s.length() - 1)))) {
      this.wantSpace();
    }
  }
}
