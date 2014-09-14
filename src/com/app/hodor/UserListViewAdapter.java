package com.app.hodor;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.hodor.R;

public class UserListViewAdapter extends SimpleCursorAdapter {

  final static int colors[] = {
	  	R.color.khakhi_2_color,
	  
	  	R.color.blue_2_color,
	  	R.color.pink_2_color,
	  	
	  	R.color.teal_2_color,
	  	
	  	R.color.red_2_color,
	  	
	  	R.color.green_2_color,
	  	
	  	
	  	R.color.pink_3_color,
	  	R.color.violet_2_color,
	  	R.color.khakhi_3_color,
	  	R.color.blue_3_color,
	  	R.color.pink_4_color,
	  	R.color.green_3_color,
	  	R.color.khakhi_4_color,
	  	
	  };
  
  int lastColorUsed = 0;
  
  private String meUserName;
	
  public UserListViewAdapter(Context context,  Cursor data, String[] fields, ListView listView) {
    super(context, R.layout.activity_recent_list, data, fields, null, 0);
    lastColorUsed = 0;
    
    SharedPreferences settings = mContext.getSharedPreferences(HodorLoginActivity.SharedPreferenceRoot, 0);
    this.meUserName = settings.getString(HodorLoginActivity.SharedPreferenceUserNameKey, "");

  }

  @Override
  public View getView(int pos, View view, ViewGroup arg2) {

	  View convertView = super.getView(pos, view, arg2);
	  if (convertView != null) {
		  lastColorUsed = pos % colors.length; 
		  convertView.setBackgroundResource(colors[lastColorUsed]);
	  }
	
	  return convertView;
  }

  @Override
  public void bindView(View rowView, Context context, Cursor c) {
      final ViewFlipper viewFlipper = (ViewFlipper) rowView.findViewById(R.id.view_flipper);
      if (viewFlipper.getDisplayedChild() != 0) {
    	  viewFlipper.showNext();
      }

      final String userId = c.getString(c.getColumnIndex(DatabaseHandler.USER_ID));
      TextView textViewSender = (TextView) rowView.findViewById(R.id.user);
      textViewSender.setText(userId.toUpperCase(Locale.getDefault())); 
      textViewSender.setTypeface(Typeface.DEFAULT_BOLD);
      
      final String itemId = c.getString(c.getColumnIndex(DatabaseHandler.ID));
      Button deleteButton = (Button) rowView.findViewById(R.id.delete_button);
      deleteButton.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			mContext.getContentResolver().delete(DatabaseAccessUtility.CONTENT_URI, itemId, null);
		}
    	  
      });

      Button blockButton = (Button) rowView.findViewById(R.id.block_button);
      blockButton.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			mContext.getContentResolver().delete(DatabaseAccessUtility.CONTENT_URI, itemId, null);
            (new ServiceAPIs(mContext)).BlockUser(meUserName, userId);    		
		}
    	  
      });

      Button cancelButton = (Button) rowView.findViewById(R.id.cancel_button);
      cancelButton.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (viewFlipper.getDisplayedChild() != 0) {
		    	  viewFlipper.showNext();
		    }
		}
    	  
      });

  }
  
}