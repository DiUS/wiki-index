require 'rubygems'
require "bundler/setup"
require 'buildr-dependency-extensions'

# Version number for this release
VERSION_NUMBER = "0.1.#{ENV['BUILD_NUMBER'] || 'SNAPSHOT'}"
# Group identifier for your projects
GROUP = "com.springsense"
COPYRIGHT = "(C) Copyright 2011 SpringSense Trust. All rights reserved. Licensed under  the Apache License, Version 2.0. See file LICENSE."

# Specify Maven 2.0 remote repositories here, like this:
repositories.remote << "http://www.ibiblio.org/maven2/"
repositories.remote << "http://mojo.informatik.uni-erlangen.de/nexus/content/repositories/public-releases"

desc "The SpringSense Wikipedia Indexing project"
define "wiki-index" do
  extend PomGenerator

  project.version = VERSION_NUMBER
  project.group = GROUP
  manifest["Implementation-Vendor"] = COPYRIGHT
  
  WIKITEXT = artifact("org.sweble.wikitext:sweble-wikitext:jar:1.0.0")

  JUNIT4 = artifact("junit:junit:jar:4.8.2")
  HAMCREST = artifact("org.hamcrest:hamcrest-core:jar:1.2.1")
  MOCKITO = artifact("org.mockito:mockito-all:jar:1.8.5")
       
  compile.with WIKITEXT
  test.compile.with JUNIT4, HAMCREST, MOCKITO
  
  package(:jar)
end
