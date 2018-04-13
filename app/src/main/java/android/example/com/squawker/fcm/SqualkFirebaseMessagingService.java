package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// TODO (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.
public class SqualkFirebaseMessagingService extends FirebaseMessagingService {

    private final String LOG_TAG = SqualkFirebaseMessagingService.class.getSimpleName();

    // Maximum char length for a visible notification message
    private static final int MAX_CHARS = 30;

    private final String AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private final String AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private final String MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private final String DATE = SquawkContract.COLUMN_DATE;

    // TODO (2) As part of the new Service - Override onMessageReceived. This method will
    // be triggered whenever a squawk is received. You can get the data from the squawk
    // message using getData(). When you send a test message, this data will include the
    // following key/value pairs:
    // test: true
    // author: Ex. "TestAccount"
    // authorKey: Ex. "key_test"
    // message: Ex. "Hello world"
    // date: Ex. 1484358455343
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(LOG_TAG, "Remote message received from: " + remoteMessage.getFrom());

        Map<String, String > data = remoteMessage.getData();

        if (data.size() > 0) {
            Log.e(LOG_TAG, "Data sent: " + data);

            // Send a notification of new data
            sendNotification(data);
            insertSquawk(data);
        }
    }


    // TODO (3) As part of the new Service - If there is message data, get the data using
    // the keys and do two things with it :
    // 1. Display a notification with the first 30 character of the message
    // 2. Use the content provider to insert a new message into the local database
    // Hint: You shouldn't be doing content provider operations on the main thread.
    // If you don't know how to make notifications or interact with a content provider
    // look at the notes in the classroom for help.

    /* Creates and displays a notification showing the received message */
    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        // If set, and the activity being launched is already running in the current task, then
        // instead of launching a new instance of that activity, all of the other activities on top
        // of it will be closed and this Intent will be delivered to the (now on top) old activity
        // as a new Intent.
        // See: https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                // FLAG_ONE_SHOT - Flag indicating that this PendingIntent can be used only once.
                PendingIntent.FLAG_ONE_SHOT);

        String author = data.get(AUTHOR);
        String message = data.get(MESSAGE);

        // Truncate the message
        if (message.length() > MAX_CHARS){
            // "\u2026" - is the char code for the ellipsis char
            message = message.substring(0, MAX_CHARS) + "\u2026";
        }

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message).setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    /* Database insert for a squawk */
    private void insertSquawk(final Map<String , String> data) {
        AsyncTask<Void, Void, Void> insertSquawk = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SquawkContract.COLUMN_AUTHOR, data.get(AUTHOR));
                contentValues.put(SquawkContract.COLUMN_MESSAGE, data.get(MESSAGE));
                contentValues.put(SquawkContract.COLUMN_DATE, data.get(DATE));
                contentValues.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(AUTHOR_KEY));

                getContentResolver()
                        .insert(SquawkProvider
                                .SquawkMessages.CONTENT_URI, contentValues);

                return null;
            }
        };
        insertSquawk.execute();
    }

}
