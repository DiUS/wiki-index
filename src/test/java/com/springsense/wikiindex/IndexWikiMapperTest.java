// Generated from index_wiki_mapper_test.mirah
package com.springsense.wikiindex;

import java.io.IOException;

public class IndexWikiMapperTest extends java.lang.Object {
	private com.springsense.disambig.DisambiguatorFactory mock_disambiguator_factory;
	private com.springsense.disambig.Disambiguator mock_disambiguator;
	private com.springsense.wikiindex.IndexWikiMapper mapper;
	private org.apache.hadoop.mrunit.MapDriver driver;
	private edu.umd.cloud9.collection.wikipedia.WikipediaPage page;

	@org.junit.Before()
	public void setUp() throws Exception {
		this.mock_disambiguator_factory = ((com.springsense.disambig.DisambiguatorFactory) (org.mockito.Mockito
				.mock(com.springsense.disambig.DisambiguatorFactory.class)));
		this.mock_disambiguator = ((com.springsense.disambig.Disambiguator) (org.mockito.Mockito
				.mock(com.springsense.disambig.Disambiguator.class)));
		org.mockito.Mockito.when(
				this.mock_disambiguator_factory.openNewDisambiguator())
				.thenReturn(this.mock_disambiguator);
		this.mapper = new com.springsense.wikiindex.IndexWikiMapper(
				this.mock_disambiguator_factory);
		this.driver = new org.apache.hadoop.mrunit.MapDriver(this.mapper);
		this.page = new edu.umd.cloud9.collection.wikipedia.WikipediaPage();
		edu.umd.cloud9.collection.wikipedia.WikipediaPage.readPage(
				this.page,
				org.apache.commons.io.IOUtils.toString(this.getClass()
						.getClassLoader()
						.getResourceAsStream("test-article.xml")));
	}

	@org.junit.Test()
	public void mapper_should_return_the_correct_values() throws IOException {
		java.util.List output = null;
		java.lang.String article_text = null;

		org.apache.hadoop.mrunit.MapDriver temp$1 = this.driver;

		temp$1.setInput(new org.apache.hadoop.io.LongWritable(1024), this.page);

		output = this.driver.run();
		article_text = org.apache.commons.io.IOUtils.toString(this.getClass()
				.getClassLoader()
				.getResourceAsStream("test-article-red-army.wikitext"));
		org.junit.Assert.assertEquals(new org.apache.hadoop.io.Text(
				"Red Army invasion of Azerbaijan"),
				((org.apache.hadoop.mrunit.types.Pair) (output.get(0)))
						.getFirst());
		org.junit.Assert.assertEquals(new org.apache.hadoop.io.Text(
				article_text), ((org.apache.hadoop.mrunit.types.Pair) (output
				.get(0))).getSecond());
		((com.springsense.disambig.Disambiguator) (org.mockito.Mockito
				.verify(this.mock_disambiguator))).disambiguateText(
				article_text, 3, false, true, false);
	}
}
