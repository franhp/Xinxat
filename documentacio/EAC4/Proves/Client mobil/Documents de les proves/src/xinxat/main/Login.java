package xinxat.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

    String token = "false";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView login1 = (TextView)findViewById(R.id.textView3);
        TextView login2 = (TextView)findViewById(R.id.textView5);
        TextView md5 = (TextView)findViewById(R.id.textView6);
        Button btnRoster = (Button)findViewById(R.id.button1);
        
        //Comprovació de login amb usuari real
        String username1 = "igango";
        String password1 = "63033abbae8aa5fa279db8ecd173f0de";
		try {
			login1.setText(login(username1, password1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		//Comprovació de login amb usuari fals
		String username2 = "isaacgango";
	    String password2 = "d37cffe13de1c3db94b7b509830a46e4";
		try {
			login2.setText(login(username2, password2));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//Comprovació de la realització md5
		if (password1.equals(md5("igangoV1V4fDA"))){
			md5.setText("Succes!");
		}
		else md5.setText("MD5 Error");
		
		//Saltem a la comprovació del Roster
	      btnRoster.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            Intent intent = new Intent(Login.this, Roster.class);
	            startActivity(intent);
	            }
	      });
}
    	
    
    //Fucnio que ens comprova si el usuari es valida correctament passant-li el nom i la contrassenya
    private String login(String username, String password) throws Exception {
    	String str="null";
    	try {
            HttpClient client = new DefaultHttpClient();  
            String getURL = "http://api.xinxat.com/?user="+username+"&pass="+password;
            //String getURL = "http://api.xinxat.com/?user=hektor&pass=e08526677c50ec2a8236c7c203fd87cf";
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);  
            HttpEntity resEntityGet = responseGet.getEntity();  
            if (resEntityGet != null) {
            	str = EntityUtils.toString(resEntityGet);  
            } 
        }catch (Exception e) {
        	MessageBox("ERROR: " + e.toString());
		}
		return str;
    }
    
    //Funció que ens covnerteix la password en md5 
    public static final String md5(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
     
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
}