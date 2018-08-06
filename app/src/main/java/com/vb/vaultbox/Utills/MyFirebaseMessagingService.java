package com.vb.vaultbox.Utills;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vb.vaultbox.R;
import com.vb.vaultbox.Splash;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    String messagetype = "";
    String msg, msg_str;
    SharedPreferences prefs;
    JSONObject object1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        try {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            try {
                msg = object.getString("data");
                object1 = new JSONObject(msg);
                messagetype = object1.getString("type");
            } catch (Exception e) {

            }

            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Log.d(TAG, "Message type Body: " + remoteMessage.getData().get("type"));


            try {
//                sendNotification(remoteMessage.getData().get("message"));
                try {
                    msg_str = object1.getString("message");
                } catch (Exception e) {
                    msg_str = "new Message";
                }
                if (messagetype.equalsIgnoreCase("Chat")) {
                    sendNotification(object1.getString("person_name"),
                            msg_str,
                            object1.getString("person_id"),
                            object1.getString("person_name"),
                            object1.getString("person_image"),
                            object1.getString("person_firebaseid"),
                            object1.getString("deviceToken"));

                } else if (messagetype.equalsIgnoreCase("GroupChat")) {
//                    sendNotificationGroup(object1.getString("person_name"),
//                            msg_str,
//                            object1.getString("person_id"),
//                            object1.getString("person_name"),
//                            object1.getString("person_image"),
//                            object1.getString("person_firebaseid"),
//                            object1.getString("deviceToken"));
                } else {
                    sendNotification(object1.getString("person_name"),
                            msg_str,
                            object1.getString("person_id"),
                            object1.getString("person_name"),
                            object1.getString("person_image"),
                            object1.getString("person_firebaseid"),
                            object1.getString("deviceToken"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String messageBody) {

        Intent intent;
        intent = new Intent(this, Splash.class);

//        Intent i = new Intent(this, AlarmRing.class);
//        this.startService(i);

//        Intent in = new Intent("com.bmb.bookmebarber.global.NotiRecever");
//        in.setAction("com.bmb.bookmebarber.global.NotiRecever");
//        sendBroadcast(in);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sounds");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setSmallIcon(R.drawable.icon);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.icon);
        }
        notificationBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        Random rand = new Random();
        int as = rand.nextInt();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(as /* ID of notification */, notificationBuilder.build());

    }

    private void sendNotification(String messageTitle, String messageBody, String person_id, String person_name, String person_image_url, String person_firebase_id, String deviceToken) {
        Intent intent = new Intent(this, Splash.class);
        intent.putExtra("stdid", person_id);
        intent.putExtra("name", person_name);
        intent.putExtra("image", person_image_url);
        intent.putExtra("firebaseid", person_firebase_id);
        intent.putExtra("deviceToken", deviceToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sounds");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setSmallIcon(R.drawable.icon);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.icon);
        }

        notificationBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        Random rand = new Random();
        int as = rand.nextInt();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(as /* ID of notification */, notificationBuilder.build());

        SaveCounter(person_id);
    }

//    private void sendNotificationGroup(String messageTitle, String messageBody, String person_id, String person_name, String person_image_url, String person_firebase_id, String deviceToken) {
//
//        Intent intent = new Intent(this, Group_inner.class);
//        intent.putExtra("stdid", person_id);
//        intent.putExtra("name", person_name);
//        intent.putExtra("image", person_image_url);
//        intent.putExtra("firebaseid", person_firebase_id);
//        intent.putExtra("createdon", person_id);
//        intent.putExtra("deviceTokenList", deviceToken);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sounds");
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationBuilder.setSmallIcon(R.drawable.app_icon);
//        } else {
//            notificationBuilder.setSmallIcon(R.drawable.app_icon);
//        }
//
//        notificationBuilder.setContentTitle(getResources().getString(R.string.app_name))
//                .setContentTitle(messageTitle)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
//                .setSound(defaultSoundUri)
//                .setVibrate(new long[]{1000, 1000})
//                .setContentIntent(pendingIntent);
//
//        Random rand = new Random();
//        int as = rand.nextInt();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(as /* ID of notification */, notificationBuilder.build());
//
//        SaveCounter(person_id);
//    }

    public void SaveCounter(String person_id) {
        SharedPreferences sharedPreferences = getSharedPreferences("registerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("person_id" + person_id, 0);
        count++;
        myEditor.putInt("person_id" + person_id, count);
        myEditor.apply();//returns nothing,don't forgot to commit changes
        Log.e("Counter: person_id" + person_id, " = " + count);
    }

}
