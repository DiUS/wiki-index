package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers

import org.apache.commons.io.IOUtils

class ArticleTest
  include Assert
  include Matchers

  $Before
  def setup:void
    
    @id = -1
    @title = "Fake title"
    @articleWikitext = IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article.wikitext"))
    
    @article = Article.new(@id, @title, @articleWikitext)
  end
  
end