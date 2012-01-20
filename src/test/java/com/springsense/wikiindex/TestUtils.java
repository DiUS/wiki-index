package com.springsense.wikiindex;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public abstract class TestUtils {
	protected static WikipediaPage loadTestWikipediaPage(String pageXmlFilename) throws IOException {
		WikipediaPage page = new WikipediaPage();
		WikipediaPage.readPage(page, loadTestResourceAsString(pageXmlFilename));
		return page;
	}

	protected static String loadTestResourceAsString(String testResourceName) throws IOException {
		return IOUtils.toString(IndexWikiMapperTest.class.getClassLoader().getResourceAsStream(testResourceName), "UTF-8");
	}

}
