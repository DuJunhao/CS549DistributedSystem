package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class FinMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> {

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException, IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		/*
		 * TOD output key:-rank, value: node
		 * See IterMapper for hints on parsing the output of IterReducer.
		 */
		String[] sections = line.split("\t"); // nodeId+nodeName | rank

		if (sections.length > 2) // Checks if the data is in the incorrect format
		{
			throw new IOException("Incorrect data format");
		}
		if (sections.length != 2) {
			return;
		}
		// to reverse shuffle the reducer, we need minus rank with 0
		context.write(new DoubleWritable(0 - Double.valueOf(sections[1])), new Text(sections[0]));
	}

}
