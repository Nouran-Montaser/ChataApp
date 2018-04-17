package com.example.lenovo.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Lenovo on 2/12/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(notification_title).
                setContentText(notification_message);

        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_ID" ,from_user_id);
        PendingIntent resultPendingIntent =  PendingIntent.getActivity(this , 0 , resultIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, builder.build());

    }
}
