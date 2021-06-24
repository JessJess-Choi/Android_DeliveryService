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

// work(work_id, user_id1, title, contents, user_id2)
// https://firebase.google.com/docs/database/android/read-and-write?hl=ko
@IgnoreExtraProperties
public class Work
{

	public String title;
	public String category;
	public String contents;
	public String user1;
	public String user2;
	public String cost;
	public double latitude;
	public double longitude;
	public int iWorkNum ;

	Work ( DBHelper dbHelper )
	{
		//dbHerper 를 통해서 가져오
	}

	public Work ( String title , String category , String contents , String user1 , String user2 , Integer cost , String latitude , String longitude , int worknum )
	{
		this.title = title;
		this.category = category;
		this.contents = contents;
		this.user1 = user1;
		this.user2 = user2;
		this.cost = String.valueOf ( cost );
		this.latitude = Double.parseDouble ( latitude );
		this.longitude = Double.parseDouble ( longitude );
		iWorkNum = worknum ;
	}
}