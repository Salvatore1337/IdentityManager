package com.identitymanager.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Pie;

import com.identitymanager.models.data.Account;
import com.identitymanager.utilities.language.LanguageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int strong = 0, medium = 0, weak = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View settView = inflater.inflate(R.layout.fragment_statistics, container, false);

        TextView passwordSecurityLevel = settView.findViewById(R.id.password_security_level);
        TextView weakPasswords = settView.findViewById(R.id.weak_passwords);
        TextView changePasswords = settView.findViewById(R.id.change_passwords);

        SharedPreferences sharedLanguage = getActivity().getSharedPreferences("language", 0);
        int refresh = sharedLanguage.getInt("sP", 0);

        // Checks language
        if (refresh == 2) {
            LanguageManager lang = new LanguageManager(getContext());
            lang.updateResources("it");
            passwordSecurityLevel.setText("Livello di sicurezza password");
            weakPasswords.setText("Password deboli");
            changePasswords.setText("Password consigliate da cambiare");
        } else {
            passwordSecurityLevel.setText("Password security level");
            weakPasswords.setText("Weak passwords");
            changePasswords.setText("Recommended passwords to change");
        }

        setupPieChart(settView, refresh);
        setupWeakList(settView, refresh);
        setupToChangeList(settView, refresh);

        return settView;
    }

    // Shows pie chart with password strength percentage of all user accounts
    private void setupPieChart(View settView, int refresh) {
        AnyChartView anyChartView = settView.findViewById(R.id.any_chart_view);

        // Gets the user ID
        Bundle bundle = getActivity().getIntent().getExtras();
        String idUserLoggedIn = bundle.getString("userDocumentId");

        // Gets password strength percentage of all user accounts
        db.collection("accounts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Account account = document.toObject(Account.class);
                                if ((account.getFkIdUser().equals(idUserLoggedIn)) && account.getPasswordStrength().equals("STRONG")) {
                                    strong++;
                                }
                                if ((account.getFkIdUser().equals(idUserLoggedIn)) && account.getPasswordStrength().equals("MEDIUM")) {
                                    medium++;
                                }
                                if ((account.getFkIdUser().equals(idUserLoggedIn)) && account.getPasswordStrength().equals("WEAK")) {
                                    weak++;
                                }
                            }

                            String[] levels;
                            int[] totals = {strong, medium, weak};
                            Pie pie = AnyChart.pie();
                            List<DataEntry> dataEntries = new ArrayList<>();
                            if (refresh == 2) {
                                levels = new String[]{"Forte", "Media", "Debole"};

                                for (int i = 0; i < levels.length; i++){
                                    dataEntries.add(new ValueDataEntry(levels[i], totals[i]));
                                }

                            } else {
                                levels = new String[]{"Strong", "Medium", "Weak"};

                                for (int i = 0; i < levels.length; i++){
                                    dataEntries.add(new ValueDataEntry(levels[i], totals[i]));
                                }

                            }
                            pie.data(dataEntries);
                            anyChartView.setChart(pie);
                        }
                    }
                });
    }

    // Shows user accounts with weak passwords
    private void setupWeakList(View settView, int refresh) {
        ListView listView = settView.findViewById(R.id.category_weak_passwords);

        ArrayList<String> arrayList = new ArrayList<>();

        // Gets the user ID
        Bundle bundle = getActivity().getIntent().getExtras();
        String idUserLoggedIn = bundle.getString("userDocumentId");

        // Gets user accounts with weak passwords
        db.collection("accounts")
                .get()
                .addOnCompleteListener(operation -> {
                    if (operation.isSuccessful()) {
                        for (QueryDocumentSnapshot document : operation.getResult()) {
                            Account account = document.toObject(Account.class);
                            if (account.getFkIdUser().equals(idUserLoggedIn) && account.getPasswordStrength().equals("WEAK")) {
                                arrayList.add(account.getAccountName());

                                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
                                listView.setAdapter(arrayAdapter);

                                // Redirects user to the selected account details
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                        db.collection("accounts")
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            if (documentSnapshot.getData().get("fkIdUser").equals(idUserLoggedIn) && documentSnapshot.getData().get("accountName").equals(adapterView.getItemAtPosition(position))) {

                                                                getActivity().getIntent().putExtra("id", documentSnapshot.getData().get("fkIdUser").toString());
                                                                getActivity().getIntent().putExtra("accountName", documentSnapshot.getData().get("accountName").toString());
                                                                getActivity().getIntent().putExtra("email", documentSnapshot.getData().get("email").toString());
                                                                getActivity().getIntent().putExtra("category", documentSnapshot.getData().get("category").toString());
                                                                getActivity().getIntent().putExtra("username", documentSnapshot.getData().get("username").toString());
                                                                getActivity().getIntent().putExtra("password", documentSnapshot.getData().get("password").toString());
                                                                getActivity().getIntent().putExtra("passwordStrength", documentSnapshot.getData().get("passwordStrength").toString());
                                                                getActivity().getIntent().putExtra("authentication", documentSnapshot.getData().get("twoFactorAuthentication").toString());

                                                                Fragment fragment = new AccountDetailsFragment();
                                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                        // Informs user that no account has a weak password
                        if (arrayList.isEmpty()) {
                            if (refresh == 2) {
                                arrayList.add("Nessun account ha una password debole");
                            } else {
                                arrayList.add("No account has a weak password");
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(arrayAdapter);
                        }
                    }
                });
    }

    // Shows user accounts that need to change password
    private void setupToChangeList(View settView, int refresh) {
        ListView listView = settView.findViewById(R.id.category_to_change_passwords);

        ArrayList<String> arrayList = new ArrayList<>();

        // Gets the user ID
        Bundle bundle = getActivity().getIntent().getExtras();
        String idUserLoggedIn = bundle.getString("userDocumentId");

        // Gets user accounts that need to change password
        db.collection("accounts")
                .get()
                .addOnCompleteListener(operation -> {
                    if (operation.isSuccessful()) {
                        for (QueryDocumentSnapshot document : operation.getResult()) {
                            Account account = document.toObject(Account.class);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(account.getLastUpdate().toDate());
                            int oldMonth = calendar.get(Calendar.MONTH);
                            calendar.setTime(new Date());
                            int newMonth = calendar.get(Calendar.MONTH);
                            int differenceMonth = newMonth - oldMonth;

                            if (account.getFkIdUser().equals(idUserLoggedIn) && differenceMonth >= 6) {
                                arrayList.add(account.getAccountName());

                                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
                                listView.setAdapter(arrayAdapter);

                                // Redirects user to the selected account details
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                        db.collection("accounts")
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            if (documentSnapshot.getData().get("fkIdUser").equals(idUserLoggedIn) && documentSnapshot.getData().get("accountName").equals(adapterView.getItemAtPosition(position))) {

                                                                getActivity().getIntent().putExtra("id", documentSnapshot.getData().get("fkIdUser").toString());
                                                                getActivity().getIntent().putExtra("accountName", documentSnapshot.getData().get("accountName").toString());
                                                                getActivity().getIntent().putExtra("email", documentSnapshot.getData().get("email").toString());
                                                                getActivity().getIntent().putExtra("category", documentSnapshot.getData().get("category").toString());
                                                                getActivity().getIntent().putExtra("username", documentSnapshot.getData().get("username").toString());
                                                                getActivity().getIntent().putExtra("password", documentSnapshot.getData().get("password").toString());
                                                                getActivity().getIntent().putExtra("passwordStrength", documentSnapshot.getData().get("passwordStrength").toString());
                                                                getActivity().getIntent().putExtra("authentication", documentSnapshot.getData().get("twoFactorAuthentication").toString());

                                                                Fragment fragment = new AccountDetailsFragment();
                                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                        // Informs user that no account needs password change
                        if (arrayList.isEmpty()) {
                            if (refresh == 2) {
                                arrayList.add("Nessun account ha bisogno del cambio password");
                            } else {
                                arrayList.add("No account needs password change");
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(arrayAdapter);
                        }
                    }
                });
    }
}