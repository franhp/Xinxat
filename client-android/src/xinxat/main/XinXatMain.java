package xinxat.main;
/*
 * XinXatMain.java
 */
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Procastinadors
 */
public class XinXatMain extends Activity {
	//Declaració de varaibles
	public String acumlador = "";
	public XMLParser parser;
	private ArrayList<Message> messages = new ArrayList<Message>();
	private static final int NOTIF_ALERTA_ID = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinxat);

        //Obtenció de les varaibles de l'activitat anterior: Roster.java
        Bundle bundle = this.getIntent().getExtras();
        final String username = "" + bundle.getString("USERNAME1");
        final String token = "" + bundle.getString("TOKEN1");
        final String target = "" + bundle.getString("TARGET1");
        final String type = "" + bundle.getString("TYPE1");
        setTitle(target);
        
        	//Declarem els dos fils
	        final Handler handler = new Handler();
	        final Handler handler1 = new Handler();
	        final DBAdapter db = new DBAdapter(this);
	        //Excecució del primer fil cada 80 sec
	        Runnable runnable1 = new Runnable() {
	        	@SuppressWarnings("unchecked")
				public void run() {
	        		TextView txtMonitor = (TextView) findViewById(R.id.TxtMonitor);
	        		handler.postDelayed(this, 80000);
	        		try {
	        			//Crida a la funció getOnline
						String str = getOnline(username, token);

						if (!str.startsWith("<presence") && !str.equals("") && !str.equals("NULL") &&  str != null){
							parser = new XMLParser(str, "messages");
							messages = (ArrayList<Message>) parser.parseXmlString();			
							
							if (!messages.isEmpty()){
								//Creació de la Notificació
								String ns = Context.NOTIFICATION_SERVICE;
								NotificationManager notManager = (NotificationManager) getSystemService(ns);
								
								//Configurem la notifiació
								int icono = android.R.drawable.stat_sys_warning;
								CharSequence textoEstado = "Nou Missatge!";
								
								//Obtenim la hora del sistema
								long hora = System.currentTimeMillis();
								Notification notif = new Notification(icono, textoEstado, hora);
								
								//Configurem el intent per tornar a accedir a l'aplicació si aquest roman tancada en "background"
								Context contexto = getApplicationContext();
								CharSequence titulo = "Nou Missatge!";
								CharSequence descripcion = "Nou missatge de XinXat!";
								Intent notIntent = new Intent(contexto, Login.class);
								PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, notIntent, 0);
								notif.setLatestEventInfo(contexto, titulo, descripcion, contIntent);
								
								//AutoCancel: Quan és prem borrar la notificació desapareix
								notif.flags |= Notification.FLAG_AUTO_CANCEL;
								
								//Afegim opcions a la notificació, ambé es podria fer vibrar, etc . . . 
								notif.defaults |= Notification.DEFAULT_SOUND;
								
								//Enviem la notificació
								notManager.notify(NOTIF_ALERTA_ID, notif);							
								

									for(int i=0;i<messages.size();i++)
									{
										String body = null;
										String from = null;
										String to = null;
										
										//Obtenim els missatges
										from = messages.get(i).getFrom();
										body = messages.get(i).getBody();
										to = messages.get(i).getTo();
										
										//Guardem els missatges al historial
								        db.open();        
								        long id;
								        id = db.insertTitle(
								        		from,
								        		to,
								        		body);   
								        db.close();
										
								        //Mostrarem els missatges en format HTML per modificar el estil
										if (body != null){
											if (!from.equals(username)){
												acumlador += "<b><font color=\"#375b0b\">("+from+")</b>"+to+":<i>"+messages.get(i).toString()+"</i></font><br>";
												txtMonitor.setText(Html.fromHtml(acumlador ));
											}
										}
									} 
								}
							else {
									//Si no hi ha missatges
									MessageBox("No hay message");
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				messages.clear();
				
			}
		};

	        handler1.postDelayed(runnable1, 80000);
	        
	        //Excecució del segon fil cada 2 sec
			Runnable runnable = new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					TextView txtMonitor = (TextView) findViewById(R.id.TxtMonitor);
					handler.postDelayed(this, 2000);
					try {
							String str = getPage(username, token);

							if (!str.startsWith("<presence") && !str.equals("") && !str.equals("NULL") &&  str != null){
								parser = new XMLParser(str, "messages");
								messages = (ArrayList<Message>) parser.parseXmlString();			
								
								if (!messages.isEmpty()){
									//Creació de la Notificació
									String ns = Context.NOTIFICATION_SERVICE;
									NotificationManager notManager = (NotificationManager) getSystemService(ns);
									
									//Configurem la notifiació
									int icono = android.R.drawable.stat_sys_warning;
									CharSequence textoEstado = "Nou Missatge!";
									
									//Obtenim la hora del sistema
									long hora = System.currentTimeMillis();
									Notification notif = new Notification(icono, textoEstado, hora);
									
									//Configurem el intent per tornar a accedir a l'aplicació si aquest roman tancada en "background"
									Context contexto = getApplicationContext();
									CharSequence titulo = "Nou Missatge!";
									CharSequence descripcion = "Nou missatge de XinXat!";
									
									Intent notIntent = new Intent(contexto, Login.class);
									
									PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, notIntent, 0);

									notif.setLatestEventInfo(contexto, titulo, descripcion, contIntent);
									
									//AutoCancel: Quan és prem borrar la notificació desapareix
									notif.flags |= Notification.FLAG_AUTO_CANCEL;
									
									//Afegim opcions a la notificació, ambé es podria fer vibrar, etc . . . 
									notif.defaults |= Notification.DEFAULT_SOUND;
									
									//Enviaem la notificació
									notManager.notify(NOTIF_ALERTA_ID, notif);				

									
										for(int i=0;i<messages.size();i++)
										{
											String body = null;
											String from = null;
											String to = null;
											
											//Obtenim els missatges
											from = messages.get(i).getFrom();
											body = messages.get(i).getBody();
											to = messages.get(i).getTo();
											
											//Guardem els missatges al historial
									        db.open();        
									        long id;
									        id = db.insertTitle(
									        		from,
									        		to,
									        		body);   
									        db.close();
									        
									      //Mostrarem els missatges en format HTML per modificar el estil
											if (body != null){
												if (!from.equals(username)){
													acumlador += "<b><font color=\"#375b0b\">("+from+")</b>"+"<font color=\"#375b0b\">"+to+":"+"<i><font color=\"#375b0b\">"+messages.get(i).toString()+"</i></font><br>";
													txtMonitor.setText(Html.fromHtml(acumlador ));
												}
											}
										} 
									}
								else {
										MessageBox("No hay message");
								}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					messages.clear();
					
				}
			};
	
			handler.postDelayed(runnable, 2000);
			
			
			//Envia el text del EditText al usuari que haviem elegit anteriorment
			final Button button = (Button) findViewById(R.id.sendButton);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					TextView txtMonitor = (TextView) findViewById(R.id.TxtMonitor);
					try {
						EditText msg = (EditText) findViewById(R.id.mSendText);
						String text = msg.getText().toString();
						//No podem enviar camps sense res
						if (!text.equals("")){
							
							if(text.equals("/clean")){
							TextView textView1 = (TextView) findViewById(R.id.textView1);
							textView1.setText("");
							db.open();
							db.deleteDB();
							db.close();
							}
						
							
							//Comproavació si introdueix comanda de sistema 
							else if (text.startsWith("/")){
								String type="system";
								String sys = sendPage(target, username, type, text, token);
								acumlador += username+": "+text+"<br>";
								acumlador += username+": "+sys+"<br>";
								txtMonitor.setText(Html.fromHtml(acumlador));
								msg.setText("");}
							else{
							//Cridem a la funció sendPage que enviarà el nsotre missatge al servidor
							sendPage(target, username, type, text, token);
							//Modifiquem el nostre missatge amb HTML
							acumlador += "<b><font color=\"#375b0b\">("+username+")</font></b><font color=\"#375b0b\">:"+text+"<br></font>";
							txtMonitor.setText(Html.fromHtml(acumlador));
							
							  //Guardem el missatge a la Base de Dades
							  db.open();        
						        long id;
						        id = db.insertTitle(
						        		target,
						        		username,
						        		text);   
						        db.close();
							msg.setText("");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//Obrim la Base de Dades
			 db.open(); 
			 //Obtenim el Historial
			 Cursor c = db.getAllTitles();
		        if (c.moveToFirst())
		        {
		            do {          
		            	//Mostrem el Historial de la Base de Dades
		                DisplayTitle(c);
		            } while (c.moveToNext());
		        }
		    //Tanquem la Base de Dades
	        db.close();
        
    }
    
	/**
	 * Funció amb la qual mostrem el historial de conversació
	 * 
     * @param Cursor c
     */
    public void DisplayTitle(Cursor c)
    {
    	String historial = "";
    	
    	
    	if(c.moveToFirst() ){
    		do{
    			Bundle bundle1 = this.getIntent().getExtras();
    			final String target = "" + bundle1.getString("TARGET1");
    			final String username = "" + bundle1.getString("USERNAME1");
    			TextView txthistorial = (TextView) findViewById(R.id.textView1);
    			if (!c.getString(1).equals(username))historial += "<b>("+c.getString(1)+")</b>"+ c.getString(2)+": "+c.getString(3)+"<br>";
    	    	txthistorial.setText(Html.fromHtml(historial));
    		}while (c.moveToNext());
		}  
    } 
	/**
	 * Funció que ens canvia el estat Online i que obte els missatges que té el servidor
	 * 
     * @param target
     * @param token
     * @return
     * @throws Exception
     */
    private String getOnline(String target, String token) throws Exception {
    	String str="NULL";
    	
		try {
			HttpClient client = new DefaultHttpClient();  
			//Obtenim els nostres missatges i enviem petició d'online
			String getURL = "http://projecte-xinxat.appspot.com/messages?to="+target+"&token="+token+"&show=online&status=chating";
			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);  
			HttpEntity resEntityGet = responseGet.getEntity();  
			if (resEntityGet != null) {  
				str = EntityUtils.toString(resEntityGet); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
    }
    
    /**
     * Funció que ens torna els missatges que té el servidor 
     * 
     * @param target
     * @param token
     * @return
     * @throws Exception
     */
    private String getPage(String target, String token) throws Exception {
		String str="NULL";

		try {
			HttpClient client = new DefaultHttpClient();  
			//Obtenim els missatges que ens envien
			String getURL = "http://projecte-xinxat.appspot.com/messages?to="+target+"&token="+token;
			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);  
			HttpEntity resEntityGet = responseGet.getEntity();  
			if (resEntityGet != null) {  
				str = EntityUtils.toString(resEntityGet); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
    
    /**
     * Funció que envia el nsotre text al servidor
     * 
     * @param target
     * @param user
     * @param type
     * @param text
     * @param token
     * @return
     * @throws Exception
     */
	private String sendPage(String target, String user, String type, String text, String token) throws Exception {
		String resposta = null;
				
		try {
			HttpClient client = new DefaultHttpClient();  
			String postURL = "http://projecte-xinxat.appspot.com/messages";
			//Realitzem un post amb dues claus, msg i el token passant totes les variables
			HttpPost post = new HttpPost(postURL); 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("msg", "<message to=\""+target+"\" from=\""+user+"\" type=\""+type+"\"><body>"+text+"</body></message>"));
			params.add(new BasicNameValuePair("token", token));
			
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			post.setEntity(ent);
			
			HttpResponse responsePOST = client.execute(post);  
			HttpEntity resEntity = responsePOST.getEntity();  
			resposta = EntityUtils.toString(resEntity);
			
			if (resEntity != null) {    
				Log.i("RESPONSE",EntityUtils.toString(resEntity));
			}
			
			else Log.i("RESPONSE","No responde");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resposta;

	}
    
    /**
     * Funció que serveix per mostrar un message box en la layout quan quelcom va malament
     * 
     * @param String message
     */
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
}
