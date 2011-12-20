package xinxat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xinxat.test.XmppTestMsgCreator;

@SuppressWarnings("serial")
public class ServerTest extends HttpServlet{
	
	private final String host = "http://localhost:8888";
	private final String username = "testuser";
	private final String token = "c2d930fe79cc71189b343dd28a9dd831";
	private final String testRoom = "testRoom";
	private final String privateTestRoom = "@testRoom";
	
	private final String usernameVictim = "franhp";
	private final String tokenVictim = "3d5a5c438605de06cd3ade2fef78b78c";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {		
		Date testStart = new Date();
		resp.getWriter().println("====Started at:======" + testStart + "==========");
		resp.getWriter().println("Updating: \t\t\t\t" + updateServer());
		resp.getWriter().println("==Private Chats==");
		resp.getWriter().println("Sending private chat: \t\t\t" + testSendPrivateChat());
		resp.getWriter().println("Receiving private chat: \t\t" + testReceivePrivateChat());
		resp.getWriter().println("Sending message with wrong password: \t" + testSendChatWithWrongPassword());
		resp.getWriter().println("Receiving message with wrong password: \t" + testReceiveChatWithWrongPassword());
		resp.getWriter().println("Sending message to non-existent user: \t" + testSendChatToNonExistentUser());
		resp.getWriter().println("==Group Chats==");
		resp.getWriter().println("Reseting and updating rooms: \t\t" + resetAndUpdateRooms());
		resp.getWriter().println("Sending message to room: \t\t" + testSendGroupchatToRoom());
		resp.getWriter().println("Sending message to non-existent room: \t" + testSendGroupchatToNonExistentRoom());
		resp.getWriter().println("Sending message to private room: \t" + testSendGroupchatToPrivateRoom());
		resp.getWriter().println("Sending message to a non-joined room: \t" + testSendGroupchatNonJoined());
		resp.getWriter().println("==System Chats==");
		resp.getWriter().println("Joining a private room\t\t\t" + testJoinPrivateRoom());
		resp.getWriter().println("Joining a nonexistent room\t\t" + testJoinNonExistentRoom());
		resp.getWriter().println("Leaving a room\t\t\t\t" + testLeaveRoom());
		resp.getWriter().println("Inviting someone to a room\t\t" + testInvite());
		resp.getWriter().println("Kicking someone of a room\t\t" + testKick());
		resp.getWriter().println("Banning someone of a room\t\t" + testBan());
		resp.getWriter().println("Trying to join after ban\t\t" + testJoinAfterBan());
		resp.getWriter().println("Unbanning someone of a room\t\t" + testUnbanAndJoin());
		resp.getWriter().println("==Roster==");
		resp.getWriter().println("Sanity check on roster\t\t\t" + testRoster());
		resp.getWriter().println("==Reset==");
		resp.getWriter().println("Last reset: \t\t\t\t" + resetAndUpdateRooms());
		resp.getWriter().println("Updating: \t\t\t\t" + updateServer());
		
	}
	


	private String updateServer() {
		xinxat.server.UpdateRooms.doUpdate();
		xinxat.server.UpdateDB.doUpdate();
		return "OK";
	}



