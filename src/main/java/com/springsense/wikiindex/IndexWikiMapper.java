// Generated from index_wiki_mapper.mirah
package com.springsense.wikiindex;

import java.io.IOException;

public class IndexWikiMapper extends org.apache.hadoop.mapred.MapReduceBase
		implements org.apache.hadoop.mapred.Mapper {
	private com.springsense.disambig.DisambiguatorFactory disambiguator_factory;
	private com.springsense.wikiindex.ArticleRenderer article_renderer;
	private java.lang.ThreadLocal disambiguator_store;

	public IndexWikiMapper(
			com.springsense.disambig.DisambiguatorFactory disambiguator_factory) {
		this.disambiguator_factory = disambiguator_factory;
	}

	public void map(java.lang.Object key, java.lang.Object p,
			org.apache.hadoop.mapred.OutputCollector output,
			org.apache.hadoop.mapred.Reporter reporter) {
		edu.umd.cloud9.collection.wikipedia.WikipediaPage w = null;
		com.springsense.wikiindex.Article article = null;
		java.lang.String article_text = null;
		com.springsense.disambig.SentenceDisambiguationResult[] result = null;
		w = ((edu.umd.cloud9.collection.wikipedia.WikipediaPage) (p));
		article = new com.springsense.wikiindex.Article(-1, w.getTitle(),
				w.getWikiMarkup());
		article_text = this.article_renderer().render_as_text(article);
		result = this.disambiguator().disambiguateText(article_text, 3, false,
				true, false);
		org.apache.hadoop.mapred.OutputCollector temp$1 = output;
		try {
			temp$1.collect(new org.apache.hadoop.io.Text(w.getTitle()),
					new org.apache.hadoop.io.Text(article_text));
		} catch (IOException e) {
			throw new RuntimeException("Could not output due to an error", e);
		}
	}

	public com.springsense.wikiindex.ArticleRenderer article_renderer() {
		if ((this.article_renderer != null)) {
			return this.article_renderer;
		} else {
			return this.article_renderer = new com.springsense.wikiindex.ArticleRenderer();
		}
	}

	public com.springsense.disambig.Disambiguator disambiguator() {
		return ((com.springsense.disambig.Disambiguator) (this
				.disambiguator_store().get()));
	}

	public java.lang.ThreadLocal disambiguator_store() {
		if ((this.disambiguator_store == null)) {
			this.disambiguator_store = new java.lang.ThreadLocal();
			java.lang.ThreadLocal temp$1 = this.disambiguator_store;
			try {
				temp$1.set(this.disambiguator_factory.openNewDisambiguator());
			} catch (IOException e) {
				throw new RuntimeException(
						"Could not create new disambiguator due to an error", e);
			}
		}
		return this.disambiguator_store;
	}
}
