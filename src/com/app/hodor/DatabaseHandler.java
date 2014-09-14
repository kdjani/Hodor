package com.app.hodor;

import java.security.InvalidAlgorithmParameterException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static DatabaseHandler dBClient = null;

	// Contacts Table Columns names
	public static final String ID = "_id";
	public static final String USER_ID = "userId";
	public static final String BLOCKED_STATUS = "blockedStatus";
	public static final String UNREAD_STATE = "unreadState";
	public static final String HODOR_COUNT = "hodorCount";

	
	public static final int MIN_USER_SELECTION_SIZE = 1;

	public static DatabaseHandler createDatabase(Context context) {
		if (dBClient == null) {
			dBClient = new DatabaseHandler(context);
		}

		return dBClient;
	}

	public boolean UpdateOrAddNewMessage(String user) throws Exception {
		 	
		boolean isNew = false;
 		if ( user.isEmpty() ) {
 			throw new InvalidAlgorithmParameterException("incoming user cannot be empty");
 		}

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(true, TABLE_USER, new String[] {
                 ID, HODOR_COUNT, UNREAD_STATE, BLOCKED_STATUS },
                 USER_ID + "=?" , 
                 new String[] {user},
                 null, null, null, null);
		
		
		ContentValues values = new ContentValues();

		if (cursor.moveToFirst() && cursor.getCount() > 0) {
			String rowId = cursor.getString(cursor.getColumnIndex(ID));
			long count = cursor.getLong(cursor.getColumnIndex(HODOR_COUNT));
			long unReadState = cursor.getLong(cursor.getColumnIndex(UNREAD_STATE));
			String blockedStatus = cursor.getString(cursor.getColumnIndex(BLOCKED_STATUS));

			values.put(USER_ID, user);
			values.put(BLOCKED_STATUS, blockedStatus);
			values.put(HODOR_COUNT, count+1);
			values.put(UNREAD_STATE, unReadState);
			updateChat(values, rowId);
		} else {
			isNew = true;
			Chat chat = new Chat(user, 1, 0);
			addChat(chat);
		}
		
		return isNew;
	}

	public Cursor getAllUsersCursor() {
		String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
				+ USER_ID + "<>''";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public static DatabaseHandler get(Context context) {
		if (dBClient == null) {
			createDatabase(context);
		}
		return dBClient;
	}

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Hodor";

	private static final String TABLE_USER = "user";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "(" + ID
				+ " INTEGER PRIMARY KEY," + USER_ID + " TEXT NOT NULL UNIQUE,"
				+ BLOCKED_STATUS + " TEXT," + UNREAD_STATE+ " INTEGER," + HODOR_COUNT + " INTEGER" + ")";

		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		onCreate(db);
	}

	public void addChat(Chat chat) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USER_ID, chat.receiverId);
		values.put(BLOCKED_STATUS, chat.blocked);
		values.put(HODOR_COUNT, 0);
		values.put(UNREAD_STATE, 0);

		db.insert(TABLE_USER, null, values);
		db.close();
	}

	public int deleteChat(long rowId) {
		SQLiteDatabase db = this.getWritableDatabase();

		int ret = db.delete(TABLE_USER, ID + "=" + rowId, null);

		db.close();
		return ret;
	}

	public int updateChat(ContentValues values, String rowId) {
		SQLiteDatabase db = this.getWritableDatabase();

		int retValue = db.update(TABLE_USER, values, ID + "=" + rowId, null);
		db.close();
		return retValue;
	}

}
