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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinxat);

        Bundle bundle = this.getIntent().getExtras();
        
        //Obtenció de les varaibles de l'activitat anterior: Roster.java
        final String username = "" + bundle.getString("USERNAME1");
        final String token = "" + bundle.getString("TOKEN1");
        final String target = "" + bundle.getString("TARGET1");
        final String type = "" + bundle.getString("TYPE1");

        	//Declarem els dos fils
	        final Handler handler = new Handler();
	        final Handler handler1 = new Handler();
	        
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
									for(int i=0;i<messages.size();i++)
									{
										String body = null;
										String from = null;
										
										from = messages.get(i).getFrom();
										body = messages.get(i).getBody();
										
										if (body != null){
											if (!from.equals(username)){
											acumlador += from+": "+messages.get(i).toString()+"<br>";
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
					MessageBox("Error raro");
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
										for(int i=0;i<messages.size();i++)
										{
											String body = null;
											String from = null;
											
											from = messages.get(i).getFrom();
											body = messages.get(i).getBody();
											
											if (body != null){
												if (!from.equals(username)){
												acumlador += from+": "+messages.get(i).toString()+"<br>";
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
						MessageBox("Error raro");
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
							//Comproavació si introdueix comanda de sistema 
							if (text.startsWith("/")){
								String type="system";
								String sys = sendPage(target, username, type, text, token);
								acumlador += username+": "+text+"<br>";
								acumlador += username+": "+sys+"<br>";
								txtMonitor.setText(Html.fromHtml(acumlador));
								msg.setText("");}
							else{
							sendPage(target, username, type, text, token);
							acumlador += username+": "+text+"<br>";
							txtMonitor.setText(Html.fromHtml(acumlador));
							msg.setText("");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
        
    }
    
    /**
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
	 * @param message
	 */
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
}
