package com.springsense.wikiindex;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ArticleRendererTest {
	private ArticleRenderer articleRenderer;
	private Article article;

	@Before()
	public void setup() throws Exception {
		articleRenderer = new ArticleRenderer();
		
		article = new Article(-1, "Anthropology", IOUtils.toString(getClass()
				.getClassLoader().getResourceAsStream("test-article.wikitext")));
	}

	@Test()
	public void testTextShouldReturnArticleContentAsText() {
		String renderedText = null;
		renderedText = articleRenderer.renderAsText(article);
		
		assertThat(
				renderedText,
				containsString("Biological anthropology, or physical anthropology, focuses on the study"));
		assertThat(renderedText, not(containsString("<UNSUPPORTED")));
	}
}
