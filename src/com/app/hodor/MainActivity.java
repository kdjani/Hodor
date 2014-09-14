package com.app.hodor;


import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor>  {
	private static final int CONTACT_PICKER_RESULT = 1;
	private UserListViewAdapter adapter;
	private String meUserName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	    SharedPreferences settings = this.getSharedPreferences(HodorLoginActivity.SharedPreferenceRoot, 0);
	    this.meUserName = settings.getString(HodorLoginActivity.SharedPreferenceUserNameKey, "");
		
		final ListView listView = (ListView) findViewById(R.id.user_list); 

		String[] columns = 
    		{
        		DatabaseHandler.ID,
        		DatabaseHandler.USER_ID, 
        		DatabaseHandler.BLOCKED_STATUS
        		};
        
        Cursor cursor = getContentResolver().query(DatabaseAccessUtility.CONTENT_URI, columns, null, null, null);
        
        if(cursor != null) {
        	cursor.moveToFirst();
        }
        	
        adapter = new UserListViewAdapter(this, cursor, columns, listView);
	    
        // use your custom layout
	    listView.setAdapter(adapter);
	    
	    listView.setOnTouchListener(new SwipeDetector());

	    listView.setOnItemClickListener(new OnItemClickListener() {
	    	   @Override
	    	   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
 			      TextView tv = (TextView) view.findViewById(R.id.user);
 		            (new ServiceAPIs(MainActivity.this)).SendHodor(meUserName, tv.getText().toString());    		
	    	   }
	    	});
	    
		final Button inviteButton = (Button) findViewById(R.id.invite_button); 
		inviteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	    		/* invite another user over sms*/
				Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
				intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
				startActivityForResult(intent, CONTACT_PICKER_RESULT);				
			}
			
		});

		final Button addButton = (Button) findViewById(R.id.add_button); 
		final EditText userAddEditBox = (EditText) findViewById(R.id.user_add);
		userAddEditBox.setVisibility(View.GONE);
 
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		        addButton.setVisibility(View.GONE);
	            userAddEditBox.setVisibility(View.VISIBLE);
	            // Request focus and show soft keyboard automatically
	    		userAddEditBox.requestFocus();
	            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.showSoftInput(userAddEditBox, InputMethodManager.SHOW_IMPLICIT);
			}
			
		});
		
		userAddEditBox.setOnKeyListener(new OnKeyListener() {

			@Override
			 public boolean onKey(View view, int keyCode, KeyEvent event) {
				 
				  if (keyCode == EditorInfo.IME_ACTION_DONE ||
						  event.getAction() == KeyEvent.ACTION_DOWN &&
						  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				    
					  if (!event.isShiftPressed()) {
							String user = userAddEditBox.getText().toString();
							user = user.toLowerCase(Locale.getDefault());
							user = user.trim();
							if (user.length() > 0) {
								
							    ContentValues values = new ContentValues();
							    values.put(DatabaseHandler.USER_ID, user);
							    values.put(DatabaseHandler.BLOCKED_STATUS, "UnBlocked");
							    
								getContentResolver().insert(DatabaseAccessUtility.CONTENT_URI, values);
							}
						   
							addButton.setVisibility(View.VISIBLE);
				            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				            imm.hideSoftInputFromWindow(userAddEditBox.getApplicationWindowToken(), 0);
				            userAddEditBox.setText(null);
							userAddEditBox.setVisibility(View.GONE);
							return true; 
					   }                
					  
					  return false;
				  }
				  
				  return false; // pass on to other listeners.
			 }
		});

	    getLoaderManager().initLoader(1, null, this);

	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    CursorLoader cursorLoader =
	            new CursorLoader(this,
	                    DatabaseAccessUtility.CONTENT_URI,
	                    null, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	    adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	    adapter.swapCursor(null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(item);
        myShareActionProvider.setShareHistoryFileName("test");
        
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sms_body));
        myIntent.setType("text/plain");

        myShareActionProvider.setShareIntent(myIntent);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		if (item.getItemId() == R.id.menu_item_ratings) {
		  Intent intent = new Intent(Intent.ACTION_VIEW);
		  intent.setData(Uri.parse("market://details?id=com.app.hodor"));
		  startActivity(intent);			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {          
    	if (resultCode == RESULT_OK) {              
    		switch (requestCode) 
    		{              
    			case CONTACT_PICKER_RESULT:               
    				Uri result = data.getData();               
    				// get the contact id from the Uri                 
    				String id = result.getLastPathSegment();               
    				// query for everything phone                 
    				Cursor cursor = getContentResolver().query(
    					Phone.CONTENT_URI, null,                         
    					Phone._ID + "=?", 
    					new String[]{id}, 
    					null
    					);                
    					
    				startManagingCursor(cursor);               
    				cursor.moveToFirst();                 
    				String phoneNumber = null;
    			
    				if (cursor.moveToFirst()) {                     
    					int phoneIdx = cursor.getColumnIndex(Phone.DATA);                     
    					phoneNumber = cursor.getString(phoneIdx);                   
    				}   
    				stopManagingCursor(cursor);
    				
    				if (!TextUtils.isEmpty(phoneNumber)){
    					Intent smsIntent = new Intent(Intent.ACTION_VIEW);       
    					smsIntent.setType("vnd.android-dir/mms-sms");       
    					smsIntent.putExtra("address", phoneNumber);       
    					smsIntent.putExtra("sms_body",getResources().getString(R.string.sms_body));       
    					startActivity(smsIntent);               
    				}                  
    				break;              
			}            
		} else {
    			// gracefully handle failure           
		}  
	} 

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	
}
