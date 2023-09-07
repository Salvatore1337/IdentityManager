package com.identitymanager.database.firestore.callbacks;

import com.identitymanager.models.data.User;

import java.util.List;

public interface GetUsersCallback {
    void onCallback(List<User> users);
}
