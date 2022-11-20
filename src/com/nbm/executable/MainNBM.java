package com.nbm.executable;

/**
 * Main class that launches the application.
 * This application will process SMS, Tweet, Email and Email Significant Incident Report messages that the application receives in an XML format via manual input or by importing a file.
 * The processed message is then displayed to the user and also appended into a JSON file. Data from the application such as username mentions, trending hash tags and Significant Incident Report data can be viewed by the user.
 * Quarantined URLs are also stored in the system but are not used and are not available to be viewed.
 * @author Renat Oosthuizen
 * @since 20/11/2022
 * */
public class MainNBM {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		new UIController(); //Create an instance of the UIController

	}

}
