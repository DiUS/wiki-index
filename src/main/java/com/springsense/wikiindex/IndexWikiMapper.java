package com.springsense.wikiindex;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.springsense.disambig.DisambiguationResult;
import com.springsense.disambig.DisambiguationResult.Sentence;
import com.springsense.disambig.DisambiguationResult.Variant;
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

		JSONObjectWritable document = processWikipediaPageToDocument(context, key, w);
		if (document == null) {
			return;
		}

		try {
			context.write(new Text(document.getString("key")), document);
			context.progress();
		} catch (JSONException e) {
			throw new RuntimeException(String.format("Could not process wikipedia article '%s' due to an error", w.getTitle()), e);
		}
	}

	public JSONObjectWritable processWikipediaPageToDocument(Context context, LongWritable key, WikipediaPage wikipediaPage) {

		if (!wikipediaPage.isArticle()) {
			return null;
		}

		Article article = new Article(key.get(), wikipediaPage);

		JSONObjectWritable document = new JSONObjectWritable();
		try {

			document.put("key", article.getTitle());
			context.progress();
			document.put("title", article.getTitle());
			context.progress();

			try {
				getArticleAnnotator().annotate(article);
				String articleText = article.getText();
				document.put("content", articleText);
				context.progress();

				if ((article.isRedirect()) && (StringUtils.isNotBlank(article.getRedirectTarget()))) {
					document.put("key", article.getRedirectTarget());
					document.remove("content");
				}
				
				context.progress();
				disambiguateAndStore(document, "title");
				context.progress();
				disambiguateAndStore(document, "content");
				context.progress();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);

				pw.printf("Error occured while processing this article ('%s')\n", article.getTitle());

				e.printStackTrace(pw);

				pw.flush();
				sw.flush();

				document.put("errors", sw.toString());

				LOG.error(String.format("Error occured while processing article '%s'", article.getTitle()), e);
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not process wikipedia article '%s' due to an error", article.getTitle()), e);
		}
		return document;
	}

	protected void disambiguateAndStore(JSONObjectWritable document, String fieldName) throws JSONException {
		Object fieldValue = document.opt(fieldName);
		if (fieldValue == null) {
			return;
		}

		//String springSenseRawFieldName = String.format("springsense.%s.raw", fieldName);
		String springSenseTextFieldName = String.format("springsense.%s.text", fieldName);

		List<String> values = new LinkedList<String>();

		if (fieldValue instanceof JSONArray) {
			JSONArray asArray = ((JSONArray) fieldValue);

			int asArrayLength = asArray.length();
			for (int i = 0; i < asArrayLength; i++) {
				values.add(asArray.get(i).toString());
			}
		} else {
			values.add(fieldValue.toString());
		}

		for (String value : values) {
			SentenceDisambiguationResult[] result = getDisambiguator().disambiguateText(value, 3, false, true, false);
			DisambiguationResult resultAsApi = convertToApiView(result);

			// document.append(springSenseRawFieldName, new
			// EmbeddedDisambiguationResult(result));

			int i = 0;
			List<Variant> variants = resultAsApi.getVariants();
			for (Variant variant : variants) {
				document.append(String.format("%s.%d", springSenseTextFieldName, i), variant.toString());
				i++;
			}
		}
	}

	protected DisambiguationResult convertToApiView(SentenceDisambiguationResult[] result) {
		List<Sentence> sentences = new ArrayList<Sentence>(result == null ? 0 : result.length);
		if (result != null) {
			for (SentenceDisambiguationResult taggedSentence : result) {
				sentences.add(taggedSentence.toApiView());
			}
		}

		DisambiguationResult resultAsApi = new DisambiguationResult(sentences);
		return resultAsApi;
	}

	public ArticleAnnotator getArticleAnnotator() {
		if (articleAnnotator == null) {
			setArticleAnnotator(new ArticleAnnotator());
		}
		return articleAnnotator;
	}

	protected void setArticleAnnotator(ArticleAnnotator articleAnnotator) {
		this.articleAnnotator = articleAnnotator;
	}

	public Disambiguator getDisambiguator() {
		return ((Disambiguator) (this.getDisambiguatorStore().get()));
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
