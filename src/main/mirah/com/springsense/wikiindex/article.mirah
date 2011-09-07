package com.springsense.wikiindex

class Article

  def initialize(id: int, title: string, wikitext: string)
    @id = id
    @title = title
    @wikitext = wikitext
  end
  
  def id
    @id
  end
  
  def title
    @title
  end
  
  def wikitext
    @wikitext
  end
end