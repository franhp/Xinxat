package xinxat.main;
/*
 * Login.java
 */

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
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Procastinadors
 */
public class Login extends Activity {
	
	//Inicialització del token a false
    String token = "false";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Creació de variables
        final EditText textUser = (EditText)findViewById(R.id.TxtNombre);
		final EditText textPass = (EditText)findViewById(R.id.TxtPassword);
		final DBAdapter db = new DBAdapter(this);
		
		//Obtenim el usuari, si aquest havia recordat el seu usuari i contrasenya
		 db.open(); 
		 //Crida ala funció getX
		 Cursor c = db.getX();
		 if (c.moveToFirst())
	        {
	            do {          
	            	//Crida a la funció que ens mostrarà el ultim usuari recordat en els camps de usuari i contrasenya
	                DisplayTitle(c);
	            } while (c.moveToNext());
	        }
		   //Tanquem la Base de Dades
	       db.close();
	       
	       
	       
	     //Declaració del boto de checkbox
        final CheckBox checkbox = (CheckBox) findViewById( R.id.checkbox );

        
        //Declaració del boto de login
        final Button btnLogin = (Button)findViewById(R.id.BtnLogin);      
        btnLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
    	        if (checkbox.isChecked())
    	        {
    	        	//Guardarem el Usuari i Contrasenya si el checkbox està marcat
    				String username = textUser.getText().toString();
    				String password = textPass.getText().toString();
    	        	db.open();
    	        	db.insertUser(username, password, "s");
    	        	db.close();
    	        }
    	        
            	//Declaració de variables
				Intent intent = new Intent(Login.this, Roster.class);
    			EditText textUser = (EditText)findViewById(R.id.TxtNombre);
				EditText textPass = (EditText)findViewById(R.id.TxtPassword);
				
				//Agafem el username del EditText
                String username = textUser.getText().toString();
                
                //Convertim la contrasenya en format md5, per dificultar-ne l'obtenció d'aquesta a tercers
                String password = md5(textPass.getText().toString()+"V1V4fDA");
                
                try {
                	//Assignem al toquen el resultat de la funció login, que crida un get de la web 
                	//que ens tornarà un login si ha anat bé i sinó ens tornarà false
                	
                	token = login(username, password);
                }catch (Exception e) {
					e.printStackTrace();
				}

                //Si l'usuari es valida correctament, enviarem el token i el nom d'usuari a la següent activitat mitjançant un intent
            	if (!token.equals( "false" )){
            		
	            	Bundle a = new Bundle(); 
	            	Bundle b = new Bundle(); 

	            	a.putString("TOKEN", token);
	            	b.putString("USERNAME", username);
	            	
	            	intent.putExtras(a);
	            	intent.putExtras(b);

	                startActivity(intent);
	                
            	}
            	//Usuari validat incorrectament
            	else MessageBox("Wrong user/pass");
            }
        });
    }

	/**
	 * Funció amb la qual mostrem el nom d'usuari i contrasenya recordats
	 * 
     * @param Cursor c
     */
    public void DisplayTitle(Cursor c)
    {

    	if(c.moveToFirst() ){
    		do{
    			 //Ens movem per el cursor i agafem el ultim usuari recordat
    			 EditText textUser = (EditText)findViewById(R.id.TxtNombre);
    			 EditText textPass = (EditText)findViewById(R.id.TxtPassword);
    			 String usuari = c.getString(1);
    			 String contrasenya = c.getString(2);
    			 
    			 textUser.setText(usuari);
    			 textPass.setText(contrasenya);
    			 
    		}while (c.moveToNext());
		}  
    } 
    
    
    
	/**
	 * Funció que ens comprova si el usuari es valida correctament passant-li el nom i la contrasenya
	 * 
     * @param String username
     * @param String password
     * @return
     * @throws Exception
     */
    private String login(String username, String password) throws Exception {
    	String str="null";
    	try {
            HttpClient client = new DefaultHttpClient();  
            /*Realitzem un get a la següent url, si la password i el usuari són 
            correctes ens crea el token si són incorrectes ens torna false*/
            String getURL = "http://api.xinxat.com/?user="+username+"&pass="+password;
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
    
    /**
     * Funció que ens covnerteix la password en md5 
     * 
     * @param String s
     * @return
     */
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
    
    /**
     * Funció que serveix per mostrar un message box en la layout quan quelcom va malament
     * 
     * @param String message
     */
	public void MessageBox(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}
}