package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class IterMapper extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		String[] sections = line.split("\t"); // Splits it into two parts. Part 1: node+rank | Part 2: adj list

		if (sections.length > 2) // Checks if the data is in the incorrect format
		{
			throw new IOException("Incorrect data format");
		}
		if (sections.length != 2) {
			return;
		}
		
		/* 
		 * TODO: emit key: adj vertex, value: computed weight.
		 * 
		 * Remember to also emit the input adjacency list for this node!
		 * Put a marker on the string value to indicate it is an adjacency list.
		 */

		String[] noderank = sections[0].split("\\+"); // split node+rank
		String node = String.valueOf(noderank[0]);
		double rank = Double.valueOf(noderank[1]);
		String ajacentlist = sections[1].toString().trim(); //

		String[] ajacentnodes = ajacentlist.split(" ");
		int N = ajacentnodes.length; 
		// 1/n * rank
		double weightOfPage = (double)1/N * rank; 
		for(String ajacentnode : ajacentnodes) {
			context.write(new Text(ajacentnode), new Text(String.valueOf(weightOfPage)));
		}
		// at the same time, emit current node's ajacent list with marker "ADJ:"
		context.write(new Text(node), new Text(PageRankDriver.MARKER + sections[1]));
	}

}
