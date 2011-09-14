package com.springsense.wikiindex

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.hamcrest.Matchers

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.lib.IdentityMapper;

import org.apache.hadoop.mrunit.MapDriver;

class IndexWikiMapperTest

  $Before
  def setUp: void
    @mapper = IdentityMapper.new();
    @driver = MapDriver.new(@mapper);
  end

  $Test
  def mapper_should_return_the_same_values: void
    @driver.withInput(Text.new("foo"), Text.new("bar"))
            .withOutput(Text.new("foo"),Text.new("bar"))
            .runTest();
  end
end
