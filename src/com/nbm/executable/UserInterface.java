package com.nbm.executable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import java.awt.ScrollPane;


/**
 * This is the graphical user interface for the application. It accepts UIController as a parameter for data binding purposes.
 * It uses a card layout and contains two screen. The first screen allows the user to import messages into the application. It also displays the processed messages.
 * The second screen displays additional data from the application such as username mentions, trending hash tags and Significant Incident Report.
 * @author Renat Oosthuizen
 * @since 20/11/2022
 * */
@SuppressWarnings("serial")
public class UserInterface extends JFrame{

	private UIController myController; //LoginController variable that performs all the logic for this screen.

	private CardLayout cardLayout = new CardLayout(0, 0); //Create card layout with edges of 0 pixels
	private JPanel contentPane = new JPanel(); //Assign contentPane to be a new instance of JPanel. This is the main panel of the JFrame
	private JPanel card1 = new JPanel(); //Card 1, first screen where the user can import messages from file or via text for processing
	private JPanel card2 = new JPanel(); //Card 2, 2nd screen where the user can see the Mentions List, SIR and Trending List
	
	private JLabel lblHeader = new JLabel("Message Header:"); //Label for the message header text area
	private JLabel lblBody = new JLabel("Message Body:"); //Label for the message body text area
	private JTextArea txtAreaHeader = new JTextArea(); //Message Header text area
	private JTextArea txtAreaBody = new JTextArea(); //Message Body text area
	
	private JButton btnImportFile = new JButton("Import File"); //Button to import the XML file
	private File file = null; //This stores the file for processing.
	private JLabel lblFilePath = new JLabel(""); //Label that displays the file path of the selected XML file
	private JButton btnProcessFile = new JButton("Process File"); //Button to process the XML file
	
	
	private JButton btnProcessText = new JButton("Process Text"); //Button to process the text from the Message Header and the Message Body
	private JLabel lblProcessInfo = new JLabel(""); //Label that provides information about processing the text (success or error messages)
	
	private JButton btnViewLists = new JButton("View Lists"); //Button opens card2 and displays the Mentions List, SIR and Trending List
	
	private JLabel lblProcessedMessage = new JLabel("Latest Processed Message:"); //Label for the processed message text area
	private JTextArea txtAreaProcessedMessage = new JTextArea(); //Processed message goes here
	
	private JLabel lblMentionsList = new JLabel("Mentions List:"); //Label for the mentionsList in card2
	private final ScrollPane scrollPaneMentionsList = new ScrollPane(); //Scroll panel containing txtAreaMentionsList, this allows the text area to be scrollable
	private JTextArea txtAreaMentionsList = new JTextArea(); //Text area that displays the mentionsList
	
	private final JLabel lblTrendingList = new JLabel("Trending List:"); //Label for the trendingList in card2
	private final ScrollPane scrollPaneTrendingList = new ScrollPane(); //Scroll panel containing txtAreaTrendingList, this allows the text area to be scrollable
	private JTextArea txtAreaTrendingList = new JTextArea(); //Text area that displays the trendingList
	
	private final JLabel lblSIRList = new JLabel("SIR List:"); //Label for the SIRList in card2
	private final ScrollPane scrollPaneSIRList = new ScrollPane(); //Scroll panel containing txtAreaSIRList, this allows the text area to be scrollable
	private JTextArea txtAreaSIRList = new JTextArea(); //Text area that displays the SIRList
	
	private JButton btnReturn = new JButton("Add More Messages"); //Button in card2 allowing the user to return to card1 in order to import more messages
	
