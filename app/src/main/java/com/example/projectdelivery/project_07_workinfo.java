package com.example.projectdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class project_07_workinfo extends AppCompatActivity implements View.OnClickListener
{
	ImageView m_image_work;
	TextView m_text_Name;
	TextView m_text_Distance;
	TextView m_text_Value;
	TextView m_text_Content;
	int m_iWorkCode;
	boolean m_bApplied;
	Button m_btn_Apply;
	Button m_btn_Back;
	Button m_btn_Messenger;

	public void renderWork ()
	{
		DBHelper dbHelper = new DBHelper ( this );
		SQLiteDatabase dbWork = dbHelper.getReadableDatabase ();
		SQLiteDatabase dbUser = dbHelper.getReadableDatabase ();
		Cursor cursorUser = dbUser.rawQuery ( "select id, password, age, sex, address, name from tb_user" , null );
		Cursor cursorWork = dbWork.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );
		while ( cursorWork.moveToNext () )                                                                                // 해당 일이 구인이 되어있으면
		{
			if ( cursorWork.getPosition () == m_iWorkCode )                                                 // 일 정보 띄우기 위함
			{
				m_text_Name.setText ( cursorWork.getString ( 3 ) );
				m_text_Value.setText ( cursorWork.getString ( 2 ) + "원" );
				m_text_Content.setText ( cursorWork.getString ( 1 ) );
				m_text_Distance.setText ( Integer.toString ( getIntent ().getExtras ().getInt ( "Distance" ) ) + "m" );

				if ( cursorWork.getString ( 0 ).equals ( "CHECK DELIVERY" ) )
				{
					m_image_work.setImageResource ( R.drawable.ic_baseline_moped_24 );
				}
				else if ( cursorWork.getString ( 0 ).equals ( "FOOD" ) )
				{
					m_image_work.setImageResource ( R.drawable.ic_baseline_fastfood_24 );
				}
				else if ( cursorWork.getString ( 0 ).equals ( "HOME" ) )
				{
					m_image_work.setImageResource ( R.drawable.ic_baseline_home_24 );
				}
				else if ( cursorWork.getString ( 0 ).equals ( "ETC" ) )
				{
					m_image_work.setImageResource ( R.drawable.ic_baseline_help_outline_24 );
				}

				Log.d ( "CHECK DELIVERY" , "일 정보를 출력합니다." );

				break;
			}
		}                                // 일 내용


		if ( m_bApplied )                                                // Use Messenger
		{
			m_btn_Apply.setVisibility ( View.INVISIBLE );
			m_btn_Apply.setEnabled ( false );
			m_btn_Messenger.setVisibility ( View.VISIBLE );
			m_btn_Messenger.setEnabled ( true );

			Log.d ( "CHECK DELIVERY" , "신청한 일입니다." );
		}
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_07_workinfo );

		m_image_work = ( ImageView ) findViewById ( R.id.workinfo_Image );
		m_text_Name = ( TextView ) findViewById ( R.id.workinfo_WorkName );
		m_text_Distance = ( TextView ) findViewById ( R.id.workinfo_Distance );
		m_text_Value = ( TextView ) findViewById ( R.id.workinfo_Value );
		m_text_Content = ( TextView ) findViewById ( R.id.workinfo_WorkContent );
		m_iWorkCode = getIntent ().getExtras ().getInt ( "workID" );
		m_bApplied = getIntent ().getExtras ().getBoolean ( "Applied" );

		m_btn_Apply = ( Button ) findViewById ( R.id.workinfo_Button_Apply );
		m_btn_Back = ( Button ) findViewById ( R.id.workInfo_Button_Back );


		m_btn_Back.setOnClickListener ( this );

		if ( ! m_bApplied )                    // 아직 매칭이 된 일이 아닐 경우
		{
			m_btn_Apply.setOnClickListener ( this );
		}
		else                                // 매칭이 된 일이다
		{
			m_btn_Messenger = ( Button ) findViewById ( R.id.workinfo_Button_Messenger );
			m_btn_Messenger.setOnClickListener ( this );

			m_btn_Apply.setEnabled ( false );
			m_btn_Apply.setVisibility ( View.INVISIBLE );
		}

		renderWork ();
	}

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_Apply )
		{
			// 신청하는 거 정보 전해주기

			Intent intent = new Intent ();

			intent.putExtra ( "apply" , true );
			setResult ( RESULT_OK , intent );

			Log.d ( "CHECK DELIVERY" , "신청 버튼을 눌렀습니다." );

			finish ();
		}
		else if ( view == m_btn_Back )
		{
			Log.d ( "CHECK DELIVERY" , "뒤로가기 버튼을 눌렀습니다." );

			finish ();
		}
		else if ( view == m_btn_Messenger )
		{
			Intent intent = new Intent ( getApplicationContext () , project_08_messenger.class );
			intent.putExtra ( "workID" , m_iWorkCode );
			intent.putExtra ( "User" , true );            // true면 내가 일함, false면 내가 사장

			Log.d ( "CHECK CHECK DELIVERY" , "고용주와 대화하는 메신저로 이동합니다." );

			startActivity ( intent );
		}
	}
}