package com.phigital.ai.Notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.Post.PostComments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;


public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(isAppIsInBackground(getApplicationContext())) {
            // Show the notification
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            String savedCurrentUser = sp.getString("Current_USERID", "None");
            String sent = remoteMessage.getData().get("sent");
            String user = remoteMessage.getData().get("user");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fUser != null && sent.equals(fUser.getUid())){
                if (!savedCurrentUser.equals(user)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        sendOAndAboveNotification(remoteMessage);
                    }else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        } else {
            // Don't show notification
        }

    }
    private void sendNormalNotification(RemoteMessage remoteMessage){
        String user = ""+remoteMessage.getData().get("user");
        String icon = ""+remoteMessage.getData().get("icon");
        String title = ""+remoteMessage.getData().get("title");
        String body = ""+remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        if (title.equals("New Message")) {
            Intent intent = new Intent(this, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("hisUid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notificationManager.notify(j, builder.build());
        }
        if (title.equals("New Notification")){
            String postId = ""+remoteMessage.getData().get("postId");
            Intent intent = new Intent(this, PostDetails.class);
            intent.putExtra("mean", "post");
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notificationManager.notify(j, builder.build());
        }
        if (title.equals("New Comment")){
            String postId = ""+remoteMessage.getData().get("postId");
            Intent intent = new Intent(this, PostComments.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", postId);
            bundle.putString("userid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notificationManager.notify(j, builder.build());
        }
        else {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage){
        String user = ""+remoteMessage.getData().get("user");
        String icon = ""+remoteMessage.getData().get("icon");
        String title = ""+remoteMessage.getData().get("title");
        String body = ""+remoteMessage.getData().get("body");
        @SuppressWarnings("unused") RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i= Integer.parseInt(user.replaceAll("[\\D]", ""));
        if (title.equals("New Message")) {
            Intent intent = new Intent(this, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("hisUid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notification1.getManager().notify(j, builder.build());
        }
        if (title.equals("New Notification")){
            String postId = ""+remoteMessage.getData().get("postId");
            Intent intent = new Intent(this, PostDetails.class);
            intent.putExtra("mean", "post");
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notification1.getManager().notify(j, builder.build());
        }
        if (title.equals("New Comment")){
            String postId = ""+remoteMessage.getData().get("postId");
            Intent intent = new Intent(this, PostComments.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", postId);
            bundle.putString("userid", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);
            int j = 0;
            if (i > 0) {
                j = i;
            }
            notification1.getManager().notify(j, builder.build());
        }
        else{

        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }
}
