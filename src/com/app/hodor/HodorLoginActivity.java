
package com.app.hodor;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HodorLoginActivity extends Activity implements OnServiceResponse {

	final static String SharedPreferenceRoot = "Hodor";
	final static String SharedPreferenceIsUserLoggedInKey = "IsUserLoggedIn";
	final static String SharedPreferenceUserNameKey = "MeUserName";
 
    private String userName; 
	private EditText userNameBox;
	private TextView headingText;
	private LinearLayout buttonsLayout;
	private Button returnButton;
	private TextView errorText;


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings. 
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
 
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

	
	@Override
	protected void onResume() {
		super.onResume();
		
        // Check device for Play Services APK.		 
        checkPlayServices();

		SharedPreferences settings = getSharedPreferences(SharedPreferenceRoot, 0);
		boolean isUserLoggedIn = settings.getBoolean(SharedPreferenceIsUserLoggedInKey, false);
		userName = settings.getString(SharedPreferenceUserNameKey, "");

        setupLoginPage(isUserLoggedIn);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        // Check device for Play Services APK.		 
        checkPlayServices();

		SharedPreferences settings = getSharedPreferences(SharedPreferenceRoot, 0);
		boolean isUserLoggedIn = settings.getBoolean(SharedPreferenceIsUserLoggedInKey, false);
		userName = settings.getString(SharedPreferenceUserNameKey, "");
		
		if (isUserLoggedIn) {
			Navigate(com.app.hodor.MainActivity.class);			
		} else {
			setupLoginPage(isUserLoggedIn);			
		}
	}
	
	private void setupLoginPage(boolean isUserLoggedIn) {

		userNameBox = (EditText) findViewById(R.id.userName);
		this.buttonsLayout = (LinearLayout) findViewById(R.id.loginlinearLayout);
		this.headingText= (TextView) findViewById(R.id.textView1);
		this.returnButton = (Button) findViewById(R.id.cancel_login_button);
		this.errorText = (TextView) findViewById(R.id.errortextView1);

		this.errorText.setText(null);
		
		if (isUserLoggedIn) {
			userNameBox.setVisibility(View.GONE);
			headingText.setText(userName);
			buttonsLayout.setVisibility(View.VISIBLE);
		} else {
			userNameBox.setVisibility(View.VISIBLE);
			userNameBox.setText(null);
			headingText.setText(R.string.register_handle);
			buttonsLayout.setVisibility(View.GONE);			
		}

		returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Navigate(com.app.hodor.MainActivity.class);
			}
			
		});

		setupUserNameBox();
	}
	
	private void setupUserNameBox() {
		userNameBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(userNameBox, InputMethodManager.SHOW_IMPLICIT);
		userNameBox.setOnKeyListener(new OnKeyListener() {

			@Override
			 public boolean onKey(View view, int keyCode, KeyEvent event) {
				 
				  if (keyCode == EditorInfo.IME_ACTION_DONE ||
						  event.getAction() == KeyEvent.ACTION_DOWN &&
						  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				    
					  if (!event.isShiftPressed()) {
							userName = userNameBox.getEditableText().toString();
							if (userName != null && !userName.isEmpty())
							{								
								(new CreateUserServiceAPI(HodorLoginActivity.this)).CreateUser(userName);
							}				
							return true; 
					   }                
					  
					  return false;
				  }
				  
				  return false; // pass on to other listeners.
			 }
		});
	}
	
	private void Navigate(Class<?> activityClass){
		Intent newActivity = new Intent(this, activityClass);
		startActivity(newActivity);
	}
	
	@Override
	public void onSuccess(String result) {
		if (!result.isEmpty()) {
			SharedPreferences settings = getSharedPreferences(SharedPreferenceRoot, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(SharedPreferenceUserNameKey, userName);
			editor.putBoolean("IsUserLoggedIn", true);
			editor.commit();
			
	        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(userNameBox.getApplicationWindowToken(), 0);
			
			Navigate(com.app.hodor.MainActivity.class);	
			finish();			
		} else {
			this.errorText.setText(R.string.user_already_exists_text);
		}
	}
	
}
