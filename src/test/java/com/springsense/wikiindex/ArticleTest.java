package com.springsense.wikiindex;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ArticleTest {
	private int id;
	private String title;
	private String articleWikitext;
	private Article article;

	@Before()
	public void setup() throws Exception {
		id = -1;
		title = "Fake title";
		articleWikitext = IOUtils.toString(getClass().getClassLoader()
				.getResourceAsStream("test-article.wikitext"));
		article = new Article(id, title, articleWikitext);
	}

	@Test()
	public void articleShouldInstantiateCorrectly() {
		assertThat(article.id(), equalTo(new Integer(id)));
		assertThat(article.title(), equalTo(title));
		assertThat(article.wikitext(), equalTo(articleWikitext));
	}
}
