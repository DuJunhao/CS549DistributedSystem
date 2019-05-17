package edu.stevens.cs549.hadoop.pagerank;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FindJoinMapper extends Mapper<LongWritable, Text, Text, Text>  {

	 @Override
	    protected void setup(Context context) throws IOException, InterruptedException {
	        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
	            URI mappingFileUri = context.getCacheFiles()[0];
	            if (mappingFileUri != null) {
	                System.out.println("Mapping File: " + FileUtils.readFileToString(new File("./cache")));
	            } else {
	                System.out.println("NO MAPPING FILE");
	            }
	        } else {
	            System.out.println("NO CACHE FILES AT ALL");
	        }
	    }
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException, IllegalArgumentException {
		String line = value.toString(); // Converts to a String
	
		String[] sections;
		if (line.contains(":")) {
			int index = line.indexOf(":");
			sections = new String[2];
			sections[0] = line.substring(0, index);
			sections[1] = line.substring(index + 1, line.length());
		} else {
			sections = line.split("\t"); // Splits it into node+rank and adjacent list
		}
		

		if (sections.length > 2) 
		{
			throw new IOException("Incorrect data format");
		}
		String[] noderank = sections[0].split("\\+");
		if(noderank.length == 1) {
			// it's nodeID and its name
			context.write(new Text(noderank[0]), new Text(PageRankDriver.MARKER_NAME + sections[1].trim()));
		}
		
		if(noderank.length == 2) {
			// it's nodeID and its rank
			context.write(new Text(noderank[0]), new Text(PageRankDriver.MARKER_RANK + noderank[1]));
		}
	}

	
}
