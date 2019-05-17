package edu.stevens.cs549.hadoop.pagerank;

import java.io.*;
import java.util.Iterator;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class DiffRed2 extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double diff_max = 0.0; // sets diff_max to a default value
		/* 
		 * TOD: Compute and emit the maximum of the differences
		 */
		Iterator<Text> iterator = values.iterator();
		// caculate the maxium difference and output the result
		while(iterator.hasNext()) {
			double diff = Double.valueOf(iterator.next().toString());
			diff_max = diff_max > diff ? diff_max : diff;
		}
		context.write(new Text(""), new Text(String.valueOf(diff_max)));
	}
}
