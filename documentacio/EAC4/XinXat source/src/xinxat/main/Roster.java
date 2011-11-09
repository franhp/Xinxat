package xinxat.main;
/*
 * Roster.java
 */

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * @author Procastinadors
 */

public class Roster extends Activity {
	
	public XMLParser parser;
	private ArrayList<Presence> presences = new ArrayList<Presence>();
	private ArrayList<Room> room = new ArrayList<Room>();
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roster);
        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
        
        //Asignamos la info del login activity a las variables
        final String username = "" + bundle.getString("USERNAME");
        final String token = "" + bundle.getString("TOKEN");
        
        String XMLRoster = null;
        String XMLRooms = null;
        
        //Crida de la funció getRoster
        try {
        	XMLRoster = getRoster();
        } catch(Exception e){
        	MessageBox("Error consiguiendo el roster");
        }
        
        //Crida de la funció getRooms
        try {
        	XMLRooms = getRooms();
        } catch(Exception e){
        	MessageBox("Error consiguiendo las salas");
        }

        //Per cada room anirem ccreant un boto amb la funció onclick
       	if (XMLRooms.startsWith("<?xml")){ 
			parser = new XMLParser(XMLRooms, "room");    
			room = (ArrayList<Room>) parser.parseXmlString();
			
			//Comprovem que hi hagui una sala com a minim
	        if (!room.isEmpty()){
					for(int i=0;i<room.size();i++)
					{
						final String target = room.get(i).getName();
						//Creació del boto
						Button myButton = new Button(this);
						myButton.setText("Sala:   " + target);
						myButton.setId(i);
						myButton.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
			    				
			    				Intent intent1 = new Intent(Roster.this, XinXatMain.class);
			    				String type = "groupchat";
			    				
			    			    Bundle a = new Bundle(); 
			    			    Bundle b = new Bundle(); 
			    			    Bundle c = new Bundle(); 
			    			    Bundle d = new Bundle();
			    			    
			    			    a.putString("TOKEN1", token);
			    			    b.putString("USERNAME1", username);
			    			    c.putString("TARGET1", target);
			    			    d.putString("TYPE1", type);
			    			    
			    			    intent1.putExtras(a);
			    			    intent1.putExtras(b);
			    			    intent1.putExtras(c);
			    			    intent1.putExtras(d);
			    			    
			    			    startActivity(intent1);
							}
							
						});	
						//Inserció del boto al layout
						LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
						layout.addView(myButton);
					}
	        	}
	        	//Per cada usuari anirem ccreant un boto amb la funció onclick
				if (XMLRoster.startsWith("<presences") && !XMLRoster.equals("") && !XMLRoster.equals("NULL") &&  XMLRoster != null){
					parser = new XMLParser(XMLRoster, "roster");
					presences = (ArrayList<Presence>) parser.parseXmlString();		
					
					//Comprovem que hi hagui usuaris
					if (!presences.isEmpty()){
						for(int i=0;i<presences.size();i++)
						{
							
							String from = null;
							String show = null;
							
							//Obtenim el nom del usuari i el seu show
							from = presences.get(i).getFrom();
							show = presences.get(i).getShow();
							
							final String target = presences.get(i).getFrom();
							//No mostrem el nostre propi nom
							if(!username.equals(from)){
								//Creacuó del boto amb el nom d'usuari 
								Button myButton = new Button(this);
								//Diferenciació d'usuaris online o offline mitjançant el textcolor
								if(show.equals("online")) myButton.setTextColor(Color.GREEN);
								else myButton.setTextColor(Color.GRAY);
								myButton.setText("Usuari:   " + from);
								myButton.setId(i);
								myButton.setOnClickListener(new OnClickListener() {
				            	 
									public void onClick(View v) {
				    				
					    				Intent intent1 = new Intent(Roster.this, XinXatMain.class);
					    				String type = "chat";
					    				
					    			    Bundle a = new Bundle(); 
					    			    Bundle b = new Bundle(); 
					    			    Bundle c = new Bundle(); 
					    			    Bundle d = new Bundle();
					    			    
					    			    //Conjunt de variables que pasarem a la següent activitat
					    			    a.putString("TOKEN1", token);
					    			    b.putString("USERNAME1", username);
					    			    c.putString("TARGET1", target);
					    			    d.putString("TYPE1", type);
					    			    
					    			    intent1.putExtras(a);
					    			    intent1.putExtras(b);
					    			    intent1.putExtras(c);
					    			    intent1.putExtras(d);
	
					    			    startActivity(intent1);
									}
				    	        
								});
								//Inserció del boto al layout
								LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
								layout.addView(myButton);
							}}}}
					else {
						MessageBox("No hay message");
					}
		}

    };
    
    /**
     * @return
     */
    public String getRoster(){
    	HttpClient client = new DefaultHttpClient();  
    	//Realitzem un get del roster
		String getURL = "http://projecte-xinxat.appspot.com/roster";
		HttpGet get = new HttpGet(getURL);
		HttpResponse responseGet = null;
		String str = "NULL";
		try {
			responseGet = client.execute(get);
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}  
		
		HttpEntity resEntityGet = responseGet.getEntity();  
		if (resEntityGet != null) {  
			try {
				str = EntityUtils.toString(resEntityGet);
			} catch (ParseException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		return str;
    }
    
    /**
     * @return
     */
    public String getRooms(){
        //Recuperem la informació passada en l'intent
        Bundle bundle = this.getIntent().getExtras();

        //Assignem la info del login activity a las variables
        final String username = "" + bundle.getString("USERNAME");
        
    	HttpClient client = new DefaultHttpClient();  
    	//Realitzem un get dels usuaris de la Sala
		String getURL = "http://api.xinxat.com/?userRoomList="+username;
		HttpGet get = new HttpGet(getURL);
		HttpResponse responseGet = null;
		String str = "NULL";
		try {
			responseGet = client.execute(get);
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}  
		
		HttpEntity resEntityGet = responseGet.getEntity();  
		if (resEntityGet != null) {  
			try {
				str = EntityUtils.toString(resEntityGet);
			} catch (ParseException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		return str;
    } 

    /**
     * @param message
     */
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
    
}
        

