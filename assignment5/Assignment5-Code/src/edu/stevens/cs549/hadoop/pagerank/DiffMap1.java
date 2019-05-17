package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class DiffMap1 extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		String[] sections = line.split("\t"); // Splits each line
		if (sections.length > 2) // checks for incorrect data format
		{
			throw new IOException("Incorrect data format");
		}
		/**
		 *  TOD: read node-rank pair and emit: key:node, value:rank
		 */
		String[] noderank = sections[0].split("\\+");// split node+rank;
		context.write(new Text(noderank[0]),  new Text(noderank[1])); // emit node, rank
	}

}
