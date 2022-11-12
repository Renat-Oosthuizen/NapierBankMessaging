package com.nbm.executable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
	
	/**UI variable used to store in instance of the UI_SCreenOne class.*/
	private static UserInterface userInterface;
	
	/**
	 * Default constructor for the UI_Controller.
	 * It will then create an instance of the UI and make it visible.
	 */
	public UIController() {
		
		getAbbreviations(); //Loads the abbreviations from the textwords.csv
		userInterface = new UserInterface(this); //userInterface is a new instance of class UserInterface that accepts the Controller class as a parameter.
		userInterface.setVisible(true); //Make userInterface visible.
	}
	
	//This function will process the incoming message
	public void processMessage(String header, String body)
	{
		//Check that header and body are not empty
		if (!header.isEmpty() && !body.isEmpty())
		{
			userInterface.getTxtAreaProcessedMessage().setText(receiveMessage(header, body)); //Process the message, output it to the JSON file and set the Processed Message text in the UI
		}
		else
		{
			//Error if header or body are empty
			System.out.println("Header or Body is empty.");
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private String currentHeader; //Header of the message currently being processed
	private String processedMessage = "";	//Message text is temporarily stored here when URLs are being quarantined or abbreviations are being expanded. Once the message is processed it is written from here to the Message instance
	
	
	private LinkedList<String> hashtagList = new LinkedList(); //Linked list will hold the  Hashtags
	private LinkedList<String> mentionsList = new LinkedList(); //Linked list will hold mentions (@User)
	private LinkedList<String> trendingList = new LinkedList(); //Linked list will hold hashtags (#Party) 
	private HashMap<String, String> abbreviations = new HashMap<String, String>(); //HashMap to store abbreviations from the csv file
	private HashMap<String, ArrayList<String>> quarantineMap = new HashMap<String, ArrayList<String>>(); //HashMap will hold the quarantined URLs. Multiple URLs can be linked to the same header
	private LinkedList<String> sirList = new LinkedList();//SIR List that must contain sort code and Nature of Incident
	private HashMap<String, Message> messageMap = new HashMap<String, Message>(); //HashMap where the key is the header and the value is the Message object
	
	//--------------------------------------PREP AND RECEIVING MESSAGE------------------------------------------------------	
	
	//This function gets the abbreviations from textwords.csv and stores them in the abbreviations HashMap for later use.
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
	
	//This will convert the text input from the UI into XML format, it will then write the data into a Message object instance.
	private void xmlConverter(String xml) throws SAXException, IOException, ParserConfigurationException
	{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))); //Convert input string into xml
		
		//System.out.println("XML Conversion successful");

		NodeList messageNodes = doc.getElementsByTagName("Message"); //Store all Messages as nodes in a NodeList
		
		for (int i = 0; i < messageNodes.getLength(); i++) //For each message
		{
			Element element = (Element)messageNodes.item(i); //Get the first message
			
			Message message = new Message();

			message.setSender(element.getElementsByTagName("Sender").item(0).getTextContent());
			message.setSubject(element.getElementsByTagName("Subject").item(0).getTextContent());
			message.setSortCode(element.getElementsByTagName("SortCode").item(0).getTextContent());
			message.setNatureOfIncident(element.getElementsByTagName("NatureOfIncident").item(0).getTextContent());
			message.setText(element.getElementsByTagName("Text").item(0).getTextContent());
			
			currentHeader = element.getElementsByTagName("Header").item(0).getTextContent();
			messageMap.put(currentHeader, message);

		} 
		/*else //If there are no messages...
		{ 
			System.out.println("No Messages"); 
		}*/
	}
	
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
				Element element = (Element)messageNodes.item(i); //Get the first message
				
				Message message = new Message();

				message.setSender(element.getElementsByTagName("Sender").item(0).getTextContent());
				message.setSubject(element.getElementsByTagName("Subject").item(0).getTextContent());
				message.setSortCode(element.getElementsByTagName("SortCode").item(0).getTextContent());
				message.setNatureOfIncident(element.getElementsByTagName("NatureOfIncident").item(0).getTextContent());
				message.setText(element.getElementsByTagName("Text").item(0).getTextContent());
				
				currentHeader = element.getElementsByTagName("Header").item(0).getTextContent();
				messageMap.put(currentHeader, message);
				
				if (currentHeader.charAt(0) == 'S')
				{
					System.out.println("SMS");
					
					processSMS(messageMap.get(currentHeader).getText()); //Process the message
					
				}
				else if (currentHeader.charAt(0) == 'E')
				{
					System.out.println("Email");
					
					processEmail(messageMap.get(currentHeader).getText());
				}
				else if (currentHeader.charAt(0) == 'T')
				{
					System.out.println("Tweet");
					
					processTweet(messageMap.get(currentHeader).getText());
				}
				else
				{
					//Error if header does not start with a valid message identifier. Case sensitive.
					System.out.println("Header is not valid.");
				}

			} 

		  } 
		  catch (IOException e) 
		  {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
		      
		  }  
		
		userInterface.getTxtAreaProcessedMessage().setText(messageMap.get(currentHeader).getText());
	}
	
	//This function receives the message header and message body from the User Interface. It decides how the message should be processed.
	public String receiveMessage(String messageHeader, String messageBody)
	{
		processedMessage = ""; //Clear the processed message. So that it can be used with a new message
		
		//Convert the input from the GUI into an XML format, then write to the Message object
		try 
		{
			xmlConverter(messageHeader + messageBody);
		} 
		catch (SAXException | IOException | ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Identify the message type, perform input validation. If valid send message for further processing, otherwise produce error message
		if (currentHeader.charAt(0) == 'S')
		{
			System.out.println("SMS");
			
			processSMS(messageMap.get(currentHeader).getText()); //Process the message
			
		}
		else if (currentHeader.charAt(0) == 'E')
		{
			System.out.println("Email");
			
			processEmail(messageMap.get(currentHeader).getText());
		}
		else if (currentHeader.charAt(0) == 'T')
		{
			System.out.println("Tweet");
			
			processTweet(messageMap.get(currentHeader).getText());
		}
		else
		{
			//Error if header does not start with a valid message identifier. Case sensitive.
			System.out.println("Header is not valid.");
		}
		
		return messageMap.get(currentHeader).getText();
	}
	
	
	//--------------------------------MESSAGE PROCESSING----------------------------------------------------------
	
	//This function processes SMS messages
	private void processSMS(String body)
	{
		expandAbbreviations(body);
		convertJSON();
	}
	
	//This function processes Email messages
	private void processEmail(String body)
	{
		detectURL(body);
		convertJSON();
		
		System.out.println(currentHeader + ": " + quarantineMap.get(currentHeader));
		
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
	
	//This function processes Tweet messages
	private void processTweet(String body)
	{
		expandAbbreviations(body);
		findHashtags(body);
		findMentions(body);
		convertJSON();

	}
	
	//This function will separate the message into different sections
	/*private void separateMessage(String body)
	{
		splitMessage = body.split("\\s+");  //Matches any punctuation that is followed by a white space
	}*/
	
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
			  
	        FileWriter fileW = new FileWriter("./src/exports/processedmessages.json");
	        fileW.write( fileContent + builder.create().toJson(messageMap) + "\n");
	        fileW.close();
		  } 
		  catch (IOException e) 
		  {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
		      
		  }  
		      
		  System.out.println(builder.create().toJson(messageMap)); 
	 }

	
	//--------------------------------INPUT VALIDATION--------------------------------------------------------------------
	
	//This function will check that the input is of a valid format
	private void checkInput()
	{
		
	}
	
	//Checks if the email address is valid
	public static boolean isValidEmailAddress(String email) 
	{
		boolean valid = true;
		return valid = EmailValidator.getInstance().isValid(email);
	}

}
