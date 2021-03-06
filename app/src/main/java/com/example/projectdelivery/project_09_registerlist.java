package com.example.projectdelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class project_09_registerlist extends AppCompatActivity implements View.OnClickListener
{
	ListView m_listView;
	Button m_btn_Register;
	ArrayList < Work > m_works;
	workAdapter m_workAdapter;
	String m_strUserName;
	int m_iWorkCode;

	public class workAdapter extends BaseAdapter
	{
		Context m_Context = null;
		LayoutInflater m_layoutInflater = null;
		ArrayList < Work > m_item;

		public workAdapter ( Context context , ArrayList < Work > item )
		{
			m_Context = context;
			m_item = item;
			m_layoutInflater = LayoutInflater.from ( context );
		}

		@Override
		public int getCount ()
		{
			return m_item.size ();
		}

		@Override
		public Object getItem ( int position )
		{
			return m_item.get ( position );
		}

		@Override
		public long getItemId ( int position )
		{
			return position;
		}

		@Override
		public View getView ( int position , View convertView , ViewGroup parent )
		{
			View view = m_layoutInflater.inflate ( R.layout.worklist_listview , null );
			ImageView imageCategory = ( ImageView ) view.findViewById ( R.id.work_Image );
			TextView subject = ( TextView ) view.findViewById ( R.id.work_Subject );
			TextView value = ( TextView ) view.findViewById ( R.id.work_Value );
			ImageView imageState = ( ImageView ) view.findViewById ( R.id.work_Register_Image );


			if ( m_item.get ( position ).category.equals ( "CHECK DELIVERY" ) )
			{
				imageCategory.setImageResource ( R.drawable.ic_baseline_moped_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "FOOD" ) )
			{
				imageCategory.setImageResource ( R.drawable.ic_baseline_fastfood_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "HOME" ) )
			{
				imageCategory.setImageResource ( R.drawable.ic_baseline_home_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "ETC" ) )
			{
				imageCategory.setImageResource ( R.drawable.ic_baseline_help_outline_24 );
			}

			value.setText ( Integer.parseInt ( m_item.get ( position ).cost ) + "???" );
			subject.setText ( m_item.get ( position ).category );
			imageState.setVisibility ( View.VISIBLE );

			if ( m_item.get ( position ).cost.equals ( "" ) )
			{
				imageState.setImageResource ( R.drawable.ic_baseline_done_24 );
			}
			else
			{
				imageState.setImageResource ( R.drawable.ic_baseline_more_horiz_24 );
			}

			return view;
		}
	}

	public void initializeWork ()
	{
		m_works = new ArrayList < Work > ();
		DBHelper dbHelper = new DBHelper ( this );
		SQLiteDatabase dbWork = dbHelper.getReadableDatabase ();
		Cursor cursor = dbWork.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );

		while ( cursor.moveToNext () )
		{
			if ( m_strUserName.equals ( cursor.getString ( 4 ) ) )                                            // ?????? user1 ?????????
			{
				m_works.add ( new Work ( cursor.getString ( 3 ) , cursor.getString ( 0 ) , cursor.getString ( 1 ) , cursor.getString ( 4 ) , cursor.getString ( 5 ) , Integer.parseInt ( cursor.getString ( 2 ) ) , cursor.getString ( 6 ) , cursor.getString ( 7 ) , cursor.getPosition () ) );
			}
		}

		Log.d ( "CHECK DELIVERY" , "DB?????? ????????? ????????? ?????? ??????????????????." );

		dbWork.close ();
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_09_registerlist );

		Log.d ( "CHECK DELIVERY" , "?????? ????????? ????????????." );


		m_btn_Register = ( Button ) findViewById ( R.id.registerlist_Button_Register );

		m_btn_Register.setOnClickListener ( this );

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

		initializeWork ();

		m_listView = ( ListView ) findViewById ( R.id.registerlist_ListView );
		m_workAdapter = new workAdapter ( this.getApplicationContext () , m_works );
		m_listView.setAdapter ( m_workAdapter );

		m_listView.setOnItemClickListener ( new AdapterView.OnItemClickListener ()
		{
			@Override
			public void onItemClick ( AdapterView < ? > parent , View view , int position , long id )
			{
				Intent intent = new Intent ( getApplicationContext () , project_10_userapplylist.class );
				m_iWorkCode = m_works.get ( position ).iWorkNum;            // ????????? ?????? id ????????????
				intent.putExtra ( "workID" , m_iWorkCode );                  // ????????? ?????? ????????? ??? ?????? ????????? ??????

				Log.d ( "CHECK DELIVERY" , "????????? ??? " + m_works.get ( position ).title + " ??? ???????????????." );

				startActivity ( intent );
			}
		} );                                                                                                                // ????????? ?????? ????????? startActivity??? ??????
	}                                                                                                                        // ?????? onActivityResult ????????? ???

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_Register )
		{
			Intent intent = new Intent ( getApplicationContext () , project_11_registerwork.class );

			Log.d ( "CHECK DELIVERY" , "project_11, ????????? ?????? ???????????? ???????????? ???????????????." );

			startActivityForResult ( intent , 2 );                                                                        // ????????? ??? ?????? ???????????? ??????
		}
	}

	@Override
	protected void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			String strWorkTitle = data.getStringExtra ( "workTitle" );

			DBHelper dbHelper = new DBHelper ( this );
			SQLiteDatabase dbWork = dbHelper.getReadableDatabase ();
			Cursor cursor = dbWork.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );

			while ( cursor.moveToNext () )
			{
				if ( strWorkTitle.equals ( cursor.getString ( 3 ) ) )                                    // ????????? ?????? ????????? ????????? ??? ??? ??????
				{
					m_works.add ( new Work ( cursor.getString ( 3 ) , cursor.getString ( 0 ) , cursor.getString ( 1 ) , cursor.getString ( 4 ) , cursor.getString ( 5 ) , Integer.parseInt ( cursor.getString ( 2 ) ) , cursor.getString ( 6 ) , cursor.getString ( 7 ) , cursor.getPosition () ) );
				}
			}

			dbWork.close ();

			Log.d ( "CHECK DELIVERY" , "????????? ?????? ?????????????????????." );

			super.onActivityResult ( requestCode , resultCode , data );

			m_workAdapter.notifyDataSetChanged ();
		}
	}
}