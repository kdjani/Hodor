package com.app.hodor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Base64;


public class ServiceAPIs extends AsyncTask<String, Void, String> implements IHodorService{ 
	
	private static final String ANDROID_DEVICE_TYPE = "android";
	private static final String REGISTER_PUSH = "register";

	public ServiceAPIs (){
		super();
	}

	public ServiceAPIs (Context a)  {
		super();
		associatedActivity = a;
	    dialog = new ProgressDialog(associatedActivity); 
	}

	protected Context associatedActivity;
    private ProgressDialog dialog; 
    private String dialogText;
	private final String SERVICE_POST_MESSAGE_METHOD_URI = "http://hodorservice.cloudapp.net/rest/default.aspx";
	private final String distributedsecret = "2711ea6a-d68a-4ffe-a431-6373325795bf";
	
	private static final String PROPERTY_APP_VERSION = "appVersion";
 
    /** 
     * This is the project number got from the API Console, as described in "GCM Getting Started."
	 */
    String SENDER_ID = "1045656476577";
    GoogleCloudMessaging gcm;   
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
 
    private void storeRegistrationId(Context context, String regId) { 
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
 
        SharedPreferences.Editor editor = prefs.edit(); 
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
 
    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
 
        if (registrationId.isEmpty()) { 
            return "";
        }
 
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) { 
            return ""; 
        }
 
