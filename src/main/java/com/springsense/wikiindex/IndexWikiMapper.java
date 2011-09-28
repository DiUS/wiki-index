package com.springsense.wikiindex;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.springsense.disambig.Disambiguator;
import com.springsense.disambig.DisambiguatorFactory;

public class IndexWikiMapper extends MapReduceBase
		implements Mapper {
	private DisambiguatorFactory disambiguatorFactory;
	private ArticleRenderer article_renderer;
	private ThreadLocal<Disambiguator> disambiguatorStore;

	public IndexWikiMapper(
			com.springsense.disambig.DisambiguatorFactory disambiguatorFactory) {
		this.disambiguatorFactory = disambiguatorFactory;
	}

	public void map(java.lang.Object key, java.lang.Object p,
			OutputCollector output,
			Reporter reporter) {
		edu.umd.cloud9.collection.wikipedia.WikipediaPage w = null;
		com.springsense.wikiindex.Article article = null;
		java.lang.String article_text = null;
		com.springsense.disambig.SentenceDisambiguationResult[] result = null;
		w = ((edu.umd.cloud9.collection.wikipedia.WikipediaPage) (p));
		article = new com.springsense.wikiindex.Article(-1, w.getTitle(),
				w.getWikiMarkup());
		article_text = getArticleRenderer().renderAsText(article);
		result = getDisambiguator().disambiguateText(article_text, 3, false,
				true, false);
		try {
			output.collect(new Text(w.getTitle()),
					new Text(article_text));
		} catch (IOException e) {
			throw new RuntimeException("Could not output due to an error", e);
		}
	}

	public ArticleRenderer getArticleRenderer() {
		if ((article_renderer != null)) {
			return article_renderer;
		} else {
			return article_renderer = new ArticleRenderer();
		}
	}

	public Disambiguator getDisambiguator() {
		return ((com.springsense.disambig.Disambiguator) (this
				.getDisambiguatorStore().get()));
	}

	public ThreadLocal<Disambiguator> getDisambiguatorStore() {
		if (disambiguatorStore == null) {
			disambiguatorStore = new ThreadLocal<Disambiguator>() {

				@Override
				protected Disambiguator initialValue() {
					try {
						return (disambiguatorFactory.openNewDisambiguator());
					} catch (IOException e) {
						throw new RuntimeException(
								"Could not create new disambiguator due to an error", e);
					}
				}
				
			};
		}
		return disambiguatorStore;
	}
}