	private String testInvite() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"someRandomRoom","/invite franhp","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					xinxat.server.Server.getUsersFromRoom("someRandomRoom").contains("franhp")) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testUnbanAndJoin() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"someRandomRoom","/unban " + usernameVictim,"system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)) {
				Map<String,String> data2 = new HashMap<String,String>();
				data2.put("token", tokenVictim);
				XmppTestMsgCreator msg2 = new XmppTestMsgCreator(usernameVictim,"someRandomRoom","/join someRandomRoom","system");			
				data2.put("msg", msg2.build());
				String result2 = postData(data2, new URL(host + "/messages"));
				if(!xinxat.server.Server.banlist("someRandomRoom").contains(usernameVictim)
						&&
						result2.contains("OK")) return "OK";
				else return "FAILED: " + result2;
			}
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testJoinAfterBan() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", tokenVictim);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(usernameVictim,"someRandomRoom","/join someRandomRoom","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("CANT".equals(result)) return "OK";
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testBan() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"someRandomRoom","/ban " + usernameVictim + " test","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					xinxat.server.Server.banlist("someRandomRoom").contains(usernameVictim)) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testKick() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"someRandomRoom","/kick " + usernameVictim + " test","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					!xinxat.server.Server.getUsersFromRoom("someRandomRoom").contains(usernameVictim)) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testLeaveRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"someRandomRoom","/leave","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					!xinxat.server.Server.getUsersFromRoom("someRandomRoom").contains(username)) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}



	private String testJoinNonExistentRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,username,"/join someRandomRoom","system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					xinxat.server.Server.getUsersFromRoom("someRandomRoom").contains(username)) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}
	}


	private String testJoinPrivateRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,username,"/join "+privateTestRoom,"system");			
			data.put("msg", msg.build());
			String result = postData(data, new URL(host + "/messages"));
			if("OK".equals(result)
					&&
					xinxat.server.Server.getUsersFromRoom(privateTestRoom).contains(username)) return result;
			else return "FAILED : " + result;
		} catch (MalformedURLException e) {
			return "FAILED : malformedURL";
		}

	}



	private String resetAndUpdateRooms() {
		xinxat.server.Server.resetBans();
		xinxat.server.UpdateRooms.doUpdate();
		if(xinxat.server.Server.listRooms().contains(privateTestRoom)) return "OK";
		else return "FAILED";
	}



	private String testSendGroupchatNonJoined() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"general","TestMessage","groupchat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if("CANT".equals(result)) return "OK";
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}




	private String testSendGroupchatToNonExistentRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,privateTestRoom + "NO","TestMessage","groupchat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if("CANT".equals(result)) return "OK";
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}

	
	private String testSendGroupchatToPrivateRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,privateTestRoom,"TestMessage","groupchat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if(result.contains("OK")) return "OK";
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}


	private String testSendGroupchatToRoom() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator firstMsg = new XmppTestMsgCreator(username,username,"/join "+testRoom,"system");			
			data.put("msg", firstMsg.build());
		    String firstMessage = postData(data, new URL(host + "/messages"));
			if("OK".equals(firstMessage)){
				Map<String,String> data2 = new HashMap<String,String>();
				data2.put("token", token);
				XmppTestMsgCreator msg = new XmppTestMsgCreator(username,testRoom,"TestMessage","groupchat");			
				data2.put("msg", msg.build());
			    String result = postData(data2, new URL(host + "/messages"));
			    
			    if(result.contains("OK")) return result;
			    else return "FAILED: test returned " + result + " because " + msg.build() + " was sent";
		    }
		    else {
		    	return "FAILED: could not even join";
		    }
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}


	private String testSendChatToNonExistentUser() {
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,"NonExistentUser","TestMessage","chat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if("NOEXISTS".equals(result)) return "OK";
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}


	private String testSendPrivateChat(){
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", token);
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,username,"TestMessage","chat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if("OK".equals(result)) return result;
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
	}
	private String testReceivePrivateChat(){
		String response;
		try {
			response = getData(new URL(host + "/messages?to=" + username + "&token=" + token));
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,username,"TestMessage","chat");
			if(response.contains(msg.build())){
				return "OK";
			}
			else return "FAILED the message was: " + response; 
		} catch (MalformedURLException e) {
			return "FAILED: MalformedURL";
		}
		
	}
	
	private String testSendChatWithWrongPassword(){
		try {
			Map<String,String> data = new HashMap<String,String>();
			data.put("token", "12345");
			
			XmppTestMsgCreator msg = new XmppTestMsgCreator(username,username,"TestMessage","chat");			
			data.put("msg", msg.build());

		    String result = postData(data, new URL(host + "/messages"));
		    
		    if("WRONG".equals(result)) return "OK";
		    else return "FAILED: test returned " + result;
		} catch (Exception e) {
			return "FAILED: exception";
		}
		
	}
	
	private String testReceiveChatWithWrongPassword(){
		String response;
		try {
			response = getData(new URL(host + "/messages?to=" + username + "&token=" + "12345"));
			if("WRONG".equals(response)) return "OK";
			else return "FAILED the message was: " + response; 
		} catch (MalformedURLException e) {
			return "FAILED: MalformedURL";
		}
	}
	
	
	private String testRoster() {
		String response;
		try {
			response = getData(new URL(host + "/roster"));
		if(response.contains("Reload")){
			return "FAILED because a null value was found";
		}
		else return "OK";
		} catch (MalformedURLException e) {
			return "FAILED urlmalformed";
		}
	}

	private String getData(URL url){
		try {
			String cadena = "";
		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String str;
		    while ((str = in.readLine()) != null) {
		       cadena += str;
		    }
		    in.close();
		    return cadena;
		} catch (MalformedURLException e) {
			return "FAILED malformedurl " + url.toString();
		} catch (IOException e) {
			return "FAILED ioexception";
		}
		
	}
	@SuppressWarnings("rawtypes")
	private String postData(Map<String, String> dataMap, URL url){
	    
		try{
			String data = "";
		    Iterator it = dataMap.entrySet().iterator();
		    while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				data += pairs.getKey().toString() + "=" + pairs.getValue().toString() + "&";
		        it.remove();
		    }

		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();
	
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String cadena = "";
		    while ((line = rd.readLine()) != null) {
		        cadena += line;
		    }
		    wr.close();
		    rd.close();
		    
		    return cadena;
		}
		catch (Exception e){
			return "EXCEPTION while Posting";
		}
	}
}