package com.springsense.wikiindex;

public class Article {

	private long id;
	private String title;
	private String wikitext;

	public Article(long id, String title, String wikitext) {
		this.id = id;
		this.title = title;
		this.wikitext = wikitext;
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
}
