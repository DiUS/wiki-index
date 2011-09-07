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

# UGLY MONKEY PATCH to make Buildr-Mirah run the tests
module Buildr
  class TestFramework::Java < TestFramework::Base
    class << self
      def applies_to?(project) #:nodoc:
        project.test.compile.language == :java || project.test.compile.language == :groovy || project.test.compile.language == :mirah
      end
    end
  end
end

desc "The SpringSense Wikipedia Indexing project"
define "wiki-index" do
  extend PomGenerator
  extend TransitiveDependencies

  project.version = VERSION_NUMBER
  project.group = GROUP
  project.transitive_scopes = [:compile, :run, :test]

  manifest["Implementation-Vendor"] = COPYRIGHT
  
  LOG4J = artifact("log4j:log4j:jar:1.2.16")
  
  WIKITEXT = artifact("org.sweble.wikitext:swc-engine:jar:1.0.0")
  COMMONS_IO = artifact("commons-io:commons-io:jar:2.0.1")

  JUNIT4 = artifact("junit:junit:jar:4.8.2")
  HAMCREST = artifact("org.hamcrest:hamcrest-core:jar:1.2.1")
  MOCKITO = artifact("org.mockito:mockito-all:jar:1.8.5")
       
  compile.with WIKITEXT, LOG4J, COMMONS_IO
  test.compile.with JUNIT4, HAMCREST, MOCKITO
  
  package(:jar)
end

