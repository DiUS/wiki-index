package com.springsense.wikiindex;

import static com.springsense.wikiindex.TestUtils.loadTestResourceAsString;
import static com.springsense.wikiindex.TestUtils.loadTestWikipediaPage;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
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
	private Mapper<LongWritable, WikipediaPage, Text, JSONObjectWritable>.Context context;
	private final LongWritable key = new LongWritable(1024);

	@SuppressWarnings("unchecked")
	@Before()
	public void setUp() throws Exception {
		context = mock(Context.class);
		mockDisambiguatorFactory = mock(DisambiguatorFactory.class);
		mockDisambiguator = mock(Disambiguator.class);
		when(mockDisambiguatorFactory.openNewDisambiguator()).thenReturn(mockDisambiguator);

		mapper = new IndexWikiMapper(mockDisambiguatorFactory);
		driver = new MapDriver<LongWritable, WikipediaPage, Text, JSONObjectWritable>(mapper);

		page = new WikipediaPage();

		WikipediaPage.readPage(page, IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("test-article.xml")));
	}

	@Test()
	public void mapperShouldOutputTheMappedDocumentWithCorrectKeyWhenReturned() throws IOException, JSONException {
		WikipediaPage page = loadTestWikipediaPage("test-article.xml");
		driver.setInput(key, page);

		List<Pair<Text, JSONObjectWritable>> output = driver.run();

		assertThat(output.size(), equalTo(1));

		Pair<Text, JSONObjectWritable> firstPair = output.get(0);
		JSONObjectWritable document = firstPair.getSecond();

		assertThat(firstPair.getFirst(), equalTo(new Text("Red Army invasion of Azerbaijan")));
		assertThat((String) document.get("key"), equalTo("Red Army invasion of Azerbaijan"));
		assertThat((String) document.get("title"), equalTo("Red Army invasion of Azerbaijan"));
		assertThat(((String) document.get("content")).getBytes(), equalTo(loadTestResourceAsString("test-article-red-army-invasion.text").getBytes()));
	}

	@Test()
	public void mapperShouldOutputTheMappedDocumentWithCorrectKeyWhenIsRedirect() throws IOException, JSONException {
		WikipediaPage page = loadTestWikipediaPage("test-redirect-article.xml");
		driver.setInput(key, page);

		List<Pair<Text, JSONObjectWritable>> output = driver.run();

		assertThat(output.size(), equalTo(1));

		Pair<Text, JSONObjectWritable> firstPair = output.get(0);
		JSONObjectWritable document = firstPair.getSecond();

		assertThat(firstPair.getFirst(), equalTo(new Text((String) document.get("key"))));
		assertThat((String) document.get("key"), equalTo("Batman: Arkham City (comics)"));
		assertThat((String) document.get("title"), equalTo("Batman: Arkham City (comic book)"));
		assertThat((Boolean) document.get("disambiguation"), equalTo(Boolean.FALSE));
		assertThat(document.opt("content"), nullValue());
	}

	@Test()
	public void mapperShouldOutputTheMappedDocumentWithCorrectAttributeWhenDisambiguationPage() throws IOException, JSONException {
		WikipediaPage page = loadTestWikipediaPage("test-article-alien.xml");
		driver.setInput(key, page);

		List<Pair<Text, JSONObjectWritable>> output = driver.run();

		assertThat(output.size(), equalTo(1));

		Pair<Text, JSONObjectWritable> firstPair = output.get(0);
		JSONObjectWritable document = firstPair.getSecond();

		assertThat((Boolean) document.get("disambiguation"), equalTo(Boolean.TRUE));
	}

	@Test()
	public void mapperShouldNotHaveOutputForUnprocessedDocuments() throws IOException, JSONException {
		WikipediaPage page = loadTestWikipediaPage("test-template-article.xml");
		driver.setInput(key, page);

		List<Pair<Text, JSONObjectWritable>> output = driver.run();

		assertThat(output.size(), equalTo(0));
	}

	@Test
	public void processWikipediaPageToDocumentShouldProcessNormalArticleCorrectly() throws Exception {
		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-article.xml"));

		assertThat((String) document.get("key"), equalTo("Red Army invasion of Azerbaijan"));
		assertThat((String) document.get("title"), equalTo("Red Army invasion of Azerbaijan"));
		assertThat((String) document.get("content"), equalTo(loadTestResourceAsString("test-article-red-army-invasion.text")));

		verify(this.mockDisambiguator).disambiguateText(loadTestResourceAsString("test-article-red-army-invasion.text"), 3, false, true, false);
	}

	@Test
	public void processWikipediaPageToDocumentShouldProcessArticleWithAnnotateErrorCorrectly() throws Exception {
		ArticleAnnotator failingArticleAnnotator = mock(ArticleAnnotator.class);
		doThrow(new RuntimeException("Problem annotating article")).when(failingArticleAnnotator).annotate((Article)anyObject());
		
		mapper.setArticleAnnotator(failingArticleAnnotator);
		
		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-article-fscked.xml"));
		
		assertThat((String) document.get("errors"), containsString("Problem annotating article"));
	}

	@Test
	public void processWikipediaPageToDocumentShouldProcessArticleWithDisambigErrorCorrectly() throws Exception {
		String islamismText = loadTestResourceAsString("test-article-islamism.text");
		when(this.mockDisambiguator.disambiguateText(islamismText, 3, false, true, false)).thenThrow(new RuntimeException("Error disambiguating text"));

		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-article-islamism.xml"));

		verify(this.mockDisambiguator).disambiguateText("Islamism", 3, false, true, false);
		verify(this.mockDisambiguator).disambiguateText(islamismText, 3, false, true, false);
		
		assertThat((String) document.get("key"), equalTo("Islamism"));
		assertThat((String) document.get("title"), equalTo("Islamism"));
		
		assertThat((String) document.get("errors"), containsString("Error disambiguating text"));
		
	}

	@Test
	public void processWikipediaPageToDocumentShouldProcessRedirectArticleCorrectly() throws Exception {
		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-redirect-article.xml"));

		assertThat((String) document.get("key"), equalTo("Batman: Arkham City (comics)"));
		assertThat((String) document.get("title"), equalTo("Batman: Arkham City (comic book)"));
		assertThat(document.opt("content"), nullValue());
	}

	@Test
	public void processWikipediaPageToDocumentShouldReturnNullForTemplateArticle() throws Exception {
		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-template-article.xml"));

		assertThat(document, nullValue());
	}

	@Test
	public void processWikipediaPageToDocumentShouldReturnNullForFileArticle() throws Exception {
		JSONObjectWritable document = mapper.processWikipediaPageToDocument(context, key, loadTestWikipediaPage("test-file-article.xml"));

		assertThat(document, nullValue());
	}

}
