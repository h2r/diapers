/* package edu.brown.h2r.diapers.textProcessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class WordCountStateEstimation {
	private String fileName;
	private String fileType;
	private HashMap<Integer, HashMap<String, Double>> stateCounts = null;
	final String jsonObject = "json";
	final String textFile = "text";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public WordCountStateEstimation(String fileNameInput, String fileTypeInput){
		fileName = fileNameInput; 
		if ((fileTypeInput.equals(textFile) || fileTypeInput.equals(textFile))!=true) {
			throw new IllegalArgumentException("Invalid input type; input json or text");
		}
		fileType=fileTypeInput;	
		
	}
	 public String getfileName( ){
	       //System.out.println("Puppy's age is :" + puppyAge ); 
	       return fileName;
	   }
	 public String getfileType( ){
	       //System.out.println("Puppy's age is :" + puppyAge ); 
	       return fileType;
	   }
	 public void readLargerTextFileAlternate(String aFileName) throws IOException {
		 String dataPath = "src\\edu\\brown\\h2r\\diapers\\data\\";
		 Path path = Paths.get(dataPath, aFileName);
		    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)){
		      String line = null;
		      int lineCounter=1;
		      while ((line = reader.readLine()) != null) {
		        //process each line in some way
		    	  stateCounts.put(lineCounter, calculateProb(line));
		    	  
		      }      
		    }
		  }
	 
	 private HashMap<String, Double> calculateProb(String line) {
		// TODO Auto-generated method stub
		 HashMap<String, Double> calculatedMap = null;
		 String words[] = line.split(" ");
		 words.//start here need a hashmap to retun prob smoothed, do counting first then the reduction...
		return calculatedMap;
	}
	public void calculateProbabilities(){
		 if (this.fileType.equals(jsonObject)){
			 System.out.println("not ready yet");
		 }
		 else{
			 
		 }
	 }
	 

}*/
