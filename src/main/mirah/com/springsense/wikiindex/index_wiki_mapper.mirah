import org.apache.hadoop.mapred.Mapper
import org.apache.hadoop.mapred.Reporter
import org.apache.hadoop.mapred.MapReduceBase
import org.apache.hadoop.mapred.OutputCollector

import org.apache.hadoop.io.LongWritable

import edu.umd.cloud9.collection.wikipedia.WikipediaPage

class IndexWikiMapper < MapReduceBase 
  implements Mapper

  def map(key: Object, p: Object, output: OutputCollector, reporter: Reporter): void
    output.collect(key, p)
  end
    
end
