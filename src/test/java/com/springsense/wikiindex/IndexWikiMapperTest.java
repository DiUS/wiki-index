package com.springsense.wikiindex;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.MapDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

import com.springsense.disambig.Disambiguator;
import com.springsense.disambig.DisambiguatorFactory;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public class IndexWikiMapperTest extends java.lang.Object {
	private DisambiguatorFactory mockDisambiguatorFactory;
	private Disambiguator mockDisambiguator;
	private IndexWikiMapper mapper;
	private MapDriver driver;
	private WikipediaPage page;

	@Before()
	public void setUp() throws Exception {
		mockDisambiguatorFactory = mock(DisambiguatorFactory.class);
		mockDisambiguator = mock(Disambiguator.class);
		when(this.mockDisambiguatorFactory.openNewDisambiguator()).thenReturn(
				this.mockDisambiguator);
		
		this.mapper = new IndexWikiMapper(this.mockDisambiguatorFactory);
		this.driver = new MapDriver(this.mapper);
		this.page = new WikipediaPage();
		
		WikipediaPage.readPage(
				this.page,
				IOUtils.toString(this.getClass().getClassLoader()
						.getResourceAsStream("test-article.xml")));
	}

	@Test()
	public void mapperShouldReturnTheCorrectValues() throws IOException {
		List output = null;
		String articleText = null;

		driver.setInput(new LongWritable(1024), this.page);

		output = driver.run();

		articleText = IOUtils.toString(this.getClass().getClassLoader()
				.getResourceAsStream("test-article-red-army.wikitext"));
		
		assertEquals(new Text(
				"Red Army invasion of Azerbaijan"),
				((Pair) (output.get(0))).getFirst());
		assertEquals(new Text(articleText),
				((Pair) (output.get(0))).getSecond());
		verify(this.mockDisambiguator).disambiguateText(articleText, 3, false,
				true, false);
	}
}
