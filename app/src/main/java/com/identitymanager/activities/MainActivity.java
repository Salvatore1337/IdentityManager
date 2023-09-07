package com.identitymanager.activities;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.identitymanager.R;
import com.identitymanager.fragments.DashboardFragment;
import com.identitymanager.fragments.StatisticsFragment;
import com.identitymanager.fragments.SettingsFragment;
import com.identitymanager.fragments.UserDetailsViewFragment;
import com.identitymanager.utilities.language.LanguageManager;
import com.identitymanager.workers.NotificationWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SharedPreferences.Editor editorLanguage;
    SharedPreferences.Editor editorTheme;
    String idUserLoggedIn;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        startNotificationServiceViaWorker();

        // Gets the idFragment
        Bundle bundle = getIntent().getExtras();
        int idFragment = bundle.getInt("fragment");

        // Gets the user ID
        idUserLoggedIn = bundle.getString("userDocumentId");
        username = bundle.getString("username");

        // First start
        int idLoad = bundle.getInt("load", 0);

        // Gets the language
        SharedPreferences sharedLanguage = getSharedPreferences("language", 0);
        int refresh = sharedLanguage.getInt("sP", 0);
        editorLanguage = sharedLanguage.edit();

        // Gets the theme
        SharedPreferences sharedTheme = getSharedPreferences("mode", 0);
        int theme = sharedTheme.getInt("theme", 0);
        editorTheme = sharedTheme.edit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        Fragment fragment = null;
        View view = null;

        // Checks which item of BottomNavigationView is selected
        switch (idFragment) {
            // Case Statistics
            case 2:
                fragment = new StatisticsFragment();
                view = bottomNav.findViewById(R.id.nav_newAccount);
                view.performClick();
                break;
            // Case Profile
            case 3:
                fragment = new UserDetailsViewFragment();
                view = bottomNav.findViewById(R.id.nav_user_details);
                view.performClick();
                break;
            // Case Settings
            case 4:
                view = bottomNav.findViewById(R.id.nav_settings);
                view.performClick();
                break;
            // Case Dashboard
            default:
                fragment = new DashboardFragment();
                view = bottomNav.findViewById(R.id.nav_dashboard);
                view.performClick();

                // Loads theme chosen and refresh
                if (idLoad == 0) {
                    identifyModePreference(theme);
                    getIntent().putExtra("load", 1);
                    finish();
                    startActivity(getIntent());
                }
                // Loads language chosen and refresh
                else if (idLoad == 1) {
                    identifyLanguagePreference(refresh);
                    getIntent().putExtra("load", 2);
                    finish();
                    startActivity(getIntent());
                }

                break;
        }

        if (idFragment != 4) {
            getIntent().putExtra("userDocumentId", idUserLoggedIn);
            getIntent().putExtra("username", username);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    // User selects an item in the BottomNavigationView
    private  NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Bundle bundle = getIntent().getExtras();
            int idChange = bundle.getInt("change_value");
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                // Case Dashboard
                 case R.id.nav_dashboard:
                     selectedFragment = new DashboardFragment();
                     getIntent().putExtra("textCheck", 1);
                     break;
                // Case Statistics
                 case R.id.nav_newAccount:
                     selectedFragment = new StatisticsFragment();
                     break;
                // Case Profile
                 case R.id.nav_user_details:
                     selectedFragment = new UserDetailsViewFragment();
                     break;
                // Case Settings
                 case R.id.nav_settings:
                     selectedFragment = new SettingsFragment();
                     break;
            }

            if (idChange != 4) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            } else {
                getIntent().putExtra("change_value", 0);
                recreate();
            }

            restoreChangeValue();

            return true;
        }
    };

   @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(R.style.DarkTheme, true);
        return theme;
    }

    public void restoreChangeValue() {
        getIntent().putExtra("change_value", 0);
    }

    public void identifyLanguagePreference(int refresh) {
        if (refresh == 1) {
            LanguageManager lang = new LanguageManager(getBaseContext());
            lang.updateResources("en");
        } else if (refresh == 2) {
            LanguageManager lang = new LanguageManager(getBaseContext());
            lang.updateResources("it");
        }
    }

    public void setChangeLanguageEnglish() {
        LanguageManager lang = new LanguageManager(getBaseContext());
        lang.updateResources("en");
        editorLanguage.putInt("refresh", 1);
        editorLanguage.commit();
    }

    public void setChangeLanguageItalian() {
        LanguageManager lang = new LanguageManager(getBaseContext());
        lang.updateResources("it");
        editorLanguage.putInt("refresh", 2);
        editorLanguage.commit();
    }

    public void identifyModePreference(int theme) {
        if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_IdentityManager);
        } else if (theme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme);
        }
    }

    public void startNotificationServiceViaWorker() {
        String UNIQUE_WORK_NAME = "StartNotificationServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance();

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(NotificationWorker.class,1, TimeUnit.DAYS).build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }
}