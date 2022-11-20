package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.nbm.executable.Message;
import com.nbm.executable.UIController;

/**
 * This class is for UNit Testing UIController functions.
 * @author Renat Oosthuizen
 * @since 20/11/2022
 * */
class UnitTests {
	
	private final String MESSAGE1 = "<Message>" //Valid Tweet
			+ "<Header>T123456789</Header>"
			+ "<Sender>@JohnSmith</Sender>"
			+ "<Subject></Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks</Text>"
			+ "</Message>";
	
	private final String MESSAGE2 = "<Message>" //Valid Email
			+ "<Header>E123456789</Header>"
			+ "<Sender>janesmith@gmail.com</Sender>"
			+ "<Subject>Check out this website!</Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Check out this website https://www.freeformatter.com/java-regex-tester.html#ad-output ! Youtube.com is also cool.</Text>"
			+ "</Message>";
	
	private final String MESSAGE3 = "<Message>" //Valid Email SIR
			+ "<Header>E123456789</Header>"
			+ "<Sender>john.doe@example.org</Sender>"
			+ "<Subject>SIR 20/10/2022</Subject>"
			+ "<SortCode>99-99-99</SortCode>"
			+ "<NatureOfIncident>Suspicious Incident</NatureOfIncident>"
			+ "<Text>I can't find my favourite mug! It looks like the one from this website: www.ebay.com .</Text>"
			+ "</Message>";
	
	private final String MESSAGE4 = "<Message>" //Valid SMS
			+ "<Header>S123456789</Header>"
			+ "<Sender>123456789012345</Sender>"
			+ "<Subject></Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Saw your message ROTFL can't wait to see you</Text>"
			+ "</Message>";
	
	private final String MESSAGE5 = "<Message" //Invalid XML
			+ "<Header>S123456789</Header>"
			+ "<Sender>123456789012345</Sender>"
			+ "<Subject></Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Saw your message ROTFL can't wait to see you</Text>"
			+ "</Message>";

	/**
	 *Test the importAbbreviations() function
	 */
	@Test 
	void importAbbreviationsTest()
	{
        
		UIController uic = new UIController(); //Instantiate UIController which will run importAbbreviations() in it's constructor
		assertTrue(uic.getAbbreviationsMap().size() == 255, "Failure: abbreviationsMap does not contain 255 items."); //Check that the function has imported the correct number of abbreviations into abbreviationsMap
	}
	
