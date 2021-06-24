package com.example.projectdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class project_11_registerwork extends AppCompatActivity implements View.OnClickListener
{
	Button m_btn_Back;
	Button m_btn_Register;
	RadioGroup m_group_Radio1;
	RadioGroup m_group_Radio2;
	RadioButton m_radio_btn_Deliver ;
	RadioButton m_radio_btn_Food ;
	RadioButton m_radio_btn_Home ;
	RadioButton m_radio_btn_ETC ;
	EditText m_edit_Name;
	EditText m_edit_Value;
	EditText m_edit_Content;
	LatLng m_latLng;
	int m_iValue;
	String work_type = "";
	String m_strUserName ;

	int countFireBaseMember;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_11_registerwork );

		Log.d ( "CHECK DELIVERY" , "새로운 일을 등록하는 화면을 그립니다." );

		try
		{
			File file = new File ( getFilesDir () , "user_id.txt" );
			BufferedReader reader = new BufferedReader ( new FileReader ( file ) );
			StringBuffer buffer = new StringBuffer ();
			String line;
			while ( ( line = reader.readLine () ) != null )
			{
				buffer.append ( line );
			}
			m_strUserName = buffer.toString ();
			reader.close ();
		} catch ( Exception e )
		{
			e.printStackTrace ();
		}

		Intent intent = getIntent ();
		countFireBaseMember = intent.getIntExtra ( "firebaseSize" , 0 );

		m_btn_Back = ( Button ) findViewById ( R.id.registerwork_Button_Back );
		m_btn_Register = ( Button ) findViewById ( R.id.registerwork_Button_Register );

		m_group_Radio1 = ( RadioGroup ) findViewById ( R.id.registerwork_Group_Radio1 );
		m_group_Radio2 = ( RadioGroup ) findViewById ( R.id.registerwork_Group_Radio2 );

		m_radio_btn_Deliver = ( RadioButton ) findViewById ( R.id.registerwork_Radio_Deliver ) ;
		m_radio_btn_Food = ( RadioButton ) findViewById ( R.id.registerwork_Radio_Food ) ;
		m_radio_btn_Home = ( RadioButton ) findViewById ( R.id.registerwork_Radio_Home ) ;
		m_radio_btn_ETC = ( RadioButton ) findViewById ( R.id.registerwork_Radio_ETC ) ;

		m_edit_Name = ( EditText ) findViewById ( R.id.registerwork_Edit_Name );
		m_edit_Value = ( EditText ) findViewById ( R.id.registerwork_Edit_Value );
		m_edit_Content = ( EditText ) findViewById ( R.id.registerwork_Edit_Content );

		m_group_Radio1.clearCheck ();
		m_group_Radio1.setOnCheckedChangeListener ( listener1 );
		m_group_Radio2.clearCheck ();
		m_group_Radio2.setOnCheckedChangeListener ( listener2 );

		m_radio_btn_Deliver.setOnClickListener ( this ) ;
		m_radio_btn_Food.setOnClickListener ( this ) ;
		m_radio_btn_Home.setOnClickListener ( this ) ;
		m_radio_btn_ETC.setOnClickListener ( this ) ;

		m_edit_Value.addTextChangedListener ( new TextWatcher ()
		{
			@Override
			public void beforeTextChanged ( CharSequence s , int start , int count , int after )
			{

			}

			@Override
			public void onTextChanged ( CharSequence s , int start , int before , int count )
			{

			}

			@Override
			public void afterTextChanged ( Editable s )
			{
				m_iValue = Integer.parseInt ( m_edit_Value.getText ().toString () );            // 그거 처리 귀찮으니까 미리 숫자는 따로 저장하기
				// 숫자 4000이 들어왔다고 칩시다
				// 그거를 4,000원 이라고 표시해야함.
			}
		} );

		m_btn_Back.setOnClickListener ( this );
		m_btn_Register.setOnClickListener ( this );
	}

	private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener ()
	{
		@Override
		public void onCheckedChanged ( RadioGroup group , int checkedId )
		{
			if ( - 1 != checkedId )
			{
				m_group_Radio2.setOnCheckedChangeListener ( null );
				m_group_Radio2.clearCheck ();
				m_group_Radio2.setOnCheckedChangeListener ( listener2 );
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener ()
	{
		@Override
		public void onCheckedChanged ( RadioGroup group , int checkedId )
		{
			if ( - 1 != checkedId )
			{
				m_group_Radio1.setOnCheckedChangeListener ( null );
				m_group_Radio1.clearCheck ();
				m_group_Radio1.setOnCheckedChangeListener ( listener1 );
			}
		}
	};

	@Override
	public void onClick ( View view )
	{
		switch ( view.getId () )
		{
			case R.id.registerwork_Radio_Deliver:
			{
				work_type = "CHECK DELIVERY";
				Log.d ( "CHECK DELIVERY" , "배달 카테고리를 선택하셨습니다." );

				break;
			}
			case R.id.registerwork_Radio_Food:
			{
				work_type = "FOOD";
				Log.d ( "CHECK DELIVERY" , "음식 카테고리를 선택하셨습니다." );

				break;
			}
			case R.id.registerwork_Radio_Home:
			{
				work_type = "HOME";
				Log.d ( "CHECK DELIVERY" , "집안일 카테고리를 선택하셨습니다." );

				break;
			}
			case R.id.registerwork_Radio_ETC:
			{
				work_type = "ETC";
				Log.d ( "CHECK DELIVERY" , "기타 카테고리를 선택하셨습니다." );

				break;
			}
			case R.id.registerwork_Button_Back:
			{
				Log.d ( "CHECK DELIVERY" , "뒤로가기 버튼을 누르셨습니다. 일을 등록하지 않습니다." );

				finish ();
				break;
			}
			case R.id.registerwork_Button_Register:
			{
				if ( work_type == "" || m_edit_Name.getText ().toString () == "" || m_edit_Content.getText ().toString () == "" || m_edit_Value.getText ().toString () == "" )
				{
					Log.d ( "CHECK DELIVERY" , "일부 정보가 입력되지 않았습니다." );

					showToast ( "정보를 모두 입력해주세요." );
				}
				else
				{
					Intent intent = new Intent ( getApplicationContext () , MapFragmentActivity.class );
					// 버튼 누르면 위치 확인하고 바로 전달
					// 정보 다 들어왔으니까 그거 서버에 전달
					Log.d ( "CHECK DELIVERY" , "새로운 일을 등록합니다." );

					startActivityForResult ( intent , 17 );
					break;
				}
			}
		}
	}

	public void showToast ( String string ) {Toast.makeText ( this , string , Toast.LENGTH_SHORT ).show ();}

	@Override
	public void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data )
	{
		if ( 17 == requestCode && RESULT_OK == resultCode )
		{
			m_latLng = data.getParcelableExtra ( "Latlng" );

			Log.d ( "CHECK DELIVERY" , "일을 등록하는 위치를 받아왔습니다." );

			FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
			SimpleDateFormat cur_time = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
			Date cur = new Date ();
			String registered_time = cur_time.format ( cur );



			//얘는 왜 또 위에서만 그러냐...
			DatabaseReference reference = firebaseDatabase.getReference ( "works" ).child ( String.valueOf (registered_time) );
			reference.child ( "category" ).setValue ( work_type );
			reference.child ( "contents" ).setValue ( m_edit_Content.getText ().toString () );
			reference.child ( "cost" ).setValue ( m_edit_Value.getEditableText ().toString () );
			reference.child ( "title" ).setValue ( m_edit_Name.getEditableText ().toString () );
			reference.child ( "user1_id" ).setValue ( m_strUserName );
			reference.child ( "user2_id" ).setValue ( " " );                  // 매칭이 안 되었으니 공란으로 저장
			reference.child ( "latitude" ).setValue ( String.valueOf ( m_latLng.latitude ) );
			reference.child ( "longitude" ).setValue ( String.valueOf ( m_latLng.longitude ) );

			DBHelper dbHelper = new DBHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
		//	db.delete("tb_posting","category=?,contest=?,cost=?,latitude=?,longitude=?,title=?,user1=?",new String[]{work_type,m_edit_Content.getText ().toString (),m_edit_Value.getEditableText ().toString (),String.valueOf ( m_latLng.latitude ),String.valueOf ( m_latLng.longitude ),m_edit_Name.getEditableText ().toString (),m_strUserName});
			db.execSQL("insert into tb_posting (category, contents, cost, latitude, longitude, title, user1, user2) values (?,?,?,?,?,?,?,?)",new String[]{work_type,m_edit_Content.getText ().toString (),m_edit_Value.getEditableText ().toString (),String.valueOf ( m_latLng.latitude ),String.valueOf ( m_latLng.longitude ),m_edit_Name.getEditableText ().toString (),m_strUserName," "});
			db.close();
			// 버튼 누르면 위치 확인하고 바로 전달
			// 정보 다 들어왔으니까 그거 서버에 전달

			super.onActivityResult ( requestCode , resultCode , data );

			Intent intent = new Intent ();

			intent.putExtra ( "workTitle" , m_edit_Name.getText ().toString () );                  // 여기에 일 번호 넣어주고 싶습니다...
			setResult ( RESULT_OK , intent );
			finish ();
		}
	}
}