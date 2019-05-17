package edu.stevens.cs549.hadoop.pagerank;

import java.io.*;
import java.util.Iterator;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

public class DiffRed1 extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double[] ranks = new double[2];
		/* 
		 * TOD: The list of values should contain two ranks.  Compute and output their difference.
		 */
		Iterator<Text> iterator = values.iterator();
		double diff = 0; // default diff has max value
		// caculate rank1
		if(iterator.hasNext()) {
			ranks[0] = Double.valueOf(iterator.next().toString());
		}
		// caculate rank2
		if(iterator.hasNext()) {
			ranks[1] = Double.valueOf(iterator.next().toString());
		}
		// caculate diff
		diff = Math.abs(ranks[0] - ranks[1]);
		System.out.println( key.toString() + "  " + diff);
		context.write(key, new Text(String.valueOf(diff)));
	}
}
