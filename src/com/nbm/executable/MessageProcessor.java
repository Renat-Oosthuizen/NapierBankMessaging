package com.nbm.executable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

public class MessageProcessor {
	
	private String header; //Message header to be processed
	private String body; //Message body to be processed
	
	private String subject;
	private String messageText;
	private String sender;
	private String natureOfIncident;
	private String sortCode;
	
	private String processedMessage = "";	//This is the final message after the abbreviations have been expanded
	
	
	private LinkedList<String> hashtagList = new LinkedList(); //Linked list will hold the  Hashtags
	private LinkedList<String> mentionsList = new LinkedList(); //Linked list will hold the  Mentions
	private LinkedList<String> trendingList = new LinkedList(); //Linked list will hold the  Mentions
	private HashMap<String, String> abbreviations = new HashMap<String, String>(); //Hashmap to store abbreviations from the csv file
	private LinkedList<URL> quarantineList = new LinkedList(); //Linked list will hold the quarantined URLs
	private LinkedList<String> sirList = new LinkedList();//SIR List that must contain sort code and Nature of Incident
	
	//Default Constructor. Loads the abbreviations from the textwords.csv
	MessageProcessor()
	{
		getAbbreviations();	
	}
	
	//This function receives the message header and message body from the User Interface. It decides how the message should be processed.
	public void receiveMessage(String messageHeader, String messageBody)
	{
			header = messageHeader;
			body = messageBody;
			
			if (header.charAt(0) == 'S')
			{
				System.out.println("SMS");
				
				processSMS(body);
			}
			else if (header.charAt(0) == 'E')
			{
				System.out.println("Email");
				
				processEmail(body);
			}
			else if (header.charAt(0) == 'T')
			{
				System.out.println("Tweet");
				
				processTweet(body);
			}
			else
			{
				//Error if header does not start with a valid message identifier. Case sensitive.
				System.out.println("Header is not valid.");
			}
	}
	
	//This function processes SMS messages
	private void processSMS(String body)
	{
		expandAbbreviations(body);
	}
	
	//This function processes Email messages
	private void processEmail(String body)
	{
		
	}
	
	//This function processes Tweet messages
	private void processTweet(String body)
	{
		expandAbbreviations(body);
		findHashtags(body);
		findMentions(body);
	}
	
	//This function will separate the message into different sections
	private void separateMessage()
	{
		
	}
	
	//This function gets the abbreviations from textwords.csv and stores them in the abbreviations Hashmap for later use.
	private void getAbbreviations()
	{
		try 
		{
			   
			String fileContent[]; //Variable that stores textwords.csv contents
		
			FileReader file = new FileReader("./src/resources/textwords.csv"); //Reader for textwords.csv
			BufferedReader reader = new BufferedReader(file); //Buffered reader for textwords.csv
		
			String line = reader.readLine(); //Variable stores first line of textwords.csv
		
			while (line != null) //While line exists
			{ 
					fileContent = line.split(","); //Split each line at the comma
					abbreviations.put(fileContent[0], fileContent[1]);	//Add abbreviations to the hash map as key/value pairs for each line
					line = reader.readLine(); //read the next line.
			}
			
			file.close();	//Close the file reader to save memory
		}
		catch (IOException e) 
		{
				
			e.printStackTrace();
		}
	}
	
	//This method will find the hashtags in the tweet and store them in the hashtagList
	private void findHashtags(String message)
	{
		String[] splitMessage = message.split("\\s+");  //Split the message into individual words on white spaces and new lines.
		
        for ( String i : splitMessage)
        {
        	//
			if (i.startsWith("#"))
			{
				System.out.println("Adding: " + i);
				
				hashtagList.add(i);
			}

		}
		
		System.out.println(hashtagList);
        
	}
	
	//This method will find the mentions in the tweet and store them in the mentionsList
	private void findMentions(String message)
	{
		String[] splitMessage = message.split("\\s+");  //Split the message into individual words.
		
        for ( String i : splitMessage)
        {
        	//
			if (i.startsWith("@"))
			{

				System.out.println("Adding: " + i);
				
				mentionsList.add(i);
			}

		}
		
		System.out.println(mentionsList);
	}
	
	//This function finds abbreviations in the message and expands them
	private void expandAbbreviations(String message)
	{
		String[] splitMessage = message.split("(?=[.,!? ])");  //Split the message at each white space and .,!? while preserving the splitting character
		
		//Iterate through the split message and check if each word is an abbreviation
		for ( String i : splitMessage) 
		{
			
			//If there is an abbreviation then expand it and then add back the split punctuation to the processed message, otherwise add the word to the processed message
			if (abbreviations.containsKey(i.strip()))
			{
				processedMessage += (" <" + abbreviations.get(i.strip()) + ">");
			}
			else
			{
				processedMessage += i;
			}

		}
		
		System.out.println(processedMessage);
	}
	
	//This method will detect any URLs in the message body 
	// !!!Only detects things starting with http and https (not www). Any punctuation after the url will be appended Try this: https://github.com/linkedin/URL-Detector  !!!
	private void detectURL(String message)
	{
		
		String[] splitMessage = message.split(" ");  //Split the message into individual words.
		
        // Attempt to convert each item into an URL.   
        for ( String i : splitMessage)  try 
        {
            URL url = new URL(i);
            
            //If successful add the url to the quarantineList
            quarantineList.add(url);
            
            //Replace the url with <URL Quarantined> in processed message
            processedMessage += "<URL Quarantined> ";
            
            // If possible then replace with anchor...
            //System.out.print("<a href=\"" + url + "\">"+ url + "</a> " );    
        } 
        catch (MalformedURLException e) 
        {
            // If there was no URL then add the word to the processed message
        	processedMessage += ( i + " " );
            
        }
        
        System.out.println(processedMessage);
        System.out.println(quarantineList);
	}

}
