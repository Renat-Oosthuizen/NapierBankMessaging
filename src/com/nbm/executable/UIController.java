package com.nbm.executable;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.validator.routines.EmailValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.GsonBuilder;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

public class UIController {
	
	private static UserInterface userInterface;
	
	private HashMap<String, String> abbreviations = new HashMap<String, String>(); //HashMap to store abbreviations from the csv file
	
	public String currentHeader; //Header of the message currently being processed
	private String processedMessage = "";	//Message text is temporarily stored here when URLs are being quarantined or abbreviations are being expanded. Once the message is processed it is written from here to the Message instance
	private HashMap<String, Message> messageMap = new HashMap<String, Message>(); //HashMap where the key is the header and the value is the Message object. This was chosen as it allows for easily accessible nested JSON objects
	
	//private LinkedList<String> hashtagList = new LinkedList(); //Linked list will hold the  Hashtags
	private LinkedList<String> mentionsList = new LinkedList<String>(); //Linked list will hold mentions (@User)
	private HashMap<String, Integer> trendingMap = new HashMap<String, Integer>(); //HashMap will hold hashtags (#Party) and number of occurrences
	private LinkedList<String> sirList = new LinkedList<String>();//SIR LinkedList that will contain Sort Code and Nature of Incident
	private HashMap<String, ArrayList<String>> quarantineMap = new HashMap<String, ArrayList<String>>(); //HashMap will hold the quarantined URLs. Multiple URLs can be linked to the same header
	
	public UIController() {
		
		getAbbreviations(); //Loads the abbreviations from the textwords.csv
		userInterface = new UserInterface(this); //userInterface is a new instance of class UserInterface that accepts the Controller class as a parameter.
		userInterface.setVisible(true); //Make userInterface visible.
	}
	
	//--------------------------------------PREP AND RECEIVING MESSAGE------------------------------------------------------	
	
	//This function gets the abbreviations from textwords.csv and stores them in the abbreviations HashMap for later use. Runs when class is instantiated.
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
	
	//This function imports the user selected XML file from the UI and attempts to process each message in the file.
	public void importXML(File file) throws SAXException, IOException, ParserConfigurationException
	{
		
		String fileContent = ""; //Variable that stores the file contents
		
		try 
		{
			//Read the file and store it's contents
			FileReader fileR = new FileReader(file.getAbsolutePath()); //Reader for the xml file
			BufferedReader reader = new BufferedReader(fileR); //Buffered reader for xml file
		
			String line = reader.readLine(); //Variable stores first line of xml file
		
			while (line != null) //While line exists
			{ 
					fileContent += (line + "\n"); //...add line to fileContent and...
					line = reader.readLine(); //read the next line.
			}
			fileR.close(); //close the file reader
			  
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(fileContent))); //Convert input string into xml
			
			NodeList messageNodes = doc.getElementsByTagName("Message"); //Store all Messages as nodes in a NodeList
			
