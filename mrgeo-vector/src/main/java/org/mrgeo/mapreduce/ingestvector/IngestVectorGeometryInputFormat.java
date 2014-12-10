/*
 * Copyright 2009-2014 DigitalGlobe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mrgeo.mapreduce.ingestvector;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.mrgeo.mapreduce.GeometryWritable;

import java.io.IOException;

public class IngestVectorGeometryInputFormat extends FileInputFormat<LongWritable, GeometryWritable>
{

  public IngestVectorGeometryInputFormat()
  {
  }

  @Override
  public RecordReader<LongWritable, GeometryWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException
  {
    final RecordReader<LongWritable, GeometryWritable> reader = new IngestVectorRecordReader();
    
    reader.initialize(split, context);

    return reader;
  }

  @Override
  protected boolean isSplitable(final JobContext context, final Path filename)
  {
    return false;
  }

}
