package com.identitymanager.database.firestore.callbacks;

import com.identitymanager.models.data.Account;

import java.util.List;

public interface GetAccountsCallback {
    void onCallback(List<Account> accounts);
}
