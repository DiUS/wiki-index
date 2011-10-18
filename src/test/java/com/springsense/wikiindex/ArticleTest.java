package com.springsense.wikiindex;

import static com.springsense.wikiindex.TestUtils.loadTestResourceAsString;
import static com.springsense.wikiindex.TestUtils.loadTestWikipediaPage;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ArticleTest {
	private long id;
	private String title;
	private String articleWikitext;
	private Article article;

	@Before()
	public void setup() throws Exception {
		id = -1;
	}

	@Test()
	public void articleShouldInstantiateCorrectlyFromWikipediaPage() throws IOException {
		article = new Article(id, loadTestWikipediaPage("test-article.xml"));

		assertThat(article.id(), equalTo(id));
		assertThat(article.getTitle(), equalTo("Red Army invasion of Azerbaijan"));
		assertThat(article.getWikitext(), equalTo(loadTestResourceAsString("test-article-red-army-invasion.wikitext")));
		assertThat(article.isRedirect(), equalTo(false));
		assertThat(article.isArticle(), equalTo(true));
	}
	
	@Test()
	public void articleShouldInstantiateCorrectlyFromRedirectPage() throws IOException {
		article = new Article(id, loadTestWikipediaPage("test-redirect-article.xml"));
		
		assertThat(article.id(), equalTo(id));
		assertThat(article.getTitle(), equalTo("Batman: Arkham City (comic book)"));
		assertThat(article.getWikitext(), equalTo("#REDIRECT [[Batman: Arkham City (comics)]]"));
		assertThat(article.isRedirect(), equalTo(true));
		assertThat(article.isArticle(), equalTo(true));
	}
}