	/**
	 *Test the expandAbbreviations() function
	 */
	@ParameterizedTest
	@CsvSource(value = {"Yeah LOL . Funny:Yeah <Laughing out loud> . Funny", "Yeah LOL. Funny:Yeah <Laughing out loud>. Funny", "Yeah LOL, funny:Yeah <Laughing out loud>, funny", "Yeah LOL? Weird:Yeah <Laughing out loud>? Weird"}, delimiter = ':') //Input and expected output to test
	void expandAbbreviationsTest(String input, String expected)
	{
		UIController uic = new UIController(); //Create a new instance of UIController
		
		uic.setCurrentHeader("T123456789"); //Set the curerentHeader in the UIController
		uic.getMessageMap().put(uic.getCurrentHeader(), new Message()); //Add a blank Message to messageMap in UIController
		
		//Use reflection to access the private expandAbbreviations() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("expandAbbreviations", String.class);
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic,  input); //Run the expandAbbreviations() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		
		assertEquals(expected, uic.getMessageMap().get("T123456789").getText()); //Check that the abbreviations have been expanded correctly in the message
	}
	
	/**
	 *Test the findMentions() function
	 */
	@ParameterizedTest
	@CsvSource(value = {"Hello @Friend .:@Friend", "Hello @Friend.:@Friend", "Hello @Friend,:@Friend", "Hello @Friend!:@Friend", "Hello @Friend?:@Friend", "Hello Friend .:null", "Hello @Friend1 and @Friend2 .:@Friend1", "Hello @Friend1 and @Friend2 .:@Friend2"}, delimiter = ':') //Input and expected output to test
	void findMentionsTest(String input, String expected)
	{
		UIController uic = new UIController(); //Create a new instance of UIController
		
		//Use reflection to access the private expandAbbreviations() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("findMentions", String.class);
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic,  input); //Run the expandAbbreviations() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		
		if (uic.getMentionsList().size() > 0 && !expected.equals("null"))
		{
			assertTrue(uic.getMentionsList().contains(expected)); //Check that mentionsList contains the correct value if there is a word starting with @
		}
		else
		{
			assertEquals(expected, "null"); //Pass the test if mentionsList is expected to be empty and is empty
		}

	}
	
	/**
	 *Test the findHashtags() function
	 */
	@ParameterizedTest
	@CsvSource(value = {"I like #Party .:#Party: 1", "I like #Party.:#Party: 1", "I like #Party,:#Party: 1", "I like #Party!:#Party: 1", "I like #Party?:#Party: 1", "I like Party .:null: null", "I like #Party1 and #Party2 .:#Party1: 1", "I like #Party1 and #Party2 .:#Party2: 1", "I like #Party1 and #Party1 .:#Party1: 2"}, delimiter = ':') //Input, expected key and expected value                   
	void findHashtagsTest(String input, String expected1, String expected2)
	{
		UIController uic = new UIController(); //Create a new instance of UIController
		
		//Use reflection to access the private findHashtags() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("findHashtags", String.class);
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic,  input); //Run the findHashtags() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		
		if (uic.getTrendingMap().size() > 0 && !expected1.equals("null"))
		{
			assertTrue(uic.getTrendingMap().get(expected1) == Integer.parseInt(expected2)); //Check that trendingMap contains the correct value and occurrence number if word starts with #
		}
		else
		{
			assertEquals(expected1, "null"); //Pass the test if trendingMap is expected to be empty and is empty
		}
	}
	
	/**
	 *Test the detectURL() function
	 */
	@ParameterizedTest
	@CsvSource(value = {"I like youtube.com and https://app.diagrams.net/.|I like <URL Quarantined> and <URL Quarantined>|youtube.com|https://app.diagrams.net/. ", "I like cookies.|I like cookies.|null|null"}, delimiter = '|')
	void detectURLTest(String input, String expected1, String expected2, String expected3)
	{
		UIController uic = new UIController(); //Create a new instance of UIController
		
		uic.setCurrentHeader("T123456789"); //Set the curerentHeader in the UIController
		uic.getMessageMap().put(uic.getCurrentHeader(), new Message()); //Add a blank Message to messageMap in UIController
		
		//Use reflection to access the private detectURL() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("detectURL", String.class);
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic,  input); //Run the detectURL() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		
		if (uic.getQuarantineMap().size() > 0 && !expected2.equals("null"))
		{
			assertEquals(expected1, uic.getMessageMap().get("T123456789").getText()); //Check that URLs have been removed from the message
			assertTrue(uic.getQuarantineMap().get("T123456789").contains(expected2) && uic.getQuarantineMap().get("T123456789").contains(expected3)); //Check that quarantineMap contains the correct data
		}
		else
		{
			assertEquals(expected1, uic.getMessageMap().get("T123456789").getText()); //Pass the test if quarantineMap is expected to be empty and is empty
		}
	}
	
	/**
	 *Test the importXML() function
	 */
	@ParameterizedTest
	@CsvSource(value = {"./src/test/input.xml|1", "./src/test/malformed input.xml|2"}, delimiter = '|')
	void importXML(String input, String id)
	{
		
		File file = new File(input);
		
		UIController uic = new UIController();

		uic.importXML(file);
		
		if (id.equals("1"))
		{
			//Confirm that the last message from a well formatted XML file has been created
			assertTrue(
						uic.getMessageMap().get(uic.getCurrentHeader()).getSender().equals("123456789012345") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getSubject().equals("") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getSortCode().equals("") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getNatureOfIncident().equals("") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getText().contains("Saw your message "));
		}
		else if (id.equals("2"))
		{
			assertTrue(uic.getMessageMap().isEmpty()); //Confirm that a message has not been created from the malformed XML file
		}

	}
	
	/**
	 *Test the importText() function
	 */
	@ParameterizedTest
	@ValueSource(strings = {MESSAGE3, MESSAGE5})
	 void importText(String input)
	 {
		
		UIController uic = new UIController();

		uic.importText(input);
		
		if (input.equals(MESSAGE3))
		{
			
			//Confirm that a message has been created from the well formatted XML (manual input)
			assertTrue(
						uic.getMessageMap().get(uic.getCurrentHeader()).getSender().equals("john.doe@example.org") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getSubject().equals("SIR 20/10/2022") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getSortCode().equals("99-99-99") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getNatureOfIncident().equals("Suspicious Incident") &&
						uic.getMessageMap().get(uic.getCurrentHeader()).getText().contains("I can't find my favourite mug! It looks like the one from this ")
					);

		}
		else if (input.equals(MESSAGE5))
		{
			assertTrue(uic.getMessageMap().isEmpty()); //Confirm that a message has not been created from the malformed XML file (manual input)
		}
	 }
	
	
	/**
	 *Test the convertJSON() function
	 */
	@Test
	void convertJSONTest()
	{
		new File("./src/exports/processedmessages.json").delete(); //Delete the JSON file
		
		UIController uic = new UIController(); //Instantiate new UIController
		
		//Create a new message
		Message message = new Message();
		message.setSender("@JohnSmith");
		message.setText("Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks");
		uic.setCurrentHeader("T123456789"); //Set the curerentHeader in the UIController
		
		uic.getMessageMap().put("T123456789", message); //Place the message in the messageMap HashMap
		
		//Use reflection to access the private convertJSON() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("convertJSON");
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic); //Run the convertJSON() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		//Read contents of the JSON file
		try 
		{
			
			String fileContent = ""; //Variable that stores processedmessages.json contents
			
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
			
			//Check that the JSON file has been successfully created and the message added
			assertEquals("{\"T123456789\":{\"sender\":\"@JohnSmith\",\"subject\":\"\",\"sortCode\":\"\",\"natureOfIncident\":\"\",\"text\":\"Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks\"}}\n", fileContent); 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		uic.getMessageMap().clear(); //Clear the messageMap as is done at the start of every file processing
		uic.setCurrentHeader("T123456780"); //Set the curerentHeader in the UIController
		uic.getMessageMap().put("T123456780", message); //Place the message in the messageMap HashMap
		
		
		//Use reflection to access the private convertJSON() function
		try 
		{
			Method method = uic.getClass().getDeclaredMethod("convertJSON");
			method.setAccessible(true);
			
			try 
			{
				method.invoke(uic); //Run the convertJSON() function
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.toString());
		}
		
		//Read contents of the JSON file
		try 
		{
			String fileContent = ""; //Variable that stores processedmessages.json contents
			
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
			
			//Check that the 2nd message has been successfully appended to the JSON file
			assertEquals("{\"T123456789\":{\"sender\":\"@JohnSmith\",\"subject\":\"\",\"sortCode\":\"\",\"natureOfIncident\":\"\",\"text\":\"Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks\"}, \n"
					+ "\"T123456780\":{\"sender\":\"@JohnSmith\",\"subject\":\"\",\"sortCode\":\"\",\"natureOfIncident\":\"\",\"text\":\"Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks\"}}\n"
					, fileContent); 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}

}
