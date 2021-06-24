package com.example.projectdelivery;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ChatRoom {
    private Integer chat_room_id;
    private Integer work_id;
    private Integer user1_id;
    private Integer user2_id;

    public ChatRoom(Integer chat_room_id, Integer work_id, Integer user1_id, Integer user2_id) {
        this.chat_room_id = chat_room_id;
        this.work_id = work_id;
        this.user1_id = user1_id;
        this.user2_id = user2_id;


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> chat_room = new HashMap<>();
        chat_room.put("chat_room_id", chat_room_id);
        chat_room.put("work_id", work_id);
        chat_room.put("user1_id", user1_id);
        chat_room.put("user2_id", user2_id);


      //  db.collection("ChatRoom")
      //          .add(user)
      //          .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
      //              @Override
      //              public void onSuccess(DocumentReference documentReference) {
      //                  Log.d(TAG, "DocumentSnapshot added with ID: "+ documentReference.getId());
      //              }
      //          })
      //          .addOnFailureListener(new OnFailureListener() {
      //              @Override
      //              public void onFailure(@NonNull Exception e) {
      //                  Log.w(TAG, "Error adding document", e);
      //              }
      //          });


    }
}
