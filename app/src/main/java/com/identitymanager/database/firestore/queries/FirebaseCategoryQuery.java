package com.identitymanager.database.firestore.queries;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.identitymanager.models.data.Category;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCategoryQuery {

    static final String CATEGORIES_COLLECTION_PATH = "categories";
    static Category category;

    public static List<String> getAllCategories(FirebaseFirestore db) {

        List<String> categoryList = new ArrayList<>();

        db.collection(CATEGORIES_COLLECTION_PATH) //get all the categories
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                category = document.toObject(Category.class);
                                categoryList.add(category.getLabel());
                                Log.d("QUERY OK", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("QUERY", "Error getting documents: ", task.getException());
                        }
                    }
                });
        
        return categoryList;
    }
}