        return registrationId;
    }
 
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerForPushNotification(String userName) {
        try {
	
	        String regid;
	        context = associatedActivity.getApplicationContext();

	        if (gcm == null) {
	            gcm = GoogleCloudMessaging.getInstance(context);
	        }
	
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
    	        regid = gcm.register(SENDER_ID);
    	        
    	        // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            }
            
		    // send the registration id to hodor service
		    SendDeviceToken(userName, regid);
 
        } catch (IOException ex) {
        }

        return ;
	}
     
	/**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
 
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        return associatedActivity.getSharedPreferences(HodorLoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    } 
	
	
	//method=sendhodor&sender=HEMAN&recipient=TARZAN&authtoken=mWeb6WR3jR%2FzRVfG5CxzBrv8rK8%2FRq3MwGBd3AURgrY%3D
	/* (non-Javadoc)
	 * @see com.app.hodor.IHodorService#SendHodor(java.lang.String, java.lang.String)
	 */
	@Override
	public void SendHodor(String sender, String recipient) {
    	String plainTextAuthToken = "method=sendhodor&sender=" + sender + "&recipient=" + recipient + distributedsecret; 
    	String postDataUrlEncoded = "";
    	dialogText = associatedActivity.getString(R.string.send_dialogtext);
    	try {
			postDataUrlEncoded = "method=" + URLEncoder.encode("sendhodor", "UTF-8") + 
					"&sender=" + URLEncoder.encode(sender, "UTF-8") + 
					"&recipient=" + URLEncoder.encode(recipient, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	super.execute(plainTextAuthToken, postDataUrlEncoded);
	}
	
	//method=createuser&username=BATMAN&authtoken=OY2jyj5XUnHN3R9uyN4RIqAdnwEPyv6mUZRU9w8x1ts%3D
	/* (non-Javadoc)
	 * @see com.app.hodor.IHodorService#CreateUser(java.lang.String)
	 */
	@Override
	public void CreateUser(String user) {
    	String plainTextAuthToken = "method=createuser&username=" + user + distributedsecret; 
    	String postDataUrlEncoded = "";
    	try {
			postDataUrlEncoded = "method=" + URLEncoder.encode("createuser", "UTF-8") + 
					"&username=" + URLEncoder.encode(user, "UTF-8");
	    	dialogText = associatedActivity.getString(R.string.create_user);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	super.execute(plainTextAuthToken, postDataUrlEncoded, user, REGISTER_PUSH);
	}

	//method=setdevicetoken&username=SRPANWAR&devicetoken=a43b9c4c68efcf856f2704bc946b58cb31b72c351b65215760b82871529f7a17&authtoken=Ue4jAml0ZMH8k9AIFW5FdexuSoYSUTSjx8a0b5%2FuCxQ%3D
	public void SendDeviceToken(String user, String deviceToken) {
    	String plainTextAuthToken = "method=setdevicetoken&username=" + user + "&devicetoken="+ deviceToken + "&devicetype=" + ANDROID_DEVICE_TYPE + distributedsecret; 
    	String postDataUrlEncoded = "";
    	try {
			postDataUrlEncoded = "method=" + URLEncoder.encode("setdevicetoken", "UTF-8") + 
					"&username=" + URLEncoder.encode(user, "UTF-8") + 
					"&devicetoken="+ URLEncoder.encode(deviceToken, "UTF-8") +
					"&devicetype="+ URLEncoder.encode(ANDROID_DEVICE_TYPE, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	this.doInBackground(new String [] {plainTextAuthToken, postDataUrlEncoded});
	}

	//method=blockuser&blocker=HEMAN&blockee=random&authtoken=7%2FISc0Bo56%2FL0uqcKXnU8nC%2F33kYxni%2FXME90h7Ezaw%3D
	/* (non-Javadoc)
	 * @see com.app.hodor.IHodorService#BlockUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void BlockUser(String blocker, String blockee) {
    	String plainTextAuthToken = "method=blockuser&blocker=" + blocker+ "&blockee=" + blockee + distributedsecret; 
    	String postDataUrlEncoded = "";
    	try {
			postDataUrlEncoded = "method=" + URLEncoder.encode("blockuser", "UTF-8") + 
					"&blocker=" + URLEncoder.encode(blocker, "UTF-8") + "&blockee=" + URLEncoder.encode(blockee, "UTF-8");
	    	dialogText = associatedActivity.getString(R.string.block_dialogtext);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	super.execute(plainTextAuthToken, postDataUrlEncoded);
	}
	
    @Override
    protected void onPostExecute(String result) {             
        super.onPostExecute(result); 
	        dialog.dismiss(); 
    } 
  
    @Override
    protected void onPreExecute() {         
        super.onPreExecute(); 
        dialog.setMessage(dialogText);
        dialog.show();             
    } 
  
    @Override
    protected String doInBackground(String... params) { 
        String result = null; 
          
        try {
        	
        	String plainTextAuthToken = params.length > 0 ? params[0] : ""; 
        	String postDataUrlEncoded = params.length > 1 ? params[1] : ""; 
        	String user = params.length > 2 ? params [2] : "";
        	boolean registerForPushNotification = params.length > 3 ? (params[3] == REGISTER_PUSH): false;

        	// compute a SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
            byte [] shaAuthToken= md.digest(plainTextAuthToken.getBytes("UTF-8"));
            
            // base64 encode the sha256authtoken
            String base64AuthToken= Base64.encodeToString(shaAuthToken, Base64.NO_WRAP);
            
            // add the authtoken to the postData and urlencode the same. 
        	// "method=sendhodor&sender=HEMAN&recipient=SRPANWAR&authtoken=mWeb6WR3jR%2FzRVfG5CxzBrv8rK8%2FRq3MwGBd3AURgrY%3D";
        	postDataUrlEncoded = postDataUrlEncoded + "&authtoken=" + URLEncoder.encode(base64AuthToken, "UTF-8");
        	
            URL u = new URL(SERVICE_POST_MESSAGE_METHOD_URI);               
            HttpURLConnection conn = (HttpURLConnection) u.openConnection(); 
            conn.setRequestMethod("POST"); 
            conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");  
            conn.setRequestProperty("Content-Length", "" + 
                     Integer.toString(postDataUrlEncoded.getBytes().length));
            conn.connect(); 
				      		
            //Send request
            DataOutputStream wr = new DataOutputStream (
                        conn.getOutputStream ());
            wr.writeBytes (postDataUrlEncoded);
            wr.flush ();
            wr.close ();

            //Get Response	
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer(); 
            while((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
            rd.close();
           
            result = response.toString();
            
            if (registerForPushNotification && (!result.isEmpty())) {
            	registerForPushNotification(user);
            }
            
            return result; 
        } 
        catch(Throwable t) { 
            t.printStackTrace(); 
        } 
        return null; 
    } 
}
