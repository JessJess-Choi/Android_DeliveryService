package com.example.projectdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class project_03_selection extends AppCompatActivity implements View.OnClickListener
{
	Button m_btn_selection_Work;
	Button m_btn_selection_Employ;


	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_03_selection );

		m_btn_selection_Work = ( Button ) findViewById ( R.id.selection_Work );
		m_btn_selection_Employ = ( Button ) findViewById ( R.id.selection_Employ );

		m_btn_selection_Work.setOnClickListener ( this );
		m_btn_selection_Employ.setOnClickListener ( this );
	}

	@Override
	public void onBackPressed ()
	{
//      super.onBackPressed ();
		// Back 버튼으로 로그인 화면으로 가는 행위 비활성화
	}

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_selection_Work )
		{
			Intent intent = new Intent ( getApplicationContext () , project_04_tab_main.class );
			Log.d ( "CHECK DELIVERY" , "project_04 05 06, 구직 화면 ViewPager로 이동합니다." );

			startActivity ( intent );
		}
		else if ( view == m_btn_selection_Employ )
		{
			Intent intent = new Intent ( getApplicationContext () , project_09_registerlist.class );
			Log.d ( "CHECK DELIVERY" , "project_09, 구인 화면으로 이동합니다." );

			startActivity ( intent );
		}
	}
}