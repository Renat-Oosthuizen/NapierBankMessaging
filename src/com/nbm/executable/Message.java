package com.nbm.executable;

/**
 * This class stores the contents of a message during processing, before it is written to a JSON file.
 * @author Renat Oosthuizen
 * @since 20/11/2022
 * */
public class Message {
	
	private String sender = ""; //Sender of the message
	private String subject = ""; //Subject of the message
	private String sortCode = ""; //Sort Code of the message
	private String natureOfIncident = ""; //Nature of Incident described in the message
	private String text = ""; //Text of the message
	
	//--------------------------------GETTERS------------------------------------

	public String getSender() {
		return sender;
	}
	public String getSubject() {
		return subject;
	}
	public String getSortCode() {
		return sortCode;
	}
	public String getNatureOfIncident() {
		return natureOfIncident;
	}
	public String getText() {
		return text;
	}
	
	//--------------------------------SETTERS------------------------------------

	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	public void setNatureOfIncident(String natureOfIncident) {
		this.natureOfIncident = natureOfIncident;
	}
	public void setText(String text) {
		this.text = text;
	}
	

}
