package com.nbm.executable;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.commons.validator.routines.EmailValidator;






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
	private LinkedList<String> quarantineList = new LinkedList(); //LinkedList will hold the quarantined URLs
	private LinkedList<String> sirList = new LinkedList();//SIR List that must contain sort code and Nature of Incident
	private String[] splitMessage;
	//private String xml = "";
	private String xml = "<Message>\r\n"
			+ "    <Header>T123456789</Header>\r\n"
			+ "    <Sender>123456789012345</Sender>\r\n"
			+ "    <Subject></Subject>\r\n"
			+ "    <SortCode></SortCode>\r\n"
			+ "    <NatureOfIncident></NatureOfIncident>\r\n"
			+ "    <Text>Saw your message @JohnDoe ROTFL, can’t wait to see you.\r\n"
			+ "Looking forward to seeing you too @JaneDoe \r\n"
			+ " #Party #Fireworks</Text>\r\n"
			+ "</Message>";
	
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
		separateMessage(body);
		
		for (int i = 0; i < splitMessage.length; i++)
		{
			detectURL(splitMessage[i]);
		}
		
		System.out.println(processedMessage);
		System.out.println(quarantineList.get(0));
		
	}
	
	//This function processes Tweet messages
	private void processTweet(String body)
	{
		//expandAbbreviations(body);
		//findHashtags(body);
		//findMentions(body);
		try {
			xmlConverter();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//This function will separate the message into different sections
	private void separateMessage(String body)
	{
		splitMessage = body.split("\\s+");  //Matches any punctuation that is followed by a white space
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
	
	//This will detect check if the provided word is a URL
	private void detectURL(String word) //Only used for email bodies
	{
		//Email bodies will not have email addresses!
		//Split punctuation if it's at the end of a word ?
		UrlDetector parser = new UrlDetector(word, UrlDetectorOptions.Default);
		List<Url> found = parser.detect();
		
		String qurantineMessage = "<URL Quarantined> ";
	    
	    if (found.isEmpty())
	    {
	    	processedMessage += word + " ";
	    }
	    else
	    {
	    	quarantineList.add(word);
	    	processedMessage += qurantineMessage;
	    }
	    
	}
	
	//Checks if the email address is valid
	public static boolean isValidEmailAddress(String email) 
	{
		boolean valid = true;
		return valid = EmailValidator.getInstance().isValid(email);
	}
	
	//This will convert the text input from the UI into XML format
	private void xmlConverter() throws SAXException, IOException, ParserConfigurationException
	{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))); //Convert input string into xml
		
		System.out.println("XML Conversion successiful");

		NodeList messageNodes = doc.getElementsByTagName("Message"); //Store all Messages as nodes in a NodeList
		
		if (messageNodes.getLength() > 0) //If there are any messages...
		{
			Element message = (Element)messageNodes.item(0); //Get the first message
			System.out.println(message.getElementsByTagName("Text").item(0).getTextContent()); //Get contents from the message tag "Text"
		} 
		else //If there are no messages...
		{ 
			System.out.println("No Messages"); 
		}
	}
	
	//This function will check that the input is of a valid format
	private void checkInput()
	{
		
	}
}
