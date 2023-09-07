package com.identitymanager.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.models.data.User;
import com.identitymanager.utilities.language.LanguageManager;

public class UserDetailsViewFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settView = inflater.inflate(R.layout.fragment_user_details_view, container, false);

        // Checks language
        SharedPreferences sharedLanguage = getActivity().getSharedPreferences("language", 0);
        int refresh = sharedLanguage.getInt("sP", 0);
        if (refresh == 2) {
            LanguageManager lang = new LanguageManager(getContext());
            lang.updateResources("it");
        }

        // Gets the username
        Bundle bundle = getActivity().getIntent().getExtras();
        String username = bundle.getString("usernameUser");

        TextView nameText = settView.findViewById(R.id.name_text);
        TextView surnameText = settView.findViewById(R.id.surname_text);
        TextView usernameText = settView.findViewById(R.id.username_text);
        TextView emailText = settView.findViewById(R.id.email_text);
        TextView birthdayText = settView.findViewById(R.id.birthday_text);
        TextView countryText = settView.findViewById(R.id.country_text);
        TextView phoneText = settView.findViewById(R.id.phone_text);

        // Shows user data
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user.getUsername().equals(username)) {
                                nameText.setText(user.getName());
                                surnameText.setText(user.getSurname());
                                usernameText.setText(user.getUsername());
                                emailText.setText(user.getEmail());
                                birthdayText.setText(user.getBirthDate());
                                countryText.setText(user.getCountry());
                                phoneText.setText(user.getPhone());

                                getActivity().getIntent().putExtra("nameUser", user.getName());
                                getActivity().getIntent().putExtra("surnameUser", user.getSurname());
                                getActivity().getIntent().putExtra("usernameUser", user.getUsername());
                                getActivity().getIntent().putExtra("emailUser", user.getEmail());
                                getActivity().getIntent().putExtra("birthdayUser", user.getBirthDate());
                                getActivity().getIntent().putExtra("countryUser", user.getCountry());
                                getActivity().getIntent().putExtra("phoneUser", user.getPhone());
                            }
                        }
                    }
                });

        Button edit = settView.findViewById(R.id.button_edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UserDetailsEditFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        return settView;
    }
}