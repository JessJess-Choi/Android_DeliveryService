package com.example.projectdelivery;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    public String sender;
    public String msg;

    public Chat() {}

    public Chat(String sender, String msg) {
        this.sender = sender;
        this.msg = msg;

    }
    public String getUserName() { return sender;}


}