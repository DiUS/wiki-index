package com.springsense.wikiindex;

import static com.springsense.wikiindex.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ArticleAnnotatorTest {
	private ArticleAnnotator articleRenderer;

	@Before()
	public void setup() throws Exception {
		articleRenderer = new ArticleAnnotator();

	}

	@Test()
	public void testTextShouldReturnArticleContentAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText.replaceAll("\\s", ""), equalTo(loadTestResourceAsString("test-article-red-army-invasion.text").replaceAll("\\s", "")));
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}

	@Test()
	public void testTextShouldReturnArticleContentWithDefinitionListAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-transportation-in-angola.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();

		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}

	@Test()
	public void testTextShouldReturnArticleContentWithXmlElementsAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-abacus.xml"));
		
		//System.out.println(article.getWikitext());

		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test()
	public void testTextShouldReturnArticleContentWithDefinitionTermAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-aberdeen.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test()
	public void testTextShouldReturnArticleContentWithXmlAttrGarbageAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-adventure.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test()
	public void testTextShouldReturnArticleContentWithEnumerationAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-aegean-sea.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test()
	public void testTextShouldReturnArticleContentWithTableAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-the-letter-a.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}

	@Test()
	public void testTextShouldReturnArticleContentWithTable2AsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-albedo.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test()
	public void testTextShouldReturnRedirectArticleContentAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-redirect-article.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();
		
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
		assertThat(renderedText, equalTo("<span class='redirect'>Batman: Arkham City (comics)</span>"));
		
		assertThat(article.isRedirect(), equalTo(true));
		assertThat(article.getRedirectTarget(), equalTo("Batman: Arkham City (comics)"));
	}
}
