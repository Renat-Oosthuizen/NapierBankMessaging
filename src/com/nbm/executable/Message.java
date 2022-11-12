package com.nbm.executable;


public class Message {
	
	private String sender = "";
	private String subject = "";
	private String sortCode = "";
	private String natureOfIncident = "";
	private String text = "";
	
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
