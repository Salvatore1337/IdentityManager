package com.identitymanager.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.identitymanager.database.firestore.queries.FirebaseAccountQuery;
import com.identitymanager.models.data.Account;
import com.identitymanager.utilities.notifications.PushNotification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class NotificationWorker extends Worker {

    private Context context;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userId = this.getUserId();
        List<Account> accounts = FirebaseAccountQuery.getAccountsByUserId(firebaseFirestore, userId);

        PushNotification pushNotification = new PushNotification(this.context);

        Date dateMinusMonths = Date.from(LocalDate.now().minusMonths(6).atStartOfDay().toInstant(ZoneOffset.UTC));

        for (Account account: accounts) {
            if(account.getLastUpdate().toDate().compareTo(dateMinusMonths) < 0) {
                pushNotification.sendNotification(account.getAccountName());
            }
        }

        return Result.success();
    }

    public String getUserId() {

        String result = null;
        final String filename = "user_properties.txt";
        FileInputStream fis = null;

        try {
            fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    if(line.contains("userId=")) {
                        result = line.substring(line.indexOf("=") + 1);
                        break;
                    }
                    else {
                        line = reader.readLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
