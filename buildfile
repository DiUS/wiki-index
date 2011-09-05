require 'rubygems'
require "bundler/setup"
require 'buildr-dependency-extensions'
require "buildr/mirah"

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
  
  WIKITEXT = [ artifact("org.sweble.wikitext:swc-engine:jar:1.0.0"), artifact("org.sweble.wikitext:swc-parser-lazy:jar:1.0.0")]

  LOG4J = artifact("log4j:log4j:jar:1.2.16")

  JUNIT4 = artifact("junit:junit:jar:4.8.2")
  HAMCREST = artifact("org.hamcrest:hamcrest-core:jar:1.2.1")
  MOCKITO = artifact("org.mockito:mockito-all:jar:1.8.5")
       
  puts artifact("junit:junit:jar:4.8.2").to_s

  compile.with WIKITEXT, LOG4J
  test.compile.with JUNIT4, HAMCREST, MOCKITO
  
  package(:jar)
end

