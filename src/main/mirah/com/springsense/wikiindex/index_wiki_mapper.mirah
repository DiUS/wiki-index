package com.springsense.wikiindex

import org.apache.hadoop.mapred.Mapper
import org.apache.hadoop.mapred.Reporter
import org.apache.hadoop.mapred.MapReduceBase
import org.apache.hadoop.mapred.OutputCollector

import org.apache.hadoop.io.Text
import org.apache.hadoop.io.LongWritable

import java.io.File
import org.apache.commons.io.FileUtils

import edu.umd.cloud9.collection.wikipedia.WikipediaPage

class IndexWikiMapper < MapReduceBase 
  implements Mapper
  
  def map(key: Object, p: Object, output: OutputCollector, reporter: Reporter): void
  
    w = WikipediaPage(p)
    
    article = Article.new(-1, w.getTitle, w.getWikiMarkup)
    article_text = article_renderer.render_as_text(article)
    
#    FileUtils.writeStringToFile(File.new('/Users/redbeard/Dev/SpringSense/wiki-index/src/test/resources/test-article-red-army.wikitext'), article_text)
    
    output.collect(
      Text.new(w.getTitle), 
      Text.new(article_text) 
      )
  end
    
  def article_renderer
    @article_renderer ||= ArticleRenderer.new
  end
end
