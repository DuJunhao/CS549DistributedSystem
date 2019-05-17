package edu.stevens.cs549.hadoop.pagerank;

import java.io.*;
import java.util.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class IterReducer extends Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double d = PageRankDriver.DECAY; // Decay factor
		/* 
		 * TOD: emit key:node+rank, value: adjacency list
		 * Use PageRank algorithm to compute rank from weights contributed by incoming edges.
		 * Remember that one of the values will be marked as the adjacency list for the node.
		 */
		Iterator<Text> iterator = values.iterator();
		double currentRank = 0; // default rank is 1 - d
		String ajacentlist = "";
		while(iterator.hasNext()) {
			String line = iterator.next().toString();
			if(!line.startsWith(PageRankDriver.MARKER)) {
				currentRank += Double.valueOf(line);
			} else {
				ajacentlist = line.replaceAll(PageRankDriver.MARKER, "");
			}
		}
		// (1-d) + d * sum(bac
		currentRank = 1 - d + currentRank * d;
		context.write(new Text(key + "+" + currentRank), new Text(ajacentlist));
	}
}
