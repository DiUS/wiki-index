// Generated from article.mirah
package com.springsense.wikiindex;

public class Article {
  
  private int id;
  private java.lang.String title;
  private java.lang.String wikitext;
  
  public  Article(int id, java.lang.String title, java.lang.String wikitext) {
    this.id = id;
    this.title = title;
    this.wikitext = wikitext;
  }
  public int id() {
    return this.id;
  }
  public java.lang.String title() {
    return this.title;
  }
  public java.lang.String wikitext() {
    return this.wikitext;
  }
}
