package com.identitymanager.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.utilities.security.AES;

import java.util.Locale;

public class AccountDetailsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View settView = inflater.inflate(R.layout.fragment_account_details, container, false);

        String id, accountNameDetails, categoryDetails, emailDetails, usernameDetails, passwordDetails, passwordStrengthDetails;

        TextView accountName, category, email, username, password, passwordStrength;

        accountName = settView.findViewById(R.id.account_name_details);
        email = settView.findViewById(R.id.account_email_details);
        category = settView.findViewById(R.id.account_category_details);
        username = settView.findViewById(R.id.account_username_details);
        password = settView.findViewById(R.id.account_password_details);
        passwordStrength = settView.findViewById(R.id.account_password_strength_details);

        // Gets data account
        Bundle bundle = getActivity().getIntent().getExtras();
        id =  bundle.getString("id");
        accountNameDetails = bundle.getString("accountName");
        categoryDetails = bundle.getString("category");
        emailDetails = bundle.getString("email");
        usernameDetails = bundle.getString("username");
        passwordDetails = bundle.getString("password");
        passwordStrengthDetails = bundle.getString("passwordStrength");

        // Sets data account
        accountName.setText(accountNameDetails);
        category.setText(categoryDetails);
        email.setText(emailDetails);
        username.setText(usernameDetails);
        password.setText(AES.decrypt(passwordDetails, id));
        passwordStrength.setText(passwordStrengthDetails);

        deleteAccount(settView, accountNameDetails);
        modifyAccount(settView);

        return settView;
    }

    public void deleteAccount(View settView, String accountNameDetails) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button delete = settView.findViewById(R.id.button_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new DashboardFragment();
                deleteProcess(db, fragment, accountNameDetails);
            }
        });
    }

    public void modifyAccount(View settView) {
        Button modify = settView.findViewById(R.id.button_modify);

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ModifyAccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    public void deleteProcess(FirebaseFirestore db, Fragment fragment, String accountNameDetails) {

        db.collection("accounts")
                .whereEqualTo("accountName", accountNameDetails)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("accounts").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("DELETE OK", "DocumentSnapshot successfully deleted!");
                                            if (Locale.getDefault().getLanguage().equals("it")) {
                                                Toast.makeText(getContext(), "Account eliminato", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                            }
                                            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                                            bottomNav.setSelectedItemId(R.id.nav_dashboard);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("DELETE FAILURE", "Error deleting document");
                                            if (Locale.getDefault().getLanguage().equals("it")) {
                                                Toast.makeText(getActivity(), "Errore nell'eliminare l'account", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Unable to delete account", Toast.LENGTH_SHORT).show();
                                            }
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                        }
                                    });
                        }
                    }
                });
    }
}