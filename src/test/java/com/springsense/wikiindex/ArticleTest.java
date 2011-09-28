// Generated from article_test.mirah
package com.springsense.wikiindex;

import java.io.IOException;

public class ArticleTest extends java.lang.Object {
	private int id;
	private java.lang.String title;
	private java.lang.String articleWikitext;
	private com.springsense.wikiindex.Article article;

	@org.junit.Before()
	public void setup() throws Exception {
		this.id = -1;
		this.title = "Fake title";
		this.articleWikitext = org.apache.commons.io.IOUtils.toString(this
				.getClass().getClassLoader()
				.getResourceAsStream("test-article.wikitext"));
		this.article = new com.springsense.wikiindex.Article(this.id,
				this.title, this.articleWikitext);
	}

	@org.junit.Test()
	public void article_should_instantiate_correctly() {
		org.junit.Assert.assertThat(new java.lang.Integer(this.article.id()),
				org.hamcrest.Matchers.equalTo(new java.lang.Integer(this.id)));
		org.junit.Assert.assertThat(this.article.title(),
				org.hamcrest.Matchers.equalTo(this.title));
		org.junit.Assert.assertThat(this.article.wikitext(),
				org.hamcrest.Matchers.equalTo(this.articleWikitext));
	}
}
