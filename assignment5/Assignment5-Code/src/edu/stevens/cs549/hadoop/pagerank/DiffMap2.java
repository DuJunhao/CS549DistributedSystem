package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class DiffMap2 extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String s = value.toString(); // Converts Line to a String

		/* 
		 * TOD: emit: key:"Difference" value:difference calculated in DiffRed1
		 */
		String[] noderank = s.split("\t+");
		context.write(new Text("Difference"), new Text(noderank[1])); 
	}

}
