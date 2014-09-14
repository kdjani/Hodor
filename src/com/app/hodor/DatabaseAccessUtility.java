package com.app.hodor;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DatabaseAccessUtility extends ContentProvider 
{
	DatabaseHandler dbHandler;
	public static String authority="com.app.hodor";
	public static Uri CONTENT_URI = Uri.parse("content://" + authority  + "/users");
	   
	@Override
	public boolean onCreate() {
		dbHandler = DatabaseHandler.createDatabase(getContext());
		if(dbHandler==null) {
			return false;
		}
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
		String[] selectionArgs, String sortOrder) {
		Cursor cursor=dbHandler.getAllUsersCursor();
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		int ret = dbHandler.deleteChat(Integer.valueOf(arg1));
		getContext().getContentResolver().notifyChange(uri, null);
		return ret;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		dbHandler.addChat(new Chat(values.getAsString(DatabaseHandler.USER_ID), values.getAsString(DatabaseHandler.BLOCKED_STATUS)));
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int retValue = dbHandler.updateChat(values, selection);
		getContext().getContentResolver().notifyChange(uri, null);
		return retValue;
	}
	@Override
	public String getType(Uri uri) {
		return null;
	}
}
