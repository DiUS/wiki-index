package com.springsense.wikiindex;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.springsense.disambig.Disambiguator;
import com.springsense.disambig.DisambiguatorFactory;
import com.springsense.disambig.SentenceDisambiguationResult;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.umd.cloud9.io.JSONObjectWritable;

public class IndexWikiMapper extends
		Mapper<LongWritable, WikipediaPage, Text, JSONObjectWritable> {
	private DisambiguatorFactory disambiguatorFactory;
	private ArticleRenderer article_renderer;
	private ThreadLocal<Disambiguator> disambiguatorStore;

	public IndexWikiMapper(
			com.springsense.disambig.DisambiguatorFactory disambiguatorFactory) {
		this.disambiguatorFactory = disambiguatorFactory;
	}

	@Override
	protected void map(LongWritable key, WikipediaPage w, Context context)
			throws IOException, InterruptedException {

		Article article = null;
		String articleText = null;
		
		article = new Article(key.get(), w.getTitle(), w.getWikiMarkup());
		
		articleText = getArticleRenderer().renderAsText(article);
		
		SentenceDisambiguationResult[] result = getDisambiguator().disambiguateText(articleText, 3, false,
				true, false);
		
		try {
			JSONObjectWritable document = new JSONObjectWritable();

			document.put("title", article.getTitle());
			document.put("content", articleText);

			context.write(new Text(article.getTitle()), document);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not process wikipedia article '%s' due to an error", article.getTitle()), e);
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
								"Could not create new disambiguator due to an error",
								e);
					}
				}

			};
		}
		return disambiguatorStore;
	}
}