			for (int i = 0; i < messageNodes.getLength(); i++) //For each message
			{
				processedMessage = ""; //Clear the processed message. So that it can be used with a new message
				
				Element element = (Element)messageNodes.item(i); //Get the first message
				
				Message message = new Message();

				message.setSender(element.getElementsByTagName("Sender").item(0).getTextContent());
				message.setSubject(element.getElementsByTagName("Subject").item(0).getTextContent());
				message.setSortCode(element.getElementsByTagName("SortCode").item(0).getTextContent());
				message.setNatureOfIncident(element.getElementsByTagName("NatureOfIncident").item(0).getTextContent());
				message.setText(element.getElementsByTagName("Text").item(0).getTextContent());
				
				currentHeader = element.getElementsByTagName("Header").item(0).getTextContent();
				messageMap.put(currentHeader, message);
				
				checkInput(); //Check that message format is valid and determine message type

			} 

		  } 
		  catch (IOException e) 
		  {
			 userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
	         userInterface.getLblProcessInfo().setText("Problem processing XML file. File is malformed."); //Display error message      
		  }  
		
	}
	
	//This will convert the text input from the UI into XML format, it will then write the data into a Message object instance.
	public void importText(String xml) throws SAXException, IOException, ParserConfigurationException
	{
		
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))); //Convert input string into xml

			NodeList messageNodes = doc.getElementsByTagName("Message"); //Store all Messages as nodes in a NodeList
			
			for (int i = 0; i < messageNodes.getLength(); i++) //For each message
			{
				processedMessage = ""; //Clear the processed message. So that it can be used with a new message
				
				Element element = (Element)messageNodes.item(i); //Get the first message
				
				Message message = new Message();

				message.setSender(element.getElementsByTagName("Sender").item(0).getTextContent());
				message.setSubject(element.getElementsByTagName("Subject").item(0).getTextContent());
				message.setSortCode(element.getElementsByTagName("SortCode").item(0).getTextContent());
				message.setNatureOfIncident(element.getElementsByTagName("NatureOfIncident").item(0).getTextContent());
				message.setText(element.getElementsByTagName("Text").item(0).getTextContent());
				
				currentHeader = element.getElementsByTagName("Header").item(0).getTextContent();
				messageMap.put(currentHeader, message);
				
				checkInput(); //Check that message format is valid and determine message type
				
			} 
		}
		catch (IOException e) 
		{
			userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
	        userInterface.getLblProcessInfo().setText("Input text format not valid."); //Display error message      
		}  

	}
	
	//--------------------------------INPUT VALIDATION--------------------------------------------------------------------
	
		//This function will figure out the type of message present and check that the input is of a valid format
		public void checkInput()
		{
			
			//Check that the header is valid
			if (!currentHeader.matches("[S|T|E]\\d{9}"))
			{

				userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
		        userInterface.getLblProcessInfo().setText("Header " + currentHeader + " is not of valid format."); //Display error message 
		        
				return; //Do not proceed further
			}
			
			//Check that the SMS is valid
			if (currentHeader.charAt(0) == 'S')
			{
				if	( 	
						messageMap.get(currentHeader).getSender().length() < 16 &&
						messageMap.get(currentHeader).getSubject().length() == 0 &&
						messageMap.get(currentHeader).getSortCode().length() == 0 &&
						messageMap.get(currentHeader).getNatureOfIncident().length() == 0 &&
						messageMap.get(currentHeader).getText().length() < 141
					)
				{
					processSMS(messageMap.get(currentHeader).getText()); //Process the SMS message
				}
				else
				{
					userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
			        userInterface.getLblProcessInfo().setText("SMS with header " + currentHeader + " is not of valid format."); //Display error message 
			        
					return; //Do not proceed further
				}
				
			}
			
			//Check that the Tweet is valid
			if (currentHeader.charAt(0) == 'T')
			{
				if	( 	
						messageMap.get(currentHeader).getSender().charAt(0) == '@' &&
						messageMap.get(currentHeader).getSender().length() < 16 &&
						messageMap.get(currentHeader).getSubject().length() == 0 &&
						messageMap.get(currentHeader).getSortCode().length() == 0 &&
						messageMap.get(currentHeader).getNatureOfIncident().length() == 0 &&
						messageMap.get(currentHeader).getText().length() < 141
					)
				{
					processTweet(messageMap.get(currentHeader).getText()); //Process the Tweet message
				}
				else
				{
					userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
			        userInterface.getLblProcessInfo().setText("Tweet with header " + currentHeader + " is not of valid format."); //Display error message 
			        
					return; //Do not proceed further
				}
			}
			
			//Check that the Email is valid
			if (currentHeader.charAt(0) == 'E')
			{
				
				final String DATE_FORMAT = "dd/MM/yy"; //The date format used to verify that the subject contains a valid date
				final List<String> noiList = Arrays.asList("Theft", "Staff Attack", "ATM Theft", "Raid", "Customer Attack", "Staff Abuse", "Bomb Threat", "Terrorism", "Suspicious Incident", "Intelligence", "Cash Loss"); //This list contains the valid nature of incident contents.
				boolean validDate;
				
		        //Split subject at the first number and keep the splitting character
		        String[] splitSubject = messageMap.get(currentHeader).getSubject().split("(?=\\d)", 2);
		        
		        //Check if second part of the subject line is a valid date (there may not be a second part of the subject line, this is acceptable)
		        try 
		        {
		            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		            df.setLenient(false);
		            df.parse(splitSubject[1]);
		            validDate = true;
		        } 
		        catch (ParseException e) 
		        {
		        	validDate = false;
		        }
		        
		        //If SIR email
		        if (splitSubject[0].equals("SIR ") && validDate)
		        {
		        	if	( 	
							EmailValidator.getInstance().isValid(messageMap.get(currentHeader).getSender()) && //Valid email address
							messageMap.get(currentHeader).getSubject().length() < 21 && //Subject is 20 chars or less
							messageMap.get(currentHeader).getSortCode().matches("\\d{2}-\\d{2}-\\d{2}") && //Sort code is of valid format
							noiList.contains(messageMap.get(currentHeader).getNatureOfIncident()) && //Check that the nature of incident is one of a list of valid phrases
							messageMap.get(currentHeader).getText().length() < 1029 //Text is 1028 chars or less
						)
					{
		        		processEmail(messageMap.get(currentHeader).getText()); //Process the SIR Email message
					}
					else
					{
						userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
				        userInterface.getLblProcessInfo().setText("SIR Email with header " + currentHeader + " is not of valid format."); //Display error message 
				        
						return; //Do not proceed further
					}
		        }
		        else if	( 	//If regular email
						EmailValidator.getInstance().isValid(messageMap.get(currentHeader).getSender()) && //Valid email address
						messageMap.get(currentHeader).getSubject().length() < 21 && //Subject is 20 chars or less
						messageMap.get(currentHeader).getSortCode().length() == 0 && //Sort code is empty
						messageMap.get(currentHeader).getNatureOfIncident().length() == 0 && //Nature of incident is empty
						messageMap.get(currentHeader).getText().length() < 1029 //Text is 1028 chars or less
					)
				{
		        	processEmail(messageMap.get(currentHeader).getText()); //Process the Regular Email message
				}
				else
				{
					userInterface.getLblProcessInfo().setForeground(Color.RED); //Change info message colour to red
			        userInterface.getLblProcessInfo().setText("Regular Email with header " + currentHeader + " is not of valid format."); //Display error message 
			        
					return; //Do not proceed further
				}

			}
			
		}
	
	
	//--------------------------------MESSAGE PROCESSING----------------------------------------------------------
	
	//This function processes SMS messages
	private void processSMS(String body)
	{
		expandAbbreviations(body); //Expand abbreviations in the message
		
		userInterface.getTxtAreaProcessedMessage().setText(messageMap.get(currentHeader).getText()); //Display the processed text
		userInterface.getLblProcessInfo().setForeground(Color.GREEN); //Change info message colour to green
        userInterface.getLblProcessInfo().setText("Message " + currentHeader + " processed successfully"); //Display success message
        
		convertJSON(); //Add message to the JSON file
	}
	
	//This function processes Email messages
	private void processEmail(String body)
	{
		detectURL(body); //Find and remove URLS in the message, add them to the qurantineMap
		
		//If Email is an SIR, put SIR data into sirMap
		if (messageMap.get(currentHeader).getSortCode().length() > 0)
		{
			sirList.push(messageMap.get(currentHeader).getSortCode() + " : " + messageMap.get(currentHeader).getNatureOfIncident());
		}
		
		userInterface.getTxtAreaProcessedMessage().setText(messageMap.get(currentHeader).getText()); //Display the processed text
		userInterface.getLblProcessInfo().setForeground(Color.GREEN); //Change info message colour to green
        userInterface.getLblProcessInfo().setText("Message " + currentHeader + " processed successfully"); //Display success message
        
		convertJSON(); //Add message to the JSON file
		
	}
	
	//This function processes Tweet messages
	private void processTweet(String body)
	{
		expandAbbreviations(body); //Expand abbreviations in the message
		findHashtags(body); //Find hashtags in the message and add them to the trending list
		findMentions(body); //Find mentions in the message and add them to the mentions list
		
		userInterface.getTxtAreaProcessedMessage().setText(messageMap.get(currentHeader).getText()); //Display the processed text
		userInterface.getLblProcessInfo().setForeground(Color.GREEN); //Change info message colour to green
        userInterface.getLblProcessInfo().setText("Message " + currentHeader + " processed successfully"); //Display success message

		convertJSON(); //Add message to the JSON file
	}
	
	//This will detect check if the provided word is a URL
	private void detectURL(String text) //Only used for email bodies
	{
		String qurantineMessage = "<URL Quarantined> "; //Text to replace a quarantined URL
		ArrayList<String> values = new ArrayList<String>(); //This stores quarantineMap values until they are added to the quarantineMap
		
		String[] splitMessage = text.split("\\s+");  //Split the message into individual words on white spaces and new lines.
		
		for (int i = 0; i < splitMessage.length; i++) //For each word
		{
			//Check if the word is a URL
			UrlDetector parser = new UrlDetector(splitMessage[i], UrlDetectorOptions.Default); 
			List<Url> found = parser.detect();
		    
		    if (found.isEmpty()) //If not URL then add word to the processed message
		    {
		    	processedMessage += splitMessage[i] + " ";
		    }
		    else //If URL then quarantine and replace word with quarantined message
		    {
		    	values.add(splitMessage[i]);
		    	processedMessage += qurantineMessage;
		    }
		}

		quarantineMap.put(currentHeader, values);
		messageMap.get(currentHeader).setText(processedMessage); //Set text of the message to the processed message
	    
	}
	
	//This method will find the hashtags in the tweet and store them in the hashtagList
	private void findHashtags(String message)
	{
		String[] splitMessage = message.split("\\s+");  //Split the message into individual words on white spaces and new lines.
		int count;
		
        for ( String i : splitMessage)
        {
        	//
			if (i.startsWith("#"))
			{
				System.out.println("Adding: " + i);
				
				//hashtagList.add(i);
				
				//Return the current count of the hashtag, put 1 in trending HashMap for count if hashtag is new
				//If hashtag is already in HashMap, increment it's count by 1
				if (trendingMap.putIfAbsent(i, 1) != null)
				{
					count = trendingMap.get(i);
					trendingMap.put(i, count + 1);
				}				
				
			}

		}
		
		//System.out.println(hashtagList);
        
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
	private void expandAbbreviations(String text)
	{
		String[] splitMessage = text.split("(?=[.,!? ])");  //Split the message at each white space and .,!? while preserving the splitting character
		
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
		
		messageMap.get(currentHeader).setText(processedMessage);;
		//System.out.println(processedMessage);
	}
	
	//-------------------------------JSON CONVERSION---------------------------------------------------------------------
	
	//Serialise the Message instance as JSON in processedmessages.json file
	private void convertJSON()
	{
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping(); //Create a GsonBuilder object with the HTML Escape parameter disabled. This prevents some characters from being written in unicode
		String fileContent = ""; //Variable that stores processedmessages.json contents
		
		//Check that file exists and create one if it does not
		if (! new File("./src/exports/processedmessages.json").isFile()) //If file does not exist...
		{
			try 	
			{
				File file = new File("./src/exports/processedmessages.json");	//...then create it.
				file.createNewFile();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		try 
		{
			//Read the file and store it's contents
			FileReader fileR = new FileReader("./src/exports/processedmessages.json"); //Reader for processedmessages.json
			BufferedReader reader = new BufferedReader(fileR); //Buffered reader for processedmessages.json
		
			String line = reader.readLine(); //Variable stores first line of processedmessages.json
		
			while (line != null) //While line exists
			{ 
				fileContent += (line + "\n"); //...add line to fileContent and...
				line = reader.readLine(); //read the next line.
			}
			
			fileR.close();
			
			
			//If file already contains an entry, then add a comma after the last entry, otherwise add a starting bracket as all JSON is written without a starting bracket later
			if (!fileContent.equals("")) 
			{
				String temp;
				temp = fileContent.substring(0, fileContent.length() - 2); //Remove the end bracket

				fileContent = temp + ", \n";
			}
			else
			{
				fileContent +="{";
			}
			 
			//Write the original file contents and new data back into the file
	        FileWriter fileW = new FileWriter("./src/exports/processedmessages.json");
	        fileW.write( fileContent + builder.create().toJson(messageMap).substring(1) + "\n"); //Write the original file + new JSON data (minus starting bracket)
	        fileW.close();
	        
	        messageMap.clear(); //Clear the messageMap so that it only ever contains a single object to be written to the file.
		  } 
		  catch (IOException e) 
		  {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
		      
		  }  
		      
		  //System.out.println(builder.create().toJson(messageMap)); 
	 }

	//---------------------------------------GETTERS-----------------------------------------------
	public LinkedList<String> getMentionsList() {
		return mentionsList;
	}
	
	public HashMap<String, Integer> getTrendingMap() {
		return trendingMap;
	}

	public LinkedList<String> getSirList() {
		return sirList;
	}

	//---------------------------------------SETTERS-----------------------------------------------
	public void setMentionsList(LinkedList<String> mentionsList) {
		this.mentionsList = mentionsList;
	}

	public void setTrendingMap(HashMap<String, Integer> trendingMap) {
		this.trendingMap = trendingMap;
	}

	public void setSirList(LinkedList<String> sirList) {
		this.sirList = sirList;
	}

}
