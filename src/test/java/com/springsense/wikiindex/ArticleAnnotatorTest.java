package com.springsense.wikiindex;

import static com.springsense.wikiindex.TestUtils.loadTestResourceAsString;
import static com.springsense.wikiindex.TestUtils.loadTestWikipediaPage;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
	public void testTextShouldReturnArticleContentCorrectly2() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-islamism.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();

		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
	
	@Test(expected=RuntimeException.class)
	public void testTextShouldHandleSeriouslyMalformedWikitextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-jefferson-disk.xml"));
		
		articleRenderer.annotate(article);
	}
	
	@Test()
	public void testTextShouldReturnArticleContentCorrectlyWithComplexDefinitions() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-mark-antony.xml"));
		
		articleRenderer.annotate(article);
		String renderedText = article.getText();

		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
		
		assertThat(renderedText.length(), lessThan(50000));
		
		assertThat(renderedText.replaceAll("\\s", ""), equalTo(loadTestResourceAsString("test-article-mark-antony.text").replaceAll("\\s", "")));
	}

	@Test()
	public void testTextShouldReturnArticleContentWithXmlElementsAsTextCorrectly() throws IOException {
		Article article = new Article(-1, loadTestWikipediaPage("test-article-abacus.xml"));
		
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
