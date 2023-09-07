package com.identitymanager.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.R;
import com.identitymanager.adapters.RecyclerAdapter;
import com.identitymanager.models.data.Account;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    RecyclerAdapter.RecyclerViewClickListener listener;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Account> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View settView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Gets the user ID
        Bundle bundle = getActivity().getIntent().getExtras();
        String idUserLoggedIn = bundle.getString("userDocumentId");

        recyclerView = settView.findViewById(R.id.accounts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<Account>();

        setOnClickListener();
        recyclerAdapter = new RecyclerAdapter(getContext(), list, listener);
        recyclerView.setAdapter(recyclerAdapter);

        EventChangeListener(idUserLoggedIn);
        clickButton(settView);

        return settView;
    }

    private void setOnClickListener() {
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Fragment fragment = new AccountDetailsFragment();

                getActivity().getIntent().putExtra("id", list.get(position).getFkIdUser());
                getActivity().getIntent().putExtra("accountName", list.get(position).getAccountName());
                getActivity().getIntent().putExtra("email", list.get(position).getEmail());
                getActivity().getIntent().putExtra("category", list.get(position).getcategory());
                getActivity().getIntent().putExtra("username", list.get(position).getUsername());
                getActivity().getIntent().putExtra("password", list.get(position).getPassword());
                getActivity().getIntent().putExtra("passwordStrength", list.get(position).getPasswordStrength());
                getActivity().getIntent().putExtra("authentication", list.get(position).getTwoFactorAuthentication());

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
    }

    private void clickButton(View settView) {
        FloatingActionButton fab;
        fab = settView.findViewById(R.id.add_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewAccountFragment();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    private void EventChangeListener(String idUserLoggedIn) {

        // Gets all account details
        db.collection("accounts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Account account = document.toObject(Account.class);
                                if ((account.getFkIdUser().equals(idUserLoggedIn))) {
                                    list.add(account);
                                    recyclerAdapter.notifyDataSetChanged();
                                }
                                Log.d("QUERY OK", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("QUERY", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}