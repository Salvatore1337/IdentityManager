package com.identitymanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.identitymanager.workers.NotificationWorker;

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // We are starting MyService via a worker and not directly because since Android 7,
        // any process called by a BroadcastReceiver (only manifest-declared receiver) is run at low priority
        // and hence eventually killed by Android.
        WorkManager workManager = WorkManager.getInstance();
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
        workManager.enqueue(startServiceRequest);
    }
}
