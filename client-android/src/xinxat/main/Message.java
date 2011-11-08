package xinxat.main;
/*
 * Message.java
 */

/**
 * @author Procastinadors
 */
public class Message {

	//Variables de l'objete
	public String from;
	public String getFrom() {
		return from;
		
	}
	/**
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/** 
	 * @return
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}


	public String to;
	public String body;
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param body
	 */
	public Message(String from, String to, String body){
		this.from = from;
		this.to = to;
		this.body = body;
	}

	public String toString(){
		return "<"+from+"> "+ body;
	}
	
}
