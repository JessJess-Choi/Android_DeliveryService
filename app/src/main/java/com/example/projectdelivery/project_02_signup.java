package com.example.projectdelivery;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class project_02_signup extends AppCompatActivity implements View.OnClickListener
{
	Button m_btn_SignUp;
	Button m_btn_Age;
	RadioGroup m_group_Radio;
	RadioButton m_btn_Radio1;
	RadioButton m_btn_Radio2;
	boolean m_bMale;
	boolean sexCheck = true;

	EditText editTextId;
	EditText editTextPassword;
	EditText editTextName;

	String Id = "";
	String Password = "";
	String Name = "";
	String Age = "";

	ArrayList < String > firebaseIdCheckList;
	public static final String TAG = "MyTag";
	boolean checkDB = true;
	ArrayList < User > userData;
	int countFireBaseMember;

	FirebaseDatabase firebaseDatabase;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_02_signup );
		Intent intent = getIntent ();
		countFireBaseMember = intent.getIntExtra ( "firebaseSize" , 0 );


		m_btn_SignUp = ( Button ) findViewById ( R.id.signUp_Button_SignUp );
		m_btn_Age = ( Button ) findViewById ( R.id.signUp_Button_Age );
		m_group_Radio = ( RadioGroup ) findViewById ( R.id.singUp_Group_Radio );
		m_btn_Radio1 = ( RadioButton ) findViewById ( R.id.singUp_Button_Radio1 );
		m_btn_Radio2 = ( RadioButton ) findViewById ( R.id.singUp_Button_Radio2 );

		editTextId = ( EditText ) findViewById ( R.id.signUp_Edit_ID );
		editTextPassword = ( EditText ) findViewById ( R.id.signUp_Edit_PW );
		editTextName = ( EditText ) findViewById ( R.id.signUp_Edit_Name );


		m_btn_SignUp.setOnClickListener ( this );
		m_btn_Age.setOnClickListener ( this );
		m_group_Radio.setOnClickListener ( this );
		m_btn_Radio1.setOnClickListener ( this );
		m_btn_Radio2.setOnClickListener ( this );

		m_btn_Radio1.setOnClickListener ( new Button.OnClickListener ()
		{
			@Override
			public void onClick ( View v )
			{
				m_bMale = true;
				sexCheck = false;
			}
		} );
		m_btn_Radio2.setOnClickListener ( new Button.OnClickListener ()
		{
			@Override
			public void onClick ( View v )
			{
				m_bMale = false;
				sexCheck = false;
			}
		} );

		firebaseIdCheckList = new ArrayList < String > ();
		firebaseDatabase = FirebaseDatabase.getInstance ();
		DatabaseReference databaseReference = firebaseDatabase.getReference ( "users" );
		databaseReference.addListenerForSingleValueEvent ( new ValueEventListener ()
		{
			@Override
			public void onDataChange ( @NonNull DataSnapshot snapshot )
			{
				for ( DataSnapshot data : snapshot.getChildren () )
				{
					String string = data.child ( "user_id" ).getValue ( String.class );
					firebaseIdCheckList.add ( string );
				}
			}

			@Override
			public void onCancelled ( @NonNull DatabaseError error )
			{
				showToast ( "????????? ??????????????? ?????????????????????" );
			}
		} );


	}

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_SignUp )
		{
//         Check and do
			// ?????? ??? ??????????????? ???????????? ???????????? ?????????.
			// EditText findViewByID ?????? ??? ???????????? ????????? ????????? ?????????
			// ??? ?????? ??? ?????????
			Id = editTextId.getText ().toString ();
			Password = editTextPassword.getText ().toString ();
			Name = editTextName.getText ().toString ();

			if ( ( Id.equals ( "" ) ) || ( Password.equals ( "" ) ) || ( Name.equals ( "" ) ) || ( Age.equals ( "" ) ) || sexCheck )
			{
				showToast ( "????????? ?????? ???????????????" );
				Log.d ( "CHECK DELIVERY" , "?????? ????????? ????????? ?????? ???????????????." );
			}
			else
			{

				DBHelper helper = new DBHelper ( this );
				SQLiteDatabase db = helper.getWritableDatabase ();
				String Address = "0";
				Cursor cursor = db.rawQuery ( "select id, password from tb_user" , null );
				while ( cursor.moveToNext () )
				{
					String tmp = cursor.getString ( 0 );

					if ( tmp.equals ( Id ) )
					{
						checkDB = false;
						break;
					}
				}
				if ( checkDB )
				{
					db.execSQL ( "insert into tb_user (id, password, age, sex, address, name) values (?,?,?,?,?,?)" , new String[] { Id , Password , Age , m_bMale + "" , Address , Name } );
					FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();

					DatabaseReference reference = firebaseDatabase.getReference ( "users" ).child ( String.valueOf ( countFireBaseMember ) );
					reference.child ( "age" ).setValue ( Integer.parseInt ( Age ) );
					reference.child ( "location" ).setValue ( Address );
					reference.child("name").setValue(Name);
					reference.child ( "sex" ).setValue ( m_bMale );
					reference.child ( "user_id" ).setValue ( Id );
					reference.child ( "user_pw" ).setValue ( Password );

				}
				else
				{
					showToast ( "ID??? ????????????." );
					Log.d ( "CHECK DELIVERY" , "?????? ID??? ???????????????." );
				}
				db.close ();
				if ( checkDB )
				{
					Intent intent = new Intent ( getApplicationContext () , MainActivity.class );
					Log.d ( "CHECK DELIVERY" , "?????? ????????? ?????????????????????." );
					startActivity ( intent );
				}
			}

		}
		else if ( view == m_btn_Age )
		{
			CharSequence csYear = m_btn_Age.getText ();
			String strYear = csYear.toString ();
			int iYear = 2000;


			if ( ! strYear.equals ( "" ) )                // Integer
			{
				iYear = 2021 - Integer.parseInt ( strYear );
			}


			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener ()
			{
				@Override
				public void onDateSet ( DatePicker view , int year , int month , int dayOfMonth )
				{
					m_btn_Age.setText ( Integer.toString ( 2021 - year ) );
					Age = Integer.toString ( 2021 - year );
					Log.d ( "CHECK DELIVERY" , "Spinner ?????????????????????. Age : " + Age.toString () );
				}
			};
			yearPickerDialog pickerDialog = new yearPickerDialog ();
			pickerDialog.m_iYear = iYear;
			pickerDialog.setListener ( dateSetListener );
			pickerDialog.show ( getSupportFragmentManager () , "YearpICKDR" );
		}
	}

	public void showToast ( String text )
	{
		Toast.makeText ( this , text , Toast.LENGTH_SHORT ).show ();
	}

	RadioGroup.OnCheckedChangeListener rgChangedListener = new RadioGroup.OnCheckedChangeListener ()
	{
		@Override
		public void onCheckedChanged ( RadioGroup group , int checkedId )
		{    // ??? ??? ?????? ????????????, ?????? ???????????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? ????????? ???.
			if ( checkedId == R.id.singUp_Button_Radio1 )
			{
				m_bMale = true;
			}
			else if ( checkedId == R.id.singUp_Button_Radio2 )
			{
				m_bMale = false;
			}
		}
	};
}