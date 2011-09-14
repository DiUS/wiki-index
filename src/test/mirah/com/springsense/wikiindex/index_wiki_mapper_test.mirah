package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.Mapper
import org.apache.hadoop.mapred.lib.IdentityMapper

import org.apache.hadoop.mrunit.types.Pair
import org.apache.hadoop.mrunit.MapDriver

class IndexWikiMapperTest
  include Assert
  include Matchers

  $Before
  def setUp: void
    @mapper = IndexWikiMapper.new();
    @driver = MapDriver.new(@mapper);
  end

  $Test
  def mapper_should_return_the_same_values: void
    @driver.setInput(Text.new("foo"), Text.new("bar"))
    
    output = @driver.run

    assertEquals(Text.new("foo"), Pair(output.get(0)).getFirst)
    assertEquals(Text.new("bar"), Pair(output.get(0)).getSecond)
  end
end
