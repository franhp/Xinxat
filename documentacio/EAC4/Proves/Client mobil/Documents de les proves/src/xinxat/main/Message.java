/**
 * Message.java
 *
 * Created on 28-oct-2011, 15:42:36
 * @author Hector Costa Guzman
 */

package xinxat.main;

public class Message {
	
	//variables del objeto
	public String from;
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String to;
	public String body;
	
	//constructor
	public Message(String from, String to, String body){
		this.from = from;
		this.to = to;
		this.body = body;
	}
	
	public String toString(){
		return "<"+from+"> "+ body;
	}
	
}
