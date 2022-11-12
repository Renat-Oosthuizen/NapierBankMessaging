package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.nbm.executable.*;

class TestCase1 {
	
	//MessageProcessor processor = new MessageProcessor(); //Instantiating the MessageProcessor class
	
	private String header1 = "<Message>"
			+ "<Header>T123456789</Header>";
	private String body1 = "<Sender>@JohnSmith</Sender>"
			+ "<Subject></Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Saw your message @JohnDoe ROTFL, can't wait to see you. Looking forward to seeing you too @JaneDoe #Party #Fireworks</Text>"
			+ "</Message>";
	
	private String header2 = "<Message>"
			+ "<Header>E123456789</Header>";
	private String body2 = "<Sender>janesmith@gmail.com</Sender>"
			+ "<Subject>Check out this website!</Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Check out this website https://www.freeformatter.com/java-regex-tester.html#ad-output ! Youtube.com is also cool.</Text>"
			+ "</Message>";
	
	private String header3 = "<Message>"
			+ "<Header>E123456789</Header>";
	private String body3 = "<Sender>john.doe@example.org</Sender>"
			+ "<Subject>SIR 20/10/2022</Subject>"
			+ "<SortCode>99-99-99</SortCode>"
			+ "<NatureOfIncident>Theft</NatureOfIncident>"
			+ "<Text>I can't find my favourite mug! It looks like the one from this website: www.ebay.com .</Text>"
			+ "</Message>";
	
	private String header4 = "<Message>"
			+ "<Header>S123456789</Header>";
	private String body4 = "<Sender>123456789012345</Sender>"
			+ "<Subject></Subject>"
			+ "<SortCode></SortCode>"
			+ "<NatureOfIncident></NatureOfIncident>"
			+ "<Text>Saw your message ROTFL can't wait to see you</Text>"
			+ "</Message>";

	@Test
	void receiveMessageTest1() 
	{
		
		//processor.receiveMessage(header1, body1);
		//processor.receiveMessage(header2, body2);
		//processor.receiveMessage(header3, body3);
		//processor.receiveMessage(header4, body4);
		
	}

}
