package com.springsense.wikiindex;

import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.engine.utils.TextConverter;
import org.sweble.wikitext.lazy.LinkTargetException;

public class ArticleRenderer {

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

	public String renderAsText(Article article) {
		return renderAsText(article.id(), article.getTitle(),
				article.getWikitext());
	}

	public String renderAsText(long id, String title, String wikitext) {
		PageTitle pageTitle = null;
		PageId pageId = null;
		CompiledPage cp = null;

		TextConverter p = null;
		try {
			pageTitle = PageTitle.make(getConfig(), title);
		} catch (LinkTargetException e) {
			throw new RuntimeException(
					"Problem rendering article due to an error", e);
		}
		pageId = new PageId(pageTitle, id);
		try {
			cp = getCompiler().postprocess(pageId, wikitext, null);
		} catch (CompilerException e) {
			throw new RuntimeException(
					"Problem rendering article due to an error", e);
		}

		p = new TextConverter(getConfig(), 80);

		return p.go(cp.getPage()).toString();
	}
}
