package xinxat.main;

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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class XinXatMain extends Activity {
	
	public String acumlador = "";
	public XMLParser parser;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinxat);
      //Declaració de contenidors TextView
        TextView xat1 = (TextView)findViewById(R.id.textView3);
        TextView xat2 = (TextView)findViewById(R.id.textView5);
        TextView xat3 = (TextView)findViewById(R.id.textView7);
        String comandes = "";
        //Canvi status a online el token le acosneguit de http://api.xinxat.com/?users
        try {
			xat1.setText(getOnline());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Enviar missatge de igango a hektor
        try {
			xat2.setText(sendPage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Enviar comandes system
        try {
        	comandes += ("Join @sala prova:" + sendComand("@general","/join @saladeprova1"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	comandes += ("Invite @sala prova:" + sendComand("@saladeprova1","/invite igango"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
        	comandes += ("List general:" + sendComand("@general","/list @general"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	comandes += ("Kick igango:" + sendComand("@general","/kick igango spam"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	comandes += ("Ban igango for:" + sendComand("@general","/ban igango spam"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	comandes += ("UnBan igango:" + sendComand("@general","/unban igango OK"));
			xat3.setText(comandes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private String getOnline() throws Exception {
    	String str="NULL";
    	
		try {
			HttpClient client = new DefaultHttpClient();  
			String getURL = "http://projecte-xinxat.appspot.com/messages?to=igango&token=55c3c44aae1ab8c81e099793970c54f3&show=online&status=chating";
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
    
	private String sendPage() throws Exception {
		String resposta = null;
		try {
			HttpClient client = new DefaultHttpClient();  
			String postURL = "http://projecte-xinxat.appspot.com/messages";
			
			HttpPost post = new HttpPost(postURL); 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("msg", "<message to=\"hektor\" from=\"igango\" type=\"chat\"><body>Hola, Hektor</body></message>"));
			params.add(new BasicNameValuePair("token", "55c3c44aae1ab8c81e099793970c54f3"));
			
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
	
	private String sendComand(String sala, String comand) throws Exception {
		String resposta = null;
		try {
			HttpClient client = new DefaultHttpClient();  
			String postURL = "http://projecte-xinxat.appspot.com/messages";
			
			HttpPost post = new HttpPost(postURL); 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("msg", "<message to=\""+sala+"\" from=\"igango\" type=\"system\"><body>"+comand+"</body></message>"));
			params.add(new BasicNameValuePair("token", "55c3c44aae1ab8c81e099793970c54f3"));
			
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
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
}
