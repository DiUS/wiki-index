package com.springsense.wikiindex;

/*
 * Cloud9: A MapReduce Library for Hadoop
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import edu.umd.cloud9.collection.IndexableFileInputFormat2;
import edu.umd.cloud9.collection.XMLInputFormat2;
import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

/**
 * Hadoop new API <code>InputFormat</code> for processing Wikipedia pages from
 * the XML dumps.
 * 
 * @author Jimmy Lin
 * @author Tal Rotbart
 */
public class WikipediaPageInputFormat2 extends
		IndexableFileInputFormat2<LongWritable, WikipediaPage> {

	@Override
	public RecordReader<LongWritable, WikipediaPage> createRecordReader(
			InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new WikipediaPageRecordReader2();
	}

	/**
	 * Hadoop new API <code>RecordReader</code> for reading Wikipedia pages from
	 * the XML dumps.
	 */
	public static class WikipediaPageRecordReader2 extends
			RecordReader<LongWritable, WikipediaPage> {
		private XMLInputFormat2.XMLRecordReader reader;
		private LongWritable key = null;
		private WikipediaPage value = null;

		/**
		 * Creates a <code>WikipediaPageRecordReader2</code>.
		 */
		public WikipediaPageRecordReader2() throws IOException {
			reader = new XMLInputFormat2.XMLRecordReader();

			value = new WikipediaPage();
		}

		@Override
		public void initialize(
				org.apache.hadoop.mapreduce.InputSplit inputSplit,
				TaskAttemptContext context) throws IOException,
				InterruptedException {

			Configuration conf = context.getConfiguration();
			conf.set(XMLInputFormat2.START_TAG_KEY, WikipediaPage.XML_START_TAG);
			conf.set(XMLInputFormat2.END_TAG_KEY, WikipediaPage.XML_END_TAG);

			reader.initialize(inputSplit, context);
		}

		@Override
		public LongWritable getCurrentKey() throws IOException,
				InterruptedException {
			return key;
		}

		@Override
		public WikipediaPage getCurrentValue() throws IOException,
				InterruptedException {
			return value;
		}

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			if (reader.nextKeyValue()) {
				key = reader.getCurrentKey();
				WikipediaPage.readPage(value, reader.getCurrentValue()
						.toString());

				return true;
			}

			return false;
		}

		/**
		 * Returns progress on how much input has been consumed.
		 */
		public float getProgress() throws IOException {
			return reader.getProgress();
		}

		@Override
		public void close() throws IOException {
			reader.close();
		}

	}
}