	public UserInterface(UIController controller) 
	{
		myController = controller; //Assign myController as an instance of UI_Controller
		this.setTitle("Napier Bank Messaging"); //Set title for the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //The application will quit when the JFrame is closed.
		this.setBounds(0,0,1280, 720); //(0, 0, 1920, 1080); //JFrame is created at coordinates 0, 0 of screen with 1920x1080 pixel dimensions.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //Create a 5 pixel border around all sides of the contentPane.
		this.setContentPane(contentPane); //Set content of the JFrame to contentPane JPanel
		
		contentPane.setLayout(cardLayout); //Set layout of the contentPane to CardLayout
		
		//Set the GridBag layout for card1 and it's components
		GridBagLayout gbl_card1 = new GridBagLayout();
		gbl_card1.columnWidths = new int[]{100, 400, 0, 0, 0, 0, 400, 100};
		gbl_card1.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_card1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_card1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		card1.setBackground(Color.LIGHT_GRAY);
		card1.setLayout(gbl_card1);
		
		GridBagConstraints gbc_lblHeader = new GridBagConstraints();
		gbc_lblHeader.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblHeader.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeader.gridx = 1;
		gbc_lblHeader.gridy = 7;
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 12));
		card1.add(lblHeader, gbc_lblHeader);
		
		GridBagConstraints gbc_lblProcessedMessage = new GridBagConstraints();
		gbc_lblProcessedMessage.anchor = GridBagConstraints.WEST;
		gbc_lblProcessedMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblProcessedMessage.gridx = 6;
		gbc_lblProcessedMessage.gridy = 7;
		lblProcessedMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		card1.add(lblProcessedMessage, gbc_lblProcessedMessage);
		
		GridBagConstraints gbc_txtAreaHeader = new GridBagConstraints();
		gbc_txtAreaHeader.insets = new Insets(0, 0, 5, 5);
		gbc_txtAreaHeader.fill = GridBagConstraints.BOTH;
		gbc_txtAreaHeader.gridx = 1;
		gbc_txtAreaHeader.gridy = 8;
		txtAreaHeader.setWrapStyleWord(true);
		txtAreaHeader.setLineWrap(true);
		card1.add(txtAreaHeader, gbc_txtAreaHeader);
		
		GridBagConstraints gbc_txtAreaProcessedMessage = new GridBagConstraints();
		gbc_txtAreaProcessedMessage.insets = new Insets(0, 0, 5, 5);
		gbc_txtAreaProcessedMessage.fill = GridBagConstraints.BOTH;
		gbc_txtAreaProcessedMessage.gridx = 6;
		gbc_txtAreaProcessedMessage.gridy = 8;
		txtAreaProcessedMessage.setWrapStyleWord(true);
		txtAreaProcessedMessage.setLineWrap(true);
		txtAreaProcessedMessage.setEditable(false);
		card1.add(txtAreaProcessedMessage, gbc_txtAreaProcessedMessage);
		
		GridBagConstraints gbc_btnImportFile = new GridBagConstraints();
		gbc_btnImportFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnImportFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnImportFile.gridx = 3;
		gbc_btnImportFile.gridy = 9;
		card1.add(btnImportFile, gbc_btnImportFile);
		
		GridBagConstraints gbc_lblTextInfo = new GridBagConstraints();
		gbc_lblTextInfo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTextInfo.gridx = 6;
		gbc_lblTextInfo.gridy = 11;
		lblProcessInfo.setBackground(Color.BLACK);
		card1.add(lblProcessInfo, gbc_lblTextInfo);
		
		GridBagConstraints gbc_btnProcessText = new GridBagConstraints();
		gbc_btnProcessText.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnProcessText.insets = new Insets(0, 0, 5, 5);
		gbc_btnProcessText.gridx = 1;
		gbc_btnProcessText.gridy = 15;
		card1.add(btnProcessText, gbc_btnProcessText);

		GridBagConstraints gbc_btnViewLists = new GridBagConstraints();
		gbc_btnViewLists.insets = new Insets(0, 0, 5, 5);
		gbc_btnViewLists.gridx = 6;
		gbc_btnViewLists.gridy = 15;
		card1.add(btnViewLists, gbc_btnViewLists);
		
		GridBagConstraints gbc_lblFilePath = new GridBagConstraints();
		gbc_lblFilePath.anchor = GridBagConstraints.WEST;
		gbc_lblFilePath.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilePath.gridx = 4;
		gbc_lblFilePath.gridy = 9;
		card1.add(lblFilePath, gbc_lblFilePath);
		
		GridBagConstraints gbc_lblBody = new GridBagConstraints();
		gbc_lblBody.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblBody.insets = new Insets(0, 0, 5, 5);
		gbc_lblBody.gridx = 1;
		gbc_lblBody.gridy = 10;
		lblBody.setFont(new Font("Tahoma", Font.BOLD, 12));
		card1.add(lblBody, gbc_lblBody);
		
		GridBagConstraints gbc_btnProcessFile = new GridBagConstraints();
		gbc_btnProcessFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnProcessFile.gridx = 3;
		gbc_btnProcessFile.gridy = 10;
		card1.add(btnProcessFile, gbc_btnProcessFile);
		
		GridBagConstraints gbc_txtAreaBody = new GridBagConstraints();
		gbc_txtAreaBody.insets = new Insets(0, 0, 5, 5);
		gbc_txtAreaBody.fill = GridBagConstraints.BOTH;
		gbc_txtAreaBody.gridx = 1;
		gbc_txtAreaBody.gridy = 11;
		txtAreaBody.setWrapStyleWord(true);
		txtAreaBody.setLineWrap(true);
		card1.add(txtAreaBody, gbc_txtAreaBody);
		
		contentPane.add(card1, "1");  //Add card1 to contentPane CardLayout with index "1"
						
		//Process Text Button
		btnProcessText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				myController.importText(txtAreaHeader.getText() + txtAreaBody.getText());
			}
		});
		
		//Import File Button
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser jfc = new JFileChooser();
			    jfc.showDialog(null,"Please Select the File");
			    jfc.setVisible(true);
			    file = jfc.getSelectedFile();
			    lblFilePath.setText(file.getAbsolutePath());
			}
		});
		
		//Process File Button
		btnProcessFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				SwingWorker<Void, String> worker = new SwingWorker<Void, String>() // This is a new thread. By running importXML(file) in the new thread, it is possible to update the GUI and add a delay between processing each message from the file
						{

							@Override
							protected Void doInBackground() throws Exception 
							{
								myController.importXML(file); //Import the xml file and process it's messages
								
								return null;
							}
					
						};
						
						worker.execute(); //Run the worker thread
			}
		});
		
        //View Lists Button
        btnViewLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String content = ""; //Temporary variable used to format the lists before they are displayed
				
				//Iterate through mentions list, to format it and then display it via content variable. Clear the content variable for reuse.
				for (String temp : myController.getMentionsList())
				{
					content += temp + "\n";
				}
				txtAreaMentionsList.setText(content);
				content = "";
				
				//Iterate through trending HashMap, to format it and then display it via content variable. Clear the content variable for reuse.
				for (HashMap.Entry<String, Integer> entry : myController.getTrendingMap().entrySet()) {
				    
					content += entry.getKey() + " : " + entry.getValue() + "\n";

				}
				txtAreaTrendingList.setText(content);
				content = "";
				
				//Iterate through sirList, to format it and then display it via content variable.
				for (String temp : myController.getSirList())
				{
					content += temp + "\n";
				}
				txtAreaSIRList.setText(content);
				
	            cardLayout.show(contentPane, "2"); //Show card2
			}
		});
        
		card2.setBackground(Color.LIGHT_GRAY);
		        		        		        		

		//--------------------------------------CARD 2-------------------------------------------------------------------
		contentPane.add(card2, "2"); //Add card2 to contentPane CardLayout with index "2"
		
		
		//Set the GridBag layout for card1 and it's components
		GridBagLayout gbl_card2 = new GridBagLayout();
		gbl_card2.columnWidths = new int[]{100, 300, 0, 300, 0, 300, 0, 100};
		gbl_card2.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_card2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_card2.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		card2.setLayout(gbl_card2);
		
		GridBagConstraints gbc_lblMentionsList = new GridBagConstraints();
		gbc_lblMentionsList.anchor = GridBagConstraints.WEST;
		gbc_lblMentionsList.insets = new Insets(0, 0, 5, 5);
		gbc_lblMentionsList.gridx = 1;
		gbc_lblMentionsList.gridy = 2;
		lblMentionsList.setFont(new Font("Tahoma", Font.BOLD, 12));
		card2.add(lblMentionsList, gbc_lblMentionsList);
		
		GridBagConstraints gbc_lblTrendingList = new GridBagConstraints();
		gbc_lblTrendingList.anchor = GridBagConstraints.WEST;
		gbc_lblTrendingList.insets = new Insets(0, 0, 5, 5);
		gbc_lblTrendingList.gridx = 3;
		gbc_lblTrendingList.gridy = 2;
		lblTrendingList.setFont(new Font("Tahoma", Font.BOLD, 12));
		card2.add(lblTrendingList, gbc_lblTrendingList);
		
		GridBagConstraints gbc_lblSIRList = new GridBagConstraints();
		gbc_lblSIRList.anchor = GridBagConstraints.WEST;
		gbc_lblSIRList.insets = new Insets(0, 0, 5, 5);
		gbc_lblSIRList.gridx = 5;
		gbc_lblSIRList.gridy = 2;
		lblSIRList.setFont(new Font("Tahoma", Font.BOLD, 12));
		card2.add(lblSIRList, gbc_lblSIRList);
		
		GridBagConstraints gbc_scrollPaneMentionsList = new GridBagConstraints();
		gbc_scrollPaneMentionsList.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneMentionsList.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneMentionsList.gridx = 1;
		gbc_scrollPaneMentionsList.gridy = 3;
		txtAreaMentionsList.setEditable(false);
		scrollPaneMentionsList.add(txtAreaMentionsList);
		card2.add(scrollPaneMentionsList, gbc_scrollPaneMentionsList);
		
		GridBagConstraints gbc_scrollPaneTrendingList = new GridBagConstraints();
		gbc_scrollPaneTrendingList.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTrendingList.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneTrendingList.gridx = 3;
		gbc_scrollPaneTrendingList.gridy = 3;
		txtAreaTrendingList.setEditable(false);
		scrollPaneTrendingList.add(txtAreaTrendingList);
		card2.add(scrollPaneTrendingList, gbc_scrollPaneTrendingList);
		
		GridBagConstraints gbc_scrollPaneSIRList = new GridBagConstraints();
		gbc_scrollPaneSIRList.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneSIRList.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneSIRList.gridx = 5;
		gbc_scrollPaneSIRList.gridy = 3;
		txtAreaSIRList.setEditable(false);
		scrollPaneSIRList.add(txtAreaSIRList);
		card2.add(scrollPaneSIRList, gbc_scrollPaneSIRList);
		
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 5, 5);
		gbc_btnReturn.gridx = 1;
		gbc_btnReturn.gridy = 8;
		card2.add(btnReturn, gbc_btnReturn);
		
        //Import More Messages Button
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
	            cardLayout.show(contentPane, "1"); //Show card1
			}
		});
		cardLayout.show(contentPane, "1"); //The card that is displayed when the program launches.
	}

	//-------------------------------------GETTERS--------------------------------------------------------------------------
	public JTextArea getTxtAreaProcessedMessage() {
		return txtAreaProcessedMessage;
	}

	public JLabel getLblProcessInfo() {
		return lblProcessInfo;
	}
	
	//-------------------------------------SETTERS--------------------------------------------------------------------------
	public void setTxtAreaProcessedMessage(JTextArea txtAreaProcessedMessage) {
		this.txtAreaProcessedMessage = txtAreaProcessedMessage;
	}

	public void setLblProcessInfo(JLabel lblProcessInfo) {
		this.lblProcessInfo = lblProcessInfo;
	}
	
}
