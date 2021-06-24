package com.example.projectdelivery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 2;
	
	public DBHelper ( Context context )
	{
		super ( context , "UserData" , null , DATABASE_VERSION );
	}
	
	@Override
	public void onCreate ( SQLiteDatabase db )
	{
		String userSql = "create table tb_user (" + "id, " + "password, " + "age, " + "sex, " + "address, " + "name)";
		String postingSql = "create table tb_posting (" + "category, " + "contents, " + "cost, " + "latitude, " + "longitude, " + "title, " + "user1, " + "user2)";
		db.execSQL ( userSql );
		db.execSQL ( postingSql );
	}
	
	@Override
	public void onUpgrade ( SQLiteDatabase db , int oldVersion , int newVersion )
	{
		if ( newVersion == DATABASE_VERSION )
		{
			db.execSQL ( "drop table tb_user" );
			db.execSQL ( "drop table tb_posting" );
			onCreate ( db );
		}
	}
}