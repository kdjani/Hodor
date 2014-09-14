package com.app.hodor;

import java.util.Locale;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    private static final String TICKER_TEXT = "tickerText";
	private static final String CONTENT_TITLE = "contentTitle";
	private static final String NOTIFICATION_MESSAGE = "message";
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "HODOR GCM";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error", extras.toString(), "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server", extras.toString(), "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
            	String title = extras.getString(TICKER_TEXT);
            	String contentTitle = extras.getString(CONTENT_TITLE);
            	String message = extras.getString(NOTIFICATION_MESSAGE);
				String user = title;
				if (user.contains(":")) {
					user = user.substring(0, user.indexOf(":"));					
				}
				user = user.toLowerCase(Locale.getDefault());
				user = user.trim();
				if (user.length() > 0) {
					
				    ContentValues values = new ContentValues();
				    values.put(DatabaseHandler.USER_ID, user);
				    values.put(DatabaseHandler.BLOCKED_STATUS, "UnBlocked");
				    
					getContentResolver().insert(DatabaseAccessUtility.CONTENT_URI, values);
				}
            	            	
                // Post notification of received message.
                sendNotification(title, contentTitle, message);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    public void sendNotification(String title, String contentTitle, String message) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HodorLoginActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setContentTitle(contentTitle)
        .setSmallIcon(R.drawable.ic_launcher)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(title))
        .setSound(Uri.parse("android.resource://com.app.hodor/"+ R.raw.hodor))
        .setContentText(title);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
