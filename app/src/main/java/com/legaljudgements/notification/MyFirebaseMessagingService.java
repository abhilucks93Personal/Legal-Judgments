package com.legaljudgements.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.legaljudgements.R;
import com.legaljudgements.login.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by abhi on 28/3/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    int badgeCount = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            JSONObject mJsonObject = new JSONObject(remoteMessage.getData());
            JSONObject mNewJsonObject = new JSONObject(mJsonObject.getString("data"));

            String message = mNewJsonObject.getString("message");
            String title = mNewJsonObject.getString("contentTitle");
            String ticker_text = mNewJsonObject.getString("tickerText");
            String ItemId = mNewJsonObject.getString("ItemId");


            sendNotification(ItemId, title, message, ticker_text);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void sendNotification(String ItemId, String title, String message, String ticker_text) {

        badgeCount++;
        ShortcutBadger.applyCount(this, badgeCount);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("tag", ticker_text);
        intent.putExtra("id", ItemId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon3).setTicker("Legal Judgments")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

      /*  NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, noBuilder.build()); //0 = ID of notification*/


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}