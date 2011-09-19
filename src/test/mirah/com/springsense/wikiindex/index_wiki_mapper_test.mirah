package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers
import org.mockito.Mockito;

import org.apache.commons.io.IOUtils

import org.apache.hadoop.mrunit.MapDriver
import org.apache.hadoop.mrunit.types.Pair

import org.apache.hadoop.io.Text
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapred.Mapper
import org.apache.hadoop.mapred.lib.IdentityMapper

import edu.umd.cloud9.collection.wikipedia.WikipediaPage

import com.springsense.disambig.Disambiguator
import com.springsense.disambig.DisambiguatorFactory

class IndexWikiMapperTest
  include Assert
  include Matchers
  include Mockito

  $Before
  def setUp: void
    @mock_disambiguator_factory = DisambiguatorFactory(mock(DisambiguatorFactory.class))
    @mock_disambiguator = Disambiguator(mock(Disambiguator.class))
    
    Mockito.when(@mock_disambiguator_factory.openNewDisambiguator()).thenReturn(@mock_disambiguator)
    
    @mapper = IndexWikiMapper.new(@mock_disambiguator_factory)
    @driver = MapDriver.new(@mapper)
    
    @page = WikipediaPage.new()
    
    WikipediaPage.readPage(@page, IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article.xml")))
  end

  $Test
  def mapper_should_return_the_correct_values: void
    @driver.setInput(LongWritable.new(1024), @page)
    
    output = @driver.run
    
    article_text = IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article-red-army.wikitext"))

    assertEquals(Text.new( "Red Army invasion of Azerbaijan" ), Pair(output.get(0)).getFirst)
    assertEquals(Text.new( article_text ), Pair(output.get(0)).getSecond)
    
    Disambiguator(verify(@mock_disambiguator)).disambiguateText(article_text, 3, false, true, false)
  end
end
