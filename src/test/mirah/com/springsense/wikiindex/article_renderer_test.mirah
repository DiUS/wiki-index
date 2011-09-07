package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers

import org.apache.commons.io.IOUtils

class ArticleRendererTest
  include Assert
  include Matchers

  $Before
  def setup:void
    
    @article_renderer = ArticleRenderer.new()
    @article = Article.new(-1, "Anthropology", IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article.wikitext")))
  end

  $Test
  def test_text_should_return_article_content_as_text:void
      rendered_text = @article_renderer.render_as_text(@article)

      puts "========================================"
      puts rendered_text
      puts "========================================"

      assertThat(
        rendered_text, 
        containsString("Biological anthropology, or physical anthropology, focuses on the study"))
  end
  
end