package com.springsense.wikiindex;

import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;

public class ArticleAnnotator {

	private SimpleWikiConfiguration config;
	private Compiler compiler;

	public SimpleWikiConfiguration getConfig() {
		if ((config != null)) {
			return config;
		} else {
			try {
				return config = new SimpleWikiConfiguration(
						"classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
			} catch (Throwable e) {
				throw new RuntimeException(
						"Problem loading configuration due to an error", e);
			}
		}
	}

	public Compiler getCompiler() {
		if ((compiler != null)) {
			return compiler;
		} else {
			return compiler = new org.sweble.wikitext.engine.Compiler(getConfig());
		}
	}

	public void annotate(Article article) {
		String wikitext = article.getWikitext();
		PageTitle pageTitle = null;
		PageId pageId = null;
		CompiledPage cp = null;
		
		if ((wikitext == null) || (wikitext.length() <= 0)) {
			return;
		}
		
		try {
			pageTitle = PageTitle.make(getConfig(), article.getTitle());
		} catch (LinkTargetException e) {
			throw new RuntimeException(
					"Problem annotating article due to an error", e);
		}
		pageId = new PageId(pageTitle, article.id());
		try {
			cp = getCompiler().postprocess(pageId, wikitext, null);
		} catch (CompilerException e) {
			throw new RuntimeException(
					String.format("Problem annotating article due to an error. Wikitext follows:\n----\n%s\n----\n", wikitext), e);
		}
		
		ArticleVisitor visitor = new ArticleVisitor(article, getConfig(), 80);
		
		article.setText(visitor.go(cp.getPage()).toString());
	}
}
