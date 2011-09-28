// Generated from article_renderer.mirah
package com.springsense.wikiindex;

import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.lazy.LinkTargetException;

public class ArticleRenderer {

	private org.sweble.wikitext.engine.utils.SimpleWikiConfiguration config;
	private org.sweble.wikitext.engine.Compiler compiler;

	public org.sweble.wikitext.engine.utils.SimpleWikiConfiguration config() {
		if ((this.config != null)) {
			return this.config;
		} else {
			try {
				return this.config = new org.sweble.wikitext.engine.utils.SimpleWikiConfiguration(
						"classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
			} catch (Throwable e) {
				throw new RuntimeException(
						"Problem loading configuration due to an error", e);
			}
		}
	}

	public org.sweble.wikitext.engine.Compiler compiler() {
		if ((this.compiler != null)) {
			return this.compiler;
		} else {
			return this.compiler = new org.sweble.wikitext.engine.Compiler(
					this.config());
		}
	}

	public java.lang.String renderAsText(
			com.springsense.wikiindex.Article article) {
		return this.render_as_text(article.id(), article.title(),
				article.wikitext());
	}

	public java.lang.String render_as_text(int id, java.lang.String title,
			java.lang.String wikitext) {
		org.sweble.wikitext.engine.PageTitle pageTitle = null;
		org.sweble.wikitext.engine.PageId pageId = null;
		org.sweble.wikitext.engine.CompiledPage cp = null;

		org.sweble.wikitext.engine.utils.TextConverter p = null;
		try {
			pageTitle = org.sweble.wikitext.engine.PageTitle.make(
					this.config(), title);
		} catch (LinkTargetException e) {
			throw new RuntimeException(
					"Problem rendering article due to an error", e);
		}
		pageId = new org.sweble.wikitext.engine.PageId(pageTitle, id);
		try {
			cp = this.compiler().postprocess(pageId, wikitext, null);
		} catch (CompilerException e) {
			throw new RuntimeException(
					"Problem rendering article due to an error", e);
		}

		p = new org.sweble.wikitext.engine.utils.TextConverter(this.config(),
				80);

		return p.go(cp.getPage()).toString();
	}
}
