package com.identitymanager.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.identitymanager.activities.MainActivity;
import com.identitymanager.utilities.language.LanguageManager;
import com.identitymanager.R;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorRestart;
    SharedPreferences.Editor editorMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View settView = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences sharedLanguage = getActivity().getSharedPreferences("language", 0);
        int refresh = sharedLanguage.getInt("sP", 0);

        // Checks language
        if (refresh == 2 && Locale.getDefault().getLanguage().equals("en")) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        }

        changeLanguage(settView);
        darkMode(settView);

        return settView;
    }

    // Sets language selected by the user
    public void changeLanguage(View settView) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("language", 0);
        int sP = sharedPreferences.getInt("sP", 1);
        editor = sharedPreferences.edit();

        SharedPreferences sharedRestart = getActivity().getSharedPreferences("choose", 0);
        int count = sharedRestart.getInt("count", 0);
        editorRestart = sharedRestart.edit();

        RadioGroup radioGroup = settView.findViewById(R.id.radio_group);
        RadioButton enRb = settView.findViewById(R.id.radio_button1);
        RadioButton itRb = settView.findViewById(R.id.radio_button2);
        LanguageManager lang = new LanguageManager(getContext());

        // Sets default language to english
        if (count == 0) {
            enRb.setChecked(true);
        }

        saveLanguagePreferences(sP, count, enRb, itRb, lang);

        // Sets language by checking the chosen option
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // English case
                if (i == R.id.radio_button1) {
                    lang.updateResources("en");
                    editor.putInt("sP", 1);
                    ((MainActivity) getActivity()).setChangeLanguageEnglish();
                }
                // Italian case
                else {
                    lang.updateResources("it");
                    editor.putInt("sP", 2);
                    ((MainActivity) getActivity()).setChangeLanguageItalian();
                }
                editor.commit();

                editorRestart.putInt("count", 1);
                editorRestart.commit();

                remainInSettings();
            }
        });
    }

    // Saves language chosen
    public void saveLanguagePreferences(int sP, int count, RadioButton enRb, RadioButton itRb, LanguageManager lang) {
        // English case
        if (sP == 1 && count == 1) {
            enRb.setChecked(true);
            lang.updateResources("en");
            ((MainActivity) getActivity()).setChangeLanguageEnglish();

            editorRestart.putInt("count", 1);
            editorRestart.commit();
        }
        // Italian case
        else if (sP == 2 && count == 1) {
            itRb.setChecked(true);
            lang.updateResources("it");
            ((MainActivity) getActivity()).setChangeLanguageItalian();

            editorRestart.putInt("count", 1);
            editorRestart.commit();
        }
    }

    // Changes app theme if selected
    public void darkMode(View settView) {
        SharedPreferences sharedMode = getActivity().getSharedPreferences("mode", 0);
        editorMode = sharedMode.edit();

        int check = sharedMode.getInt("theme", 0);

        SwitchCompat aSwitch = settView.findViewById(R.id.s1);

        // If dark mode is already enabled the switch is checked
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || check == 2) {
            aSwitch.setChecked(true);
        }

        // Sets app theme by checking the corresponding switch
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Enables dark mode
                if (aSwitch.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editorMode.putInt("theme", 2);
                }
                // Disables dark mode
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editorMode.putInt("theme", 1);
                }
                editorMode.commit();

                remainInSettingsCoordinationLanguage();
            }
        });
    }

    // Remains in settings
    public void remainInSettings() {
        getActivity().getIntent().putExtra("fragment", 4);
        getActivity().getIntent().putExtra("change_value", 4);
        getActivity().recreate();
    }

    // Remains in settings and controls language and theme selected by the user
    public void remainInSettingsCoordinationLanguage() {
        getActivity().getIntent().putExtra("fragment", 4);
        getActivity().getIntent().putExtra("change_value", 0);
        getActivity().recreate();
    }
}