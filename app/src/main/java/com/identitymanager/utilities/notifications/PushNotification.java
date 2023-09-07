package com.identitymanager.utilities.notifications;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.identitymanager.R;
import com.identitymanager.fragments.DashboardFragment;
import com.identitymanager.models.enums.NotificationType;

public class PushNotification {

    private final String CHANNEL_ID = "identityManagerNotification";
    private Context context;

    public PushNotification(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void sendNotification (String textContent) {

        this.createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this.context, DashboardFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        notificationManager.notify(NotificationType.PASSWORD_CHANGE.getValue(), this.createNotificationBuilder(textContent).build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, this.CHANNEL_ID, importance);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private NotificationCompat.Builder createNotificationBuilder(String textContent) {
        String NOTIFICATION_CONTENT_TITLE = "Password need a change";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, this.CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder;
    }
}
