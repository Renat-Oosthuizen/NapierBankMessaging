package com.nbm.executable;

public class UIController {
	
	/**UI variable used to store in instance of the UI_SCreenOne class.*/
	private static UIScreenOne ui1;
	private static UI_ScreenTwo ui2;
	
	MessageProcessor messageProcessor = new MessageProcessor();
	
	/**
	 * Default constructor for the UI_Controller.
	 * It will then create an instance of the UI and make it visible.
	 */
	public UIController() {
		
		ui1 = new UIScreenOne(this); //ui1 is a new instance of class UI_ScreenOne that accepts the Controller class as a parameter.
		ui1.setVisible(true); //Make ui1 visible.
	}
	
	//This function will process the incoming message
	public void processMessage(String header, String body)
	{
		//Check that header and body are not empty
		if (!header.isEmpty() && !body.isEmpty())
		{
			messageProcessor.receiveMessage(header, body);
		}
		else
		{
			//Error if header or body are empty
			System.out.println("Header or Body is empty.");
		}
	}
	
	//Closes UI_ScreenOne and opens UI_ScreenTwo
	public void showScreenTwo()
	{
		ui2 = new UI_ScreenTwo(this); //ui2 is a new instance of class UI_ScreenTwo that accepts the Controller class as a parameter.
		ui2.setVisible(true); //Make ui2 visible.
		
		ui2.getTxtrListContent().setText("Placeholder1" + "Placeholder2" + "Placeholder3");;
		
		ui1.setVisible(false); //Make ui1 invisible.
		ui1.dispose(); //Destroy the ui1 JFrame object.
	}

}
