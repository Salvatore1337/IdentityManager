package com.identitymanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.utilities.files.FileManager;
import com.identitymanager.utilities.security.Cryptography;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN = "login"; //for logs
    private String userId;
    private String username;

    public static final String USER_PROPERTIES_FILENAME = "user_properties.txt"; //for app-internal storage values

    //Biometric prompt
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                goToDashboardFragment();
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication canceled", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        File userFile = new File(this.getFilesDir(), this.USER_PROPERTIES_FILENAME);

        if(userFile.exists()) {
            this.userId = this.getUserId();
            biometricPrompt.authenticate(promptInfo);
        }
    }

    public void login(View view) {
        EditText login_username_value = findViewById(R.id.login_username_value);
        EditText login_password_value = findViewById(R.id.login_password_value);
        String login_username_value_text = login_username_value.getText().toString();
        String login_password_value_text = login_password_value.getText().toString();
        String hashedPassword = Cryptography.hashString(login_password_value_text);


        db.collection("users") //get all the users
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) { //check if the username and password exist into the db
                        if (Objects.equals(document.getData().get(USERNAME_KEY), login_username_value_text) && Objects.equals(document.getData().get(PASSWORD_KEY), hashedPassword)) {
                            this.userId = document.getId();
                            this.username = login_username_value_text;

                            String fileContent = "userId=" + document.getId();
                            File userFile = new File(this.getFilesDir(), USER_PROPERTIES_FILENAME);

                            if(userFile.exists()) {
                                FileManager.clearAppInternalStorageFile(USER_PROPERTIES_FILENAME);
                            } else {
                                FileManager.createAppInternalStorageFile(this.getFilesDir(), USER_PROPERTIES_FILENAME);
                            }

                            FileManager.writeToAppInternalStorageFile(USER_PROPERTIES_FILENAME, fileContent, this);

                            this.goToDashboardFragment();

                            return;
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "Error on login", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(LOGIN, "Error getting documents.", task.getException());
            }
        });
    }

    public void goToDashboardFragment() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("fragment", 1);
        switchActivityIntent.putExtra("userDocumentId", this.userId);
        switchActivityIntent.putExtra("usernameUser", this.username);
        startActivity(switchActivityIntent);
    }

    public void goToSignUpActivity(View view) {
        Intent switchActivityIntent = new Intent(this, SignUpActivity.class);
        startActivity(switchActivityIntent);
    }

    public String getUserId() {

        String result = null;
        final String filename = "user_properties.txt";
        FileInputStream fis = null;

        try {
            fis = getApplicationContext().openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);

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