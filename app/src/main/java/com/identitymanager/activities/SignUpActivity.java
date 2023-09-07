package com.identitymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.utilities.files.FileManager;
import com.identitymanager.utilities.security.Cryptography;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String BIRTH_DATE_KEY = "birthDate";
    public static final String EMAIL_KEY = "email";
    public static final String PHONE_KEY = "phone";
    public static final String COUNTRY_KEY = "country";
    public static final String NAME_KEY = "name";
    public static final String SURNAME_KEY = "surname";
    public static final String SIGN_UP = "sign_up"; //for logs

    public static final String USER_PROPERTIES_FILENAME = "user_properties.txt"; //for app-internal storage values

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void signup(View view) {

        EditText sign_up_username_value = findViewById(R.id.sign_up_username_value);
        EditText sign_up_password_value = findViewById(R.id.sign_up_password_value);
        EditText sign_up_confirm_password_value = findViewById(R.id.sign_up_confirm_password_value);
        String sign_up_username_value_text = sign_up_username_value.getText().toString();
        String sign_up_password_value_text = sign_up_password_value.getText().toString();
        String sign_up_confirm_password_value_text = sign_up_confirm_password_value.getText().toString();

        if (!sign_up_username_value_text.isEmpty() && !sign_up_password_value_text.isEmpty() && sign_up_password_value_text.equals(sign_up_confirm_password_value_text)) {

            db.collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) { //check if the username already exists
                        if (document.getData().get(USERNAME_KEY).equals(sign_up_username_value_text)) {
                            Toast.makeText(getApplicationContext(), "Username already exists ", Toast.LENGTH_SHORT).show();
                            this.goToLoginActivity();
                            return;
                        }
                    }

                    //If the user not exists, create him
                    Map<String, Object> user = new HashMap<>();
                    user.put(USERNAME_KEY, sign_up_username_value_text);
                    user.put(PASSWORD_KEY, Cryptography.hashString(sign_up_password_value_text));
                    user.put(EMAIL_KEY, "");
                    user.put(SURNAME_KEY, "");
                    user.put(NAME_KEY, "");
                    user.put(COUNTRY_KEY, "");
                    user.put(PHONE_KEY, "");
                    user.put(BIRTH_DATE_KEY, "");

                    // Add a new document with a generated ID
                    db.collection("users")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(SIGN_UP, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();

                        FileManager.createAppInternalStorageFile(this.getFilesDir(), this.USER_PROPERTIES_FILENAME);
                        String fileContent = "userId=" + documentReference.getId();
                        FileManager.writeToAppInternalStorageFile(this.USER_PROPERTIES_FILENAME, fileContent, this);

                        this.goToLoginActivity();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(SIGN_UP, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Unable to create user", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.w(SIGN_UP, "Error getting documents.", task.getException());
                }
            });
        } else if (sign_up_username_value_text.isEmpty() || sign_up_password_value_text.isEmpty() || sign_up_confirm_password_value_text.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
        } else if (!sign_up_password_value_text.equals(sign_up_confirm_password_value_text)) {
            Toast.makeText(getApplicationContext(), "Password and ConfirmPassword must be equals", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLoginActivity(View view) {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }

    public void goToLoginActivity() {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }
}
