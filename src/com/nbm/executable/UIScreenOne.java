package com.nbm.executable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class UIScreenOne extends JFrame {

	/**LoginController variable that performs all the logic for this screen.*/
	private UIController myController;

	private JLabel lblHeader;
	private JLabel lblBody;
	private JTextField txtHeaderContent;
	private JTextArea txtrBodyContent;
	private JButton btnProcessMessage;
	private JButton btnViewLists;
	
	public UIScreenOne(UIController controller) 
	{
	
		
		myController = controller; //Assign myController as an instance of UI_Controller
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //The application will quit when the JFrame is closed.
		setBounds(0, 0, 1280, 720);//(0, 0, 1920, 1080); //JFrame is created at coordinates 0, 0 of screen with 1920x1080 pixel dimensions.
		JPanel contentPane = new JPanel(); //Assign contentPane to be a new instance of JPanel. This is the main panel of the JFrame.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //Create a 5 pixel border around all sides of the contentPane.
		setContentPane(contentPane); //Insert contentPane into the JFrame.
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		//Process Message Button
		btnProcessMessage = new JButton("Process Message");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnProcessMessage, 889, SpringLayout.WEST, contentPane);
		contentPane.add(btnProcessMessage);
		btnProcessMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Processing...");
				myController.processMessage(txtHeaderContent.getText(), txtrBodyContent.getText());
			}
		});

		
		//Body Content Text Field
		txtHeaderContent = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtHeaderContent, 255, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtHeaderContent, 576, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtHeaderContent, -576, SpringLayout.EAST, contentPane);
		txtHeaderContent.setText("E123456789");
		contentPane.add(txtHeaderContent);
		txtHeaderContent.setColumns(10);
		
		//Body Content Text Area
		txtrBodyContent = new JTextArea();
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnProcessMessage, 57, SpringLayout.SOUTH, txtrBodyContent);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtrBodyContent, 368, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtrBodyContent, -369, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtrBodyContent, 576, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtrBodyContent, -576, SpringLayout.EAST, contentPane);
		txtrBodyContent.setText("I like test.test"); 
		contentPane.add(txtrBodyContent);
		
		//I like youtube.com, and https://github.com/linkedin/URL-Detector.
		
		/*Saw your message @JohnDoe ROTFL, can’t wait to see you.
		Looking forward to seeing you too @JaneDoe 
		#Party #Fireworks*/
		
		
		
		//Message Header Label
		lblHeader = new JLabel("Message Header:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHeader, 898, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblHeader, -6, SpringLayout.NORTH, txtHeaderContent);
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
		contentPane.add(lblHeader);
		
		//Message Body Label
		lblBody = new JLabel("Message Body:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblBody, 905, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblBody, -11, SpringLayout.NORTH, txtrBodyContent);
		lblBody.setFont(new Font("Tahoma", Font.BOLD, 11));
		contentPane.add(lblBody);
		
		btnViewLists = new JButton("View Lists");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnViewLists, 47, SpringLayout.SOUTH, btnProcessMessage);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnViewLists, 5, SpringLayout.WEST, btnProcessMessage);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnViewLists, 0, SpringLayout.EAST, btnProcessMessage);
		contentPane.add(btnViewLists);
		btnViewLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("Button Pressed");
				//myController.showScreenTwo();
				
				ProcessedMessage pm = new ProcessedMessage();
			}
		});
		

	}
}
