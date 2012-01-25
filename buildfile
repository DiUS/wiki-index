require 'rubygems'
require "bundler/setup"
require 'buildr-dependency-extensions'

# Version number for this release
VERSION_NUMBER = "0.1.#{ENV['BUILD_NUMBER'] || 'SNAPSHOT'}"
# Group identifier for your projects
GROUP = "com.springsense"
COPYRIGHT = "(C) Copyright 2011 SpringSense Trust. All rights reserved. Licensed under  the Apache License, Version 2.0. See file LICENSE."

# Specify Maven 2.0 remote repositories here, like this:
repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://www.ibiblio.org/maven2"
repositories.remote << "http://mojo.informatik.uni-erlangen.de/nexus/content/repositories/public-releases"
repositories.remote << "http://192.168.0.96/~artifacts/repository"
repositories.remote << "https://repository.cloudera.com/artifactory/repo"

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
  BLIKI = artifact("info.bliki.wiki:bliki-core:jar:3.0.16")

  GUAVA = artifact('com.google.guava:guava:jar:r09')
  JACKSON = [ 
    artifact('org.codehaus.jackson:jackson-core-asl:jar:1.8.5'),
    artifact('org.codehaus.jackson:jackson-mapper-asl:jar:1.8.5'),
  ]
  
  DISAMBIGJ = artifact("com.springsense:disambigj:jar:2.0.2.75")
  WIKITEXT = artifact("org.sweble.wikitext:swc-engine:jar:1.0.0")
  COMMONS_IO = artifact("commons-io:commons-io:jar:2.0.1")

  JUNIT4 = artifact("junit:junit:jar:4.8.2")
  HAMCREST = artifact("org.hamcrest:hamcrest-core:jar:1.2.1")
  MOCKITO = artifact("org.mockito:mockito-all:jar:1.8.5")
  MAP_REDUCE_UNIT = artifact("org.apache.mrunit:mrunit:jar:0.5.0-incubating")
       
  compile.with GUAVA, JACKSON, HADOOP, CLOUD9, BLIKI, DISAMBIGJ, WIKITEXT, LOG4J, COMMONS_IO
  test.compile.with JUNIT4, HAMCREST, MOCKITO, MAP_REDUCE_UNIT
  
  test.using :fork => true
  test.using :java_args=>[ '-Xmx2g' ]
  
  package(:jar)
  
end

directory 'target/hadoop-job-jar/lib'

def jar_dependencies(*dependencies)
  dependencies.flatten.map do | source_jar | 
    target_path = "target/hadoop-job-jar/lib/#{Pathname.new(source_jar.to_s).basename}"
    
    file target_path => [ 'target/hadoop-job-jar/lib', source_jar ] do
      cp source_jar.to_s, target_path
    end
  end
end

def class_dependencies()
  Dir.glob("target/classes/**/*").map do | source_class |
    pathname = Pathname.new(source_class)
    rel_class = pathname.relative_path_from(Pathname.new('target/classes')).to_s
    target_path = "target/hadoop-job-jar/#{rel_class.to_s}"
    if pathname.directory?
      file rel_class do
        # puts "Creating '#{target_path}'..."
        mkdir_p target_path
      end
    else
      file target_path => [ 'target/hadoop-job-jar', source_class ] do
        # puts "'#{source_class.to_s}' -> '#{target_path}'"
        cp source_class.to_s, target_path
      end
    end
  end
end

def dependencies
  deps = [] 
    
  deps.push(*project('wiki-index').package)
  deps.push(*jar_dependencies(project('wiki-index').compile.dependencies))
  deps.push(*class_dependencies)
  
  # puts "Deps:\n\t'#{deps.join("'\n\t'")}"
  
  deps
end

task 'wiki-index:hadoop-job-jars' => dependencies

def hadoop_job_jar_filename
  "target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar"
end

file hadoop_job_jar_filename => [ 'wiki-index:hadoop-job-jars' ] do
  puts "Packaging 'target/wiki-index-hadoop-job-#{project('wiki-index').version}.jar'..."
  FileUtils.cd 'target/hadoop-job-jar' do | dir |
    puts `zip -r "../wiki-index-hadoop-job-#{project('wiki-index').version}.jar" *`
  end
end

task 'wiki-index:hadoop-job-jar' => hadoop_job_jar_filename

def hadoop_dir
  ENV['HADOOP_HOME']
end

def hadoop_cmd(wiki_dump_xml)
  "#{hadoop_dir}/bin/hadoop jar #{hadoop_job_jar_filename} com.springsense.wikiindex.IndexWikipedia -input #{wiki_dump_xml} -output target/result -matrixDir /media/matrix.data/current/this"
end

task 'wiki-index:test-run' => 'wiki-index:hadoop-job-jar' do
  raise "HADOOP_HOME must be set" if hadoop_dir.nil? or hadoop_dir.empty?
  
  rm_rf 'target/result'
  cmd = hadoop_cmd("target/test/resources/enwiki-20110901-pages-articles-small.xml")
  puts cmd
  run_command cmd
end

task :upload_to_s3 => [ 'wiki-index:hadoop-job-jar' ] do
  puts "About to upload to s3..."
  run_command "s3cmd -v put #{hadoop_job_jar_filename} s3://springsense-releases/"
  puts "Done uploading."
end

def run_command(cmd, io_prefix = "local:\t", use_last_line_for_exit = false)
  puts "\tExecuting locally: '#{cmd}'"
  last_line = "0"
  IO.popen("#{cmd} 2>&1") do |f|
    while line = f.gets do
      puts "#{io_prefix}#{line}"
      last_line = line
    end
  end

  if use_last_line_for_exit then
    return_value = last_line.to_i
  else
    result = $?
    return_value = result.exitstatus
  end

  raise "Error while running command: '#{cmd}'..." unless return_value == 0
end
