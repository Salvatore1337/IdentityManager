package com.identitymanager.database.firestore.queries;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.models.data.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseAccountQuery extends Fragment {

    static final String ACCOUNTS_COLLECTION_PATH = "accounts";

    public static void createAccount(FirebaseFirestore db, Map<String, Object> accountToInsert, Context context, String userId) {


        db.collection(ACCOUNTS_COLLECTION_PATH)
                .whereEqualTo("fkIdUser", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().get("accountName").equals(accountToInsert.get("accountName"))) {
                                if (Locale.getDefault().getLanguage().equals("it")) {
                                    Toast.makeText(context, "Nome dell'account già esistente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Name account already exists", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                        }

                        // Add a new document with a generated ID
                        db.collection(ACCOUNTS_COLLECTION_PATH)
                                .add(accountToInsert)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("SAVE", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    if (Locale.getDefault().getLanguage().equals("it")) {
                                        Toast.makeText(context, "Account aggiunto", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Account added", Toast.LENGTH_SHORT).show();
                                    }

                                    String docId = documentReference.getId();
                                    db.collection(ACCOUNTS_COLLECTION_PATH).document(documentReference.getId()).update("docId", docId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("SAVE", "Error adding document", e);
                                    if (Locale.getDefault().getLanguage().equals("it")) {
                                        Toast.makeText(context, "Errore nel creare l'account", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Unable to create account", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.w("SAVE", "Error getting documents", task.getException());
                    }
                });
    }

    public static List<Account> getAccountsByUserId(FirebaseFirestore db, String userId) {

        List<Account> accounts = new ArrayList<>();


        db.collection(ACCOUNTS_COLLECTION_PATH)
            .whereEqualTo("fkIdUser", userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        accounts.add(document.toObject(Account.class));
                    }
                }
            });

        return accounts;
    }
}


