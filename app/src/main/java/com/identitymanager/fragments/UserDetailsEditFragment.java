package com.identitymanager.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.models.data.User;
import com.identitymanager.utilities.language.LanguageManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class UserDetailsEditFragment extends Fragment {

    public static final String NAME_KEY = "name";
    public static final String SURNAME_KEY = "surname";
    public static final String USERNAME_KEY = "username";
    public static final String EMAIL_KEY = "email";
    public static final String BIRTH_DATE_KEY = "birthDate";
    public static final String COUNTRY_KEY = "country";
    public static final String PHONE_KEY = "phone";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settView = inflater.inflate(R.layout.fragment_user_details_edit, container, false);

        // Checks language
        SharedPreferences sharedLanguage = getActivity().getSharedPreferences("language", 0);
        int refresh = sharedLanguage.getInt("sP", 0);
        if (refresh == 2) {
            LanguageManager lang = new LanguageManager(getContext());
            lang.updateResources("it");
        }

        // Gets the old profile data
        Bundle bundle = getActivity().getIntent().getExtras();
        String oldName =  bundle.getString("nameUser");
        String oldSurname =  bundle.getString("surnameUser");
        String oldUsername =  bundle.getString("usernameUser");
        String oldEmail =  bundle.getString("emailUser");
        String oldBirthday =  bundle.getString("birthdayUser");
        String oldCountry =  bundle.getString("countryUser");
        String oldPhone =  bundle.getString("phoneUser");

        Map<String, Object> oldUserData = new HashMap<>();
        oldUserData.put(NAME_KEY, oldName);
        oldUserData.put(SURNAME_KEY, oldSurname);
        oldUserData.put(USERNAME_KEY, oldUsername);
        oldUserData.put(EMAIL_KEY, oldEmail);
        oldUserData.put(BIRTH_DATE_KEY, oldBirthday);
        oldUserData.put(COUNTRY_KEY, oldCountry);
        oldUserData.put(PHONE_KEY, oldPhone);

        EditText name = settView.findViewById(R.id.edit_text_name);
        EditText surname = settView.findViewById(R.id.edit_text_surname);
        EditText username = settView.findViewById(R.id.edit_text_username);
        EditText email = settView.findViewById(R.id.edit_text_email);
        EditText birthday = settView.findViewById(R.id.edit_text_birthday);
        EditText country = settView.findViewById(R.id.edit_text_country);
        EditText phone = settView.findViewById(R.id.edit_text_phone);
        Button save = settView.findViewById(R.id.button_save_change_user_details);
        Button goBack = settView.findViewById(R.id.button_go_back_user_details);

        // Shows the old user data
        name.setText(oldName, TextView.BufferType.EDITABLE);
        surname.setText(oldSurname, TextView.BufferType.EDITABLE);
        username.setText(oldUsername, TextView.BufferType.EDITABLE);
        email.setText(oldEmail, TextView.BufferType.EDITABLE);
        birthday.setText(oldBirthday, TextView.BufferType.EDITABLE);
        country.setText(oldCountry, TextView.BufferType.EDITABLE);
        phone.setText(oldPhone, TextView.BufferType.EDITABLE);

        setupName(name);
        setupSurname(surname);
        setupUsername(username);
        setupEmail(email);
        setupBirthday(birthday);
        setupCountry(country);
        setupPhone(phone);
        setupSave(oldUserData, name, surname, username, email, birthday, country, phone, save);
        setupGoBack(goBack);

        return settView;
    }

    // Gets name input
    private boolean setupName(EditText name) {
        String nameInput = name.getText().toString();

        if (!nameInput.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    // Gets surname input
    private boolean setupSurname(EditText surname) {
        String surnameInput = surname.getText().toString();

        if (!surnameInput.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    // Gets username input
    private boolean setupUsername(EditText username) {
        String usernameInput = username.getText().toString();

        if (!usernameInput.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    // Gets email input
    private boolean setupEmail(EditText email) {
        String emailInput = email.getText().toString();

        Pattern EMAIL_PATTERN = Pattern.compile("^" +
                "(?=.*[a-zA-Z])" +           //any letter
                "(?=.*[@])" +                //at least 1 use of @
                "(?=\\S+$)" +                //no white spaces
                ".{6,}" +                    //at least 6 characters
                "$");

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!EMAIL_PATTERN.matcher(s).matches()) {
                    if (Locale.getDefault().getLanguage().equals("it")) {
                        email.setError("Email non valida");
                    } else {
                        email.setError("Invalid email");
                    }
                }
            }
        });

        if (!emailInput.isEmpty() && EMAIL_PATTERN.matcher(emailInput).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Gets birthday input
    private boolean setupBirthday(EditText birthday) {
        String birthdayInput = birthday.getText().toString();

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener setListener;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                birthday.setText(date);
            }
        };

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        if (!birthdayInput.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    // Gets country input
    private boolean setupCountry(EditText country) {
        String countryInput = country.getText().toString();

        if (!countryInput.isEmpty() && countryInput.matches("[A-Za-z ]+")) {
            return true;
        } else {
            if (Locale.getDefault().getLanguage().equals("it")) {
                country.setError("Sono consentite solo lettere");
            } else {
                country.setError("Only letters allowed");
            }
            return false;
        }
    }

    // Gets phone input
    private boolean setupPhone(EditText phone) {
        String phoneInput = phone.getText().toString();

        if (!phoneInput.isEmpty() && phoneInput.matches("[0-9]+")) {
            return true;
        } else {
            if (Locale.getDefault().getLanguage().equals("it")) {
                phone.setError("Sono consentiti solo numeri");
            } else {
                phone.setError("Only numbers allowed");
            }
            return false;
        }
    }

    // Saves changes if input data is valid
    private void setupSave(Map<String, Object> oldUserData, EditText name, EditText surname, EditText username, EditText email, EditText birthday, EditText country, EditText phone, Button save) {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check == 1 -> no errors detected
                // Check == 0 -> errors detected
                int check = 1;

                if (!setupName(name)) {
                    check = 0;
                }
                if (!setupSurname(surname)) {
                    check = 0;
                }
                if (!setupUsername(username)) {
                    check = 0;
                }
                if (!setupEmail(email)) {
                    check = 0;
                }
                if (!setupBirthday(birthday)) {
                    check = 0;
                }
                if (!setupCountry(country)) {
                    check = 0;
                }
                if (!setupPhone(phone)) {
                    check = 0;
                }

                // Connection with the database
                if (check == 1) {

                    String name_text = name.getText().toString();
                    String surname_text = surname.getText().toString();
                    String username_text = username.getText().toString();
                    String email_text = email.getText().toString();
                    String birthday_text = birthday.getText().toString();
                    String country_text = country.getText().toString();
                    String phone_text = phone.getText().toString();

                    Map<String, Object> userToUpdate = new HashMap<>();
                    userToUpdate.put(NAME_KEY, name_text);
                    userToUpdate.put(SURNAME_KEY, surname_text);
                    userToUpdate.put(USERNAME_KEY, username_text);
                    userToUpdate.put(EMAIL_KEY, email_text);
                    userToUpdate.put(BIRTH_DATE_KEY, birthday_text);
                    userToUpdate.put(COUNTRY_KEY, country_text);
                    userToUpdate.put(PHONE_KEY, phone_text);

                    modifyUser(db, userToUpdate, getContext());

                } else {
                    if (Locale.getDefault().getLanguage().equals("it")) {
                        Toast.makeText(getContext(), "Errore, riempi i campi correttamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error, fill in the fields correctly", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private void modifyUser(FirebaseFirestore db, Map<String, Object> userToUpdate, Context context) {

                // Checks if the user already exists
                db.collection("users")
                        .get()
                        .addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   User user = document.toObject(User.class);
                                   if (user.getUsername().equals(userToUpdate.get(USERNAME_KEY))) {
                                       if (userToUpdate.get(USERNAME_KEY).equals(oldUserData.get(USERNAME_KEY))) {
                                           break;
                                       }
                                       if (Locale.getDefault().getLanguage().equals("it")) {
                                           Toast.makeText(context, "Nome utente già esistente", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show();
                                       }
                                       return;
                                   }
                               }

                               // Update the document with new data
                               db.collection("users")
                                       .get()
                                       .addOnCompleteListener(operation -> {
                                           if (operation.isSuccessful()) {
                                               for (QueryDocumentSnapshot document : operation.getResult()) {
                                                   if (document.getData().get(USERNAME_KEY).equals(oldUserData.get(USERNAME_KEY))) {
                                                       db.collection("users").document(document.getId()).update("name", userToUpdate.get(NAME_KEY),
                                                               "surname", userToUpdate.get(SURNAME_KEY),
                                                               "username", userToUpdate.get(USERNAME_KEY),
                                                               "email", userToUpdate.get(EMAIL_KEY),
                                                               "birthDate", userToUpdate.get(BIRTH_DATE_KEY),
                                                               "country", userToUpdate.get(COUNTRY_KEY),
                                                               "phone", userToUpdate.get(PHONE_KEY));

                                                       if (Locale.getDefault().getLanguage().equals("it")) {
                                                           Toast.makeText(context, "Profilo modificato", Toast.LENGTH_SHORT).show();
                                                       } else {
                                                           Toast.makeText(context, "Profile modified", Toast.LENGTH_SHORT).show();
                                                       }

                                                       Fragment fragment = new UserDetailsViewFragment();
                                                       getActivity().getIntent().putExtra("usernameUser",  userToUpdate.get(USERNAME_KEY).toString());
                                                       getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                                   }
                                               }
                                           }
                                           else {
                                               if (Locale.getDefault().getLanguage().equals("it")) {
                                                   Toast.makeText(context, "Errore nel modificare il profilo", Toast.LENGTH_SHORT).show();
                                               } else {
                                                   Toast.makeText(context, "Unable to modify profile", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });
                           }
                        });
            }
        });
    }

    private void setupGoBack(Button goBack) {
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UserDetailsViewFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }
}