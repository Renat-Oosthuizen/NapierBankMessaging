package com.nbm.executable;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

public class UI_ScreenTwo extends JFrame {

	/**LoginController variable that performs all the logic for this screen.*/
	private UIController myController;
	private JTextArea txtrListContent;

	//Constructor
	public UI_ScreenTwo(UIController controller) 
	{
		myController = controller; //Assign myController as an instance of UI_Controller
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //The application will quit when the JFrame is closed.
		setBounds(0, 0, 1920, 1080); //JFrame is created at coordinates 0, 0 of screen with 1920x1080 pixel dimensions.
		JPanel contentPane = new JPanel(); //Assign contentPane to be a new instance of JPanel. This is the main panel of the JFrame.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //Create a 5 pixel border around all sides of the contentPane.
		setContentPane(contentPane); //Insert contentPane into the JFrame.
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		txtrListContent = new JTextArea();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtrListContent, 290, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtrListContent, 77, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtrListContent, 741, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtrListContent, 329, SpringLayout.EAST, contentPane);
		contentPane.add(txtrListContent);
	}
	
	
	//-----------------------------------------GETTERS/SETTERS--------------------------------------------------------------------------------
	public JTextArea getTxtrListContent() {
		return txtrListContent;
	}

	public void setTxtrListContent(JTextArea txtrListContent) {
		this.txtrListContent = txtrListContent;
	}
}
