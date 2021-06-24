package com.example.projectdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.google.firebase.firestore.model.SnapshotVersion;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Overlay;

import java.util.ArrayList;

public class project_10_userapplylist extends AppCompatActivity implements View.OnClickListener
{
	ListView m_listView;
	TextView m_text_Category;
	TextView m_text_Name;
	TextView m_text_Value;
	TextView m_text_Content;
	Button m_btn_Back;
	Button m_btn_Messenger;
	ArrayList < User > m_users;
	ArrayList < Work > m_Work;
	int m_iWorkCode;
	CustomDialog m_customDialog;

	public void initializeUser ()                                    // 일 ID로 지원한 유저들 받아서 Arraylist에 저장
	{
		m_users = new ArrayList < User > ();
		m_Work = new ArrayList < Work > ();
		boolean bTemp = false;

		DBHelper dbHelper = new DBHelper ( this );
		SQLiteDatabase dbUser = dbHelper.getReadableDatabase ();
		SQLiteDatabase dbWork = dbHelper.getReadableDatabase ();

		Cursor cursorWork = dbWork.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );
		Cursor cursorUser = dbUser.rawQuery ( "select id, password, age, sex, address, name from tb_user" , null );
		while ( cursorWork.moveToNext () )                                                                                // 해당 일이 구인이 되어있으면
		{
			if ( cursorWork.getPosition () == m_iWorkCode )                                                                // 일 정보 띄우기 위함
			{
				bTemp = true;
				m_text_Category.setText ( cursorWork.getString ( 0 ) );
				m_text_Name.setText ( cursorWork.getString ( 3 ) );
				m_text_Value.setText ( cursorWork.getString ( 2 ) + "원" );
				m_text_Content.setText ( cursorWork.getString ( 1 ) );

				Log.d ( "CHECK DELIVERY" , "선택한 일 " + cursorWork.getString ( 3 ) + " 을 가져왔습니다." );

				m_Work.add ( new Work ( cursorWork.getString ( 3 ) , cursorWork.getString ( 0 ) , cursorWork.getString ( 1 ) , cursorWork.getString ( 4 ) , cursorWork.getString ( 5 ) , Integer.parseInt ( cursorWork.getString ( 2 ) ) , cursorWork.getString ( 6 ) , cursorWork.getString ( 7 ) , cursorWork.getPosition () ) );

				break;
			}
		}

		if ( bTemp )
		{
			while ( cursorUser.moveToNext () )                                                                                                    // 구인이 되어 있다면 찾아서 추가
			{
				if ( m_Work.get ( 0 ).user2.equals ( cursorUser.getString ( 0 ) ) )
				{
					m_users.add ( new User ( cursorUser.getString ( 0 ) , cursorUser.getString ( 1 ) , cursorUser.getString ( 2 ) , cursorUser.getString ( 3 ) , cursorUser.getString ( 4 ) , cursorUser.getString ( 5 ) ) );

					Log.d ( "CHECK DELIVERY" , "일을 신청한 유저 " + cursorWork.getString ( 0 ) + " 의 정보를 가져왔습니다." );


					break;
				}
			}
		}


	}

	public class userAdapter extends BaseAdapter
	{
		Context m_Context = null;
		LayoutInflater m_layoutInflater = null;
		ArrayList < User > m_item;

		public userAdapter ( Context context , ArrayList < User > item )
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
			View view = m_layoutInflater.inflate ( R.layout.userlist_listview , null );
			ImageView imageSex = ( ImageView ) view.findViewById ( R.id.user_Image );
			TextView name = ( TextView ) view.findViewById ( R.id.user_Name );
			TextView distance = ( TextView ) view.findViewById ( R.id.user_Distance );

			name.setText ( m_item.get ( position ).Name );

			if ( m_item.get ( position ).Sex.equals ( "true" ) )            // Male
			{
				imageSex.setImageResource ( R.mipmap.icon_custom_male );
			}
			else
			{
				imageSex.setImageResource ( R.mipmap.icon_custom_female );
			}

			return view;
		}
	}

	public int returnDistance ( double dLat1 , double dLon1 , double dLat2 , double dLon2 )
	{
		double dTheta = dLon1 - dLon2;
		double dDistance = Math.sin ( deg2rad ( dLat1 ) ) * Math.sin ( deg2rad ( dLat2 ) ) + Math.cos ( deg2rad ( dLat1 ) ) * Math.cos ( deg2rad ( dLat2 ) ) * Math.cos ( deg2rad ( dTheta ) );
		dDistance = Math.acos ( dDistance );
		dDistance = rad2deg ( dDistance );
		dDistance = dDistance * 60 * 1.1515 * 1.609344;

		return ( ( int ) dDistance );
	}

	public double deg2rad ( double deg )
	{
		return deg * Math.PI / 180.0;
	}

	public double rad2deg ( double rad )
	{
		return ( rad / Math.PI * 180.0 );
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_10_userapplylist );

		Log.d ( "CHECK DELIVERY" , "선택한 일을 상세 조회하는 화면을 그립니다." );

		m_btn_Back = ( Button ) findViewById ( R.id.userapplylist_Back );
		m_text_Category = ( TextView ) findViewById ( R.id.userapplylist_Work_Category );
		m_text_Name = ( TextView ) findViewById ( R.id.userapplylist_Work_Name );
		m_text_Value = ( TextView ) findViewById ( R.id.userapplylist_Work_Value );
		m_text_Content = ( TextView ) findViewById ( R.id.userapplylist_Work_Content );

		m_btn_Back.setOnClickListener ( this );

		m_iWorkCode = getIntent ().getExtras ().getInt ( "workID" );
		m_listView = ( ListView ) findViewById ( R.id.userapplylist_Listview );

		initializeUser ();

		if ( ! m_users.isEmpty () )                                                                        // 구인이 되었으면
		{
			m_btn_Messenger = ( Button ) findViewById ( R.id.userapplylist_Button_Messenger );

			m_btn_Messenger.setEnabled ( true );
			m_btn_Messenger.setVisibility ( View.VISIBLE );

			m_btn_Messenger.setOnClickListener ( new View.OnClickListener ()
			{
				@Override
				public void onClick ( View view )
				{
					if ( view == m_btn_Messenger )
					{
						Intent intent = new Intent ( getApplicationContext () , project_08_messenger.class );
						intent.putExtra ( "workID" , m_iWorkCode );
						intent.putExtra ( "User" , false );            // true면 내가 일함, false면 내가 사장

						Log.d ( "CHECK DELIVERY" , "고용한 유저와 대화하는 메신저로 이동합니다." );

						startActivity ( intent );
					}
				}
			} );

			m_listView.setAdapter ( new userAdapter ( this.getApplicationContext () , m_users ) );
		}
	}

	View.OnClickListener positiveListener = new View.OnClickListener ()
	{
		@Override
		public void onClick ( View view )
		{
			// 예 버튼 누르고 처리

			m_customDialog.dismiss ();
			finish ();
		}
	};

	View.OnClickListener negativeListener = new View.OnClickListener ()
	{
		@Override
		public void onClick ( View view )
		{
			m_customDialog.dismiss ();
		}
	};

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_Back )
		{
			Log.d ( "CHECK DELIVERY" , "뒤로가기 버튼을 누르셨습니다." );

			finish ();
		}
	}
}