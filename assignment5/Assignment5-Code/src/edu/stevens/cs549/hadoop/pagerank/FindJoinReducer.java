package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FindJoinReducer extends Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException, IllegalArgumentException {
		Iterator<Text> iterator = values.iterator();
		String nodeName = "";
		String rank  = "";
		while(iterator.hasNext()) {
			String tmp = iterator.next().toString();
			if(tmp.startsWith(PageRankDriver.MARKER_NAME)) {
				nodeName = tmp.replaceAll(PageRankDriver.MARKER_NAME, "");
			} 
			if(tmp.startsWith(PageRankDriver.MARKER_RANK)) {
				rank = tmp.replaceAll(PageRankDriver.MARKER_RANK, "");
			} 
		}
		
		context.write(new Text(key + "+" + nodeName) , new Text(rank));
		
	}

}
