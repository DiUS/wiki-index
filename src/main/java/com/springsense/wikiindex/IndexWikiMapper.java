package com.springsense.wikiindex;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.springsense.disambig.Disambiguator;
import com.springsense.disambig.DisambiguatorFactory;
import com.springsense.disambig.SentenceDisambiguationResult;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.umd.cloud9.io.JSONObjectWritable;

public class IndexWikiMapper extends Mapper<LongWritable, WikipediaPage, Text, JSONObjectWritable> {

	public static final String DISAMBIG_MATRIX_DATA_DIR_KEY = "disambig.matrix.data.dir";

	private static final Logger LOG = Logger.getLogger(IndexWikiMapper.class);

	private DisambiguatorFactory disambiguatorFactory;
	private ArticleAnnotator articleAnnotator;
	private ThreadLocal<Disambiguator> disambiguatorStore;

	private static DisambiguatorFactory classLoaderDisambiguationFactory;

	public IndexWikiMapper(com.springsense.disambig.DisambiguatorFactory disambiguatorFactory) {
		this.disambiguatorFactory = disambiguatorFactory;
	}

	public IndexWikiMapper() {
		this(null);
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);

		if (disambiguatorFactory == null) {
			if (classLoaderDisambiguationFactory == null) {
				String matrixDataDir = context.getConfiguration().get(DISAMBIG_MATRIX_DATA_DIR_KEY);

				// Get the jvm heap size.
				long heapSize = Runtime.getRuntime().totalMemory();

				// Print the jvm heap size.
				LOG.info(String.format("Heap Size: %d", heapSize));

				LOG.info(String.format("Starting new disambiguator with path '%s'", matrixDataDir));
				classLoaderDisambiguationFactory = new DisambiguatorFactory(matrixDataDir);
				LOG.debug("Done.");
			}

			disambiguatorFactory = classLoaderDisambiguationFactory;
		}
	}

	@Override
	protected void map(LongWritable key, WikipediaPage w, Context context) throws IOException, InterruptedException {

		JSONObjectWritable document = processWikipediaPageToDocument(key, w);
		if (document == null) {
			return;
		}

		try {
			context.write(new Text(document.getString("key")), document);
		} catch (JSONException e) {
			throw new RuntimeException(String.format("Could not process wikipedia article '%s' due to an error", w.getTitle()), e);
		}
	}

	public JSONObjectWritable processWikipediaPageToDocument(LongWritable key, WikipediaPage wikipediaPage) {

		if (!wikipediaPage.isArticle()) {
			return null;
		}

		Article article = new Article(key.get(), wikipediaPage);
		getArticleAnnotator().annotate(article);
		
		String articleText = article.getText();
		SentenceDisambiguationResult[] result = getDisambiguator().disambiguateText(articleText, 3, false, true, false);

		JSONObjectWritable document = new JSONObjectWritable();
		try {

			document.put("key", article.getTitle());
			document.put("content", articleText);
			document.put("title", article.getTitle());
			if ((article.isRedirect()) && (StringUtils.isNotBlank(article.getRedirectTarget()))) {
				document.put("key", article.getRedirectTarget());
				document.remove("content");
			}

		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not process wikipedia article '%s' due to an error", article.getTitle()), e);
		}
		return document;
	}

	public ArticleAnnotator getArticleAnnotator() {
		if ((articleAnnotator != null)) {
			return articleAnnotator;
		} else {
			return articleAnnotator = new ArticleAnnotator();
		}
	}

	public Disambiguator getDisambiguator() {
		return ((com.springsense.disambig.Disambiguator) (this.getDisambiguatorStore().get()));
	}

	public ThreadLocal<Disambiguator> getDisambiguatorStore() {
		if (disambiguatorStore == null) {
			disambiguatorStore = new ThreadLocal<Disambiguator>() {

				@Override
				protected Disambiguator initialValue() {
					try {
						return (disambiguatorFactory.openNewDisambiguator());
					} catch (IOException e) {
						throw new RuntimeException("Could not create new disambiguator due to an error", e);
					}
				}

			};
		}
		return disambiguatorStore;
	}
}
