package com.nbm.executable;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;  

public class ConvertJSON {
	
	 public static void main(String[] args) { 
		  ProcessedMessage pm = new ProcessedMessage();
		  
		  try {
		         FileWriter file = new FileWriter("./src/exports/processedmessages.json");
		         file.write((new Gson().toJson(pm).toString()));
		         file.close();
		      } catch (IOException e) {
		         // TODO Auto-generated catch block
		         e.printStackTrace();
		      }  
		      
		  System.out.println(new Gson().toJson(pm)); 
	 }

}
