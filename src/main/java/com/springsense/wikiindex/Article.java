package com.springsense.wikiindex;

public class Article {

	private int id;
	private String title;
	private String wikitext;

	public Article(int id, String title, String wikitext) {
		this.id = id;
		this.title = title;
		this.wikitext = wikitext;
	}

	public int id() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getWikitext() {
		return this.wikitext;
	}
}
