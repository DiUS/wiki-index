package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers

import org.apache.hadoop.io.Text
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapred.Mapper
import org.apache.hadoop.mapred.lib.IdentityMapper

import org.apache.hadoop.mrunit.types.Pair
import org.apache.hadoop.mrunit.MapDriver

import org.apache.commons.io.IOUtils
import edu.umd.cloud9.collection.wikipedia.WikipediaPage

class IndexWikiMapperTest
  include Assert
  include Matchers

  $Before
  def setUp: void
    @mapper = IndexWikiMapper.new();
    @driver = MapDriver.new(@mapper);
    
    @page = WikipediaPage.new();
    
    WikipediaPage.readPage(@page, IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article.xml")))
  end

  $Test
  def mapper_should_return_the_same_values: void
    @driver.setInput(LongWritable.new(1024), @page)
    
    output = @driver.run

    assertEquals(Text.new( "Red Army invasion of Azerbaijan" ), Pair(output.get(0)).getFirst)
    assertEquals(Text.new( IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test-article-red-army.wikitext")) ), Pair(output.get(0)).getSecond)
  end
end
