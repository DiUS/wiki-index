package com.springsense.wikiindex;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import com.springsense.disambig.Disambiguator;
import com.springsense.disambig.DisambiguatorFactory;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.umd.cloud9.io.JSONObjectWritable;

public class IndexWikiMapperTest extends java.lang.Object {
	private DisambiguatorFactory mockDisambiguatorFactory;
	private Disambiguator mockDisambiguator;

	private IndexWikiMapper mapper;
	private MapDriver<LongWritable, WikipediaPage, Text, JSONObjectWritable> driver;
	private WikipediaPage page;

	@Before()
	public void setUp() throws Exception {
		mockDisambiguatorFactory = mock(DisambiguatorFactory.class);
		mockDisambiguator = mock(Disambiguator.class);
		when(mockDisambiguatorFactory.openNewDisambiguator()).thenReturn(
				mockDisambiguator);

		mapper = new IndexWikiMapper(mockDisambiguatorFactory);
		driver = new MapDriver<LongWritable, WikipediaPage, Text, JSONObjectWritable>(
				mapper);

		page = new WikipediaPage();

		WikipediaPage.readPage(
				page,
				IOUtils.toString(this.getClass().getClassLoader()
						.getResourceAsStream("test-article.xml")));
	}

	@Test()
	public void mapperShouldReturnTheCorrectValues() throws IOException,
			JSONException {
		String expectedTitle = "Red Army invasion of Azerbaijan";
		String expectedContent = IOUtils.toString(this.getClass()
				.getClassLoader()
				.getResourceAsStream("test-article-red-army.wikitext"));


		driver.setInput(new LongWritable(1024), page);
		List<Pair<Text, JSONObjectWritable>> output = driver.run();

		assertThat(output.size(), equalTo(1));

		Pair<Text, JSONObjectWritable> firstPair = output.get(0);
		JSONObjectWritable actualDocument = firstPair.getSecond();

		assertThat(firstPair.getFirst(), equalTo(new Text(expectedTitle)));

		Object actualTitle = actualDocument.get("title");
		assertThat(expectedTitle, equalTo(actualTitle));
		assertThat(expectedContent, equalTo(actualDocument.get("content")));

		verify(this.mockDisambiguator).disambiguateText(expectedContent, 3,
				false, true, false);
	}
}
