package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert

class ArticleTest
  include Assert

  puts "This was loaded"
  $Before
  def setup
    returns void
  end

  $Test
  def testTextShouldReturnArticleContentAsText
      returns void
  
      assertTrue(true) 
  end
  
end