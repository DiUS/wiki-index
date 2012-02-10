package com.springsense.wikiindex;

import org.apache.commons.lang.StringEscapeUtils;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

public class Article {

	private long id;
	private String title;
	private String wikitext;
	private String text;
	private boolean isRedirect;
	private String redirectTarget;
	
	private WikipediaPage wikipediaPage;

	public Article(long id, WikipediaPage wikipediaPage) {
		this.id = id;
		this.wikipediaPage = wikipediaPage;
		this.title = wikipediaPage.getTitle();
		this.wikitext = StringEscapeUtils.unescapeHtml(wikipediaPage.getWikiMarkup());
		this.text = null;
		this.isRedirect = wikipediaPage.isRedirect();
	}

	public long id() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getWikitext() {
		return wikitext;
	}

	public String getText() {
		return text;
	}

	protected void setText(String text) {
		this.text = text;
	}

	public boolean isRedirect() {
		return isRedirect;
	}

	protected void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public boolean isArticle() {
		return wikipediaPage.isArticle();
	}

	public boolean isDisambiguation() {
		return wikipediaPage.isDisambiguation();
	}
	
	public String getRedirectTarget() {
		return redirectTarget;
	}

	protected void setRedirectTarget(String redirectTarget) {
		this.redirectTarget = redirectTarget;
	}

}
