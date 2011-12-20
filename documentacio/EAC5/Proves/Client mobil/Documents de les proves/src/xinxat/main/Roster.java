package xinxat.main;

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
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Roster extends Activity {
	//Declaració de variables
	public XMLParser parser;
	private ArrayList<Presence> presences = new ArrayList<Presence>();
	private ArrayList<Room> room = new ArrayList<Room>();
	

	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roster);
        
      //Declaració de contenidors TextView i Buto
        TextView roster1 = (TextView)findViewById(R.id.textView3);
        TextView roster2 = (TextView)findViewById(R.id.textView5);
        Button btnXinXat = (Button)findViewById(R.id.button1);
      //Declaració de variables
        String XMLRoster = null;
        String XMLRooms = null;
        parser = new XMLParser(XMLRooms, "room");    
        String users = "";
        String rooms = "";
        
        //Obtenció del Roster
        try {
        	XMLRoster = getRoster();
        } catch(Exception e){
        	MessageBox("Error consiguiendo el roster");
        }
        
        //Obtenció de les Sales
        try {
        	XMLRooms = getRooms();
        } catch(Exception e){
        	MessageBox("Error consiguiendo el roster");
        }
        
        //Mostra dels usuaris presents en el roster
		if (XMLRoster.startsWith("<presences") && !XMLRoster.equals("") && !XMLRoster.equals("NULL") &&  XMLRoster != null){
				parser = new XMLParser(XMLRoster, "roster");
				presences = (ArrayList<Presence>) parser.parseXmlString();
				//Comrpovació per si no hi ha cap usuari
				if (!presences.isEmpty()){
						//Recorrem els usuaris
						for(int i=0;i<presences.size();i++)
						{
							String from = null;
							String show = null;
							
							from = presences.get(i).getFrom();
							show = presences.get(i).getShow();
							//Inserim a la varaible users ,el nom dels usuaris i el seu stat
							users+= from + show +"<br>"; 
						} 
						//Introduim la String users al TextView
						roster1.setText(Html.fromHtml(users));
					}
				else {
						//Error si no hi ha ahgut exit obtenint el rooster
						MessageBox("No hay message");
				}
		}
		
		//Mostra de les sales del usuari igango
       	if (XMLRooms.startsWith("<?xml")){ 
			parser = new XMLParser(XMLRooms, "room");    
			room = (ArrayList<Room>) parser.parseXmlString();
			//Mirem si hi ha sales disponibles per el usuari igango
			if (!room.isEmpty()){
				//Recorem les sales
				for(int i=0;i<room.size();i++)
				{
					String name = room.get(i).getName();
					//Inserim a la varaible rooms, el nom de les sales diponibles
					rooms+= name +"<br>"; 
				} 
				//Introduim la String rooms al TextView
				roster2.setText(Html.fromHtml(rooms));
			}
		else {
				//Erro si no hi ha algut exit obtenint les sales
				MessageBox("No hay salas");
		}
       	}
		//Saltem a la comprovació del Xat
	      btnXinXat.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            Intent intent = new Intent(Roster.this, XinXatMain.class);
	            startActivity(intent);
	            }
	      });
		
    }
    
    public String getRoster(){
    	HttpClient client = new DefaultHttpClient();  
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
    
    public String getRooms(){
        
    	HttpClient client = new DefaultHttpClient();  
		String getURL = "http://api.xinxat.com/?userRoomList=igango";
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
    
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
    
}
        

