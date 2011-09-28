// Generated from article_renderer_test.mirah
package com.springsense.wikiindex;

import java.io.IOException;

public class ArticleRendererTest extends java.lang.Object {
  private com.springsense.wikiindex.ArticleRenderer article_renderer;
  private com.springsense.wikiindex.Article article;
  @org.junit.Before()
  public void setup() throws Exception {
    this.article_renderer = new com.springsense.wikiindex.ArticleRenderer();
    this.article = new com.springsense.wikiindex.Article(-1, "Anthropology", org.apache.commons.io.IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("test-article.wikitext")));
  }
  @org.junit.Test()
  public void test_text_should_return_article_content_as_text() {
    java.lang.String rendered_text = null;
    rendered_text = this.article_renderer.render_as_text(this.article);
    org.junit.Assert.assertThat(rendered_text, org.hamcrest.Matchers.containsString("Biological anthropology, or physical anthropology, focuses on the study"));
    org.junit.Assert.assertThat(rendered_text, org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<UNSUPPORTED")));
  }
}
