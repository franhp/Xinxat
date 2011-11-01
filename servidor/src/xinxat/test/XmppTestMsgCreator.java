package xinxat.test;

public class XmppTestMsgCreator{
	
	private String message = "";
	
	public XmppTestMsgCreator(String from, String to, String message, String type){
		this.message = "<message to=\"" + to + "\" from=\"" + from + "\" type=\"" + type + "\"><body>"+message+"</body></message>";
	}
	
	public String build(){
		return message;
	}
}