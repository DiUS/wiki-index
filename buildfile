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
repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://www.ibiblio.org/maven2"
repositories.remote << "http://mojo.informatik.uni-erlangen.de/nexus/content/repositories/public-releases"
repositories.remote << "http://192.168.0.91/~artifacts/repository"

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
  
  HADOOP = artifact('org.apache.hadoop:hadoop-core:jar:0.20.204.0')
  CLOUD9 = artifact("edu.umd:cloud9:jar:1.2.4")
  
  DISAMBIGJ = artifact("com.springsense:disambigj:jar:2.0.0.51")
  WIKITEXT = artifact("org.sweble.wikitext:swc-engine:jar:1.0.0")
  COMMONS_IO = artifact("commons-io:commons-io:jar:2.0.1")

  JUNIT4 = artifact("junit:junit:jar:4.8.2")
  HAMCREST = artifact("org.hamcrest:hamcrest-core:jar:1.2.1")
  MOCKITO = artifact("org.mockito:mockito-all:jar:1.8.5")
  #MAP_REDUCE_UNIT = artifact("com.cloudera.hadoop:hadoop-mrunit:0.20.2-320:jar")
       
  compile.with HADOOP, CLOUD9, DISAMBIGJ, WIKITEXT, LOG4J, COMMONS_IO
  test.compile.with JUNIT4, HAMCREST, MOCKITO#, MAP_REDUCE_UNIT
  
  package(:jar)
  
end

directory 'target/hadoop-job-jar/lib'

def jar_dependencies(*dependencies)
  dependencies.flatten.map do | source_jar | 
    target_path = "target/hadoop-job-jar/lib/#{Pathname.new(source_jar.to_s).basename}"
    
    # puts "#{target_path} => #{source_jar}"

    file target_path => [ 'target/hadoop-job-jar/lib', source_jar ] do
      cp source_jar.to_s, target_path
    end
  end
end

task 'wiki-index:hadoop-job-jars' => jar_dependencies(project('wiki-index').package, project('wiki-index').compile.dependencies)

file "target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar" => [ 'wiki-index:hadoop-job-jars' ] do
  puts "Packaging 'target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar'..."
  FileUtils.cd 'target/hadoop-job-jar' do | dir |
    puts `zip -r "../wiki-index-hadoop-job-#{project('wiki-index').version}.jar" *`
  end
end

task 'wiki-index:hadoop-job-jar' => "target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar"

#puts task('wiki-index:hadoop-job-jar').inspect
#puts task('wiki-index:hadoop-job-jars').inspect
#puts task("target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar").inspect