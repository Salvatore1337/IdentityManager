package com.identitymanager.database.firestore.callbacks;

import com.identitymanager.models.data.Account;

public interface GetAccountCallback {

    void onCallback(Account account);
}
