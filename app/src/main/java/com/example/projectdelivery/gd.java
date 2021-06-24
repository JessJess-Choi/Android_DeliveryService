//package com.example.projectdelivery;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.naver.maps.geometry.LatLng;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Locale;
//
//public class project_05_work_list extends Fragment implements View.OnClickListener
//{
//	View m_view;
//	Spinner m_work_Spinner;
//	TextView m_LocationText;
//	boolean m_bExpensive = true;
//	int m_iCnt = 0;                                    // iCnt 말고 전체 일 개수가 따로 파이어베이스에 있으면 그거로 쓰셈
//	ArrayList < Work > m_works;
//	ArrayList < User > m_user;
//	ListView m_listView;
//	String m_strApplyCode;
//	LatLng m_latLng = null;
//	DBHelper dbHelper;
//	SQLiteDatabase db;
//	workAdapter m_workAdapter;
//	boolean m_bInitial = true;
//
//	public class workAdapter extends BaseAdapter
//	{
//		Context m_Context = null;
//		LayoutInflater m_layoutInflater = null;
//		ArrayList < Work > m_item;
//
//		public workAdapter ( Context context , ArrayList < Work > item )
//		{
//			m_Context = context;
//			m_item = item;
//			m_layoutInflater = LayoutInflater.from ( context );
//		}
//
//		@Override
//		public int getCount ()
//		{
//			return m_item.size ();
//		}
//
//		@Override
//		public Object getItem ( int position )
//		{
//			return m_item.get ( position );
//		}
//
//		@Override
//		public long getItemId ( int position )
//		{
//			return position;
//		}
//
//		@Override
//		public View getView ( int position , View convertView , ViewGroup parent )
//		{
//			View view = m_layoutInflater.inflate ( R.layout.worklist_listview , null );
//			ImageView image = ( ImageView ) view.findViewById ( R.id.work_Image );
//			TextView subject = ( TextView ) view.findViewById ( R.id.work_Subject );
//			TextView distance = ( TextView ) view.findViewById ( R.id.work_Distance );
//			TextView value = ( TextView ) view.findViewById ( R.id.work_Value );
//
//			if ( m_item.get ( position ).category == "DELIVERY" )
//			{
//				image.setImageResource ( R.drawable.ic_baseline_moped_24 );
//			}
//			else if ( m_item.get ( position ).category == "FOOD" )
//			{
//				image.setImageResource ( R.drawable.ic_baseline_fastfood_24 );
//			}
//			else if ( m_item.get ( position ).category == "HOME" )
//			{
//				image.setImageResource ( R.drawable.ic_baseline_home_24 );
//			}
//			else if ( m_item.get ( position ).category == "ETC" )
//			{
//				image.setImageResource ( R.drawable.ic_baseline_help_outline_24 );
//			}
//			subject.setText ( m_item.get ( position ).title );
//			value.setText ( Integer.parseInt ( m_item.get ( position ).cost ) + "원" );
//
//			LatLng tempLatLng = new LatLng ( m_item.get ( position ).latitude , m_item.get ( position ).longitude );
//			int iDistance = ( int ) m_latLng.distanceTo ( tempLatLng );
//			distance.setText ( Integer.toString ( iDistance ) + "m" );
//
//			return view;
//		}
//	}
//
//	public void initializeArrayList ()
//	{
//		m_works = new ArrayList < Work > ();
//		m_user = new ArrayList < User > ();
//		DBHelper dbHelper = new DBHelper ( getContext () );
//		SQLiteDatabase db = dbHelper.getReadableDatabase ();
//		SQLiteDatabase DB = dbHelper.getReadableDatabase ();
//		Cursor cursor = db.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );
//		while ( cursor.moveToNext () )
//		{
//			LatLng tempLatLng = new LatLng ( Double.parseDouble ( cursor.getString ( 6 ) ) , Double.parseDouble ( cursor.getString ( 7 ) ) );
//			int iDistance = ( int ) m_latLng.distanceTo ( tempLatLng );
//
//			if ( iDistance <= 3000 )
//			{
//				m_works.add ( new Work ( cursor.getString ( 3 ) , cursor.getString ( 0 ) , cursor.getString ( 1 ) , cursor.getString ( 4 ) , cursor.getString ( 5 ) , Integer.parseInt ( cursor.getString ( 2 ) ) , cursor.getString ( 6 ) , cursor.getString ( 7 ) ) );
//				m_iCnt++;
//			}
//		}
//		Cursor c = DB.rawQuery ( "select id, password, age, sex, address, name from tb_user" , null );
//		while ( c.moveToNext () )
//		{
//			m_user.add ( new User ( c.getString ( 0 ) , c.getString ( 1 ) , c.getString ( 2 ) , c.getString ( 3 ) , c.getString ( 4 ) , c.getString ( 5 ) ) );
//		}
//		db.close ();
//		DB.close ();
//
//	}
//
//
//	public void sortArrayList ()
//	{
//		// bExpensive에 따라서 ArrayList를 정렬해주는 함수
//		// true 이면 가격이 높은거 정렬, false면 거리순 정렬
//
//		if ( ! m_bInitial )
//		{
//			if ( m_bExpensive )
//			{
//				Collections.sort ( m_works , new Comparator < Work > ()
//				{
//					@Override
//					public int compare ( Work o1 , Work o2 )
//					{
//						return Integer.compare ( Integer.parseInt ( o1.cost ) , Integer.parseInt ( ( o2.cost ) ) );
//					}
//				} );
//			}
//			else
//			{      //거리순 정렬의 경우 주소를 저장하는 String이 위도와 경도를 , 로 구분하게 해놨습니다
//				Collections.sort ( m_works , new Comparator < Work > ()
//				{
//					@Override
//					public int compare ( Work o1 , Work o2 )
//					{
//						double latitude = m_latLng.latitude;
//						double longitude = m_latLng.longitude;
//
//						double o1_latitude = o1.latitude;
//						double o1_longtitude = o1.longitude;
//
//						double o2_latitude = o2.latitude;
//						double o2_longtitude = o2.longitude;
//
//						return Double.compare ( Math.pow ( latitude - o1_latitude , 2 ) + Math.pow ( longitude - o2_longtitude , 2 ) , Math.pow ( latitude - o2_latitude , 2 ) + Math.pow ( longitude - o2_longtitude , 2 ) );
//					}
//				} );
//			}
//
//			m_workAdapter.notifyDataSetChanged ();
//		}
//	}
//
//	@Override
//	public void onCreate ( Bundle savedInstanceState )
//	{
//		super.onCreate ( savedInstanceState );
//	}
//
//	@Override
//	public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState )
//	{
//		m_view = inflater.inflate ( R.layout.project_05_work_list , container , false );
//
//		m_LocationText = ( TextView ) m_view.findViewById ( R.id.worklist_Location );
//
//		m_latLng = new LatLng ( 37.996625 , 126.4618583 ) ;
//
//		getCurrentAddress ();
//
//		if ( m_bInitial )
//		{
//			initializeArrayList ();
//			m_bInitial = false;
//		}
//
//		m_workAdapter = new workAdapter ( this.getContext () , m_works );
//		m_listView.setAdapter ( m_workAdapter );
//
//		sortArrayList ();
//
//
//		m_workAdapter.notifyDataSetChanged ();
//
//		m_LocationText.setOnClickListener ( this );
//
//		m_work_Spinner = ( Spinner ) m_view.findViewById ( R.id.worklist_Spinner );
//
//		m_work_Spinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ()
//		{
//			@Override
//			public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id )
//			{
//				if ( 0 == position )
//				{
//					m_bExpensive = true;
//				}
//				else
//				{
//					m_bExpensive = false;
//				}
//
//				sortArrayList ();
//			}
//
//			@Override
//			public void onNothingSelected ( AdapterView < ? > parent )
//			{
//				m_bExpensive = true;
//			}
//		} );
//
//
//		m_listView = ( ListView ) m_view.findViewById ( R.id.worklist_ListView );
//
//		m_listView.setOnItemClickListener ( new AdapterView.OnItemClickListener ()
//		{
//			@Override
//			public void onItemClick ( AdapterView < ? > parent , View view , int position , long id )
//			{
//				Intent intent = new Intent ( getContext () , project_07_workinfo.class );
//				m_strApplyCode = m_works.get ( position ).user1;    // 클릭한 일의 id 가져오기
//				intent.putExtra ( "workID" , m_strApplyCode );                // 클릭한 일을 특정할 수 있는 정보를 추가
//				intent.putExtra ( "Applied" , false );
//				startActivityForResult ( intent , 1 );
//			}
//		} );
//
//
//		return m_view;
//	}
//
//	@Override
//	public void onClick ( View view )
//	{
//		if ( view == m_LocationText )
//		{
////			Intent intent = new Intent ( getContext () , MapFragmentActivity.class );
////			startActivityForResult ( intent , 17 );
//		}
//	}
//
//	@Override
//	public void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data )
//	{
//		super.onActivityResult ( requestCode , resultCode , data );
//
//
//		if ( 1 == requestCode && Activity.RESULT_OK == resultCode )
//		{
//			if ( data.getBooleanExtra ( "apply" , false ) == true )
//			{
//				//   일 신청 버튼 눌렀으니 그에 대한 처리
//				//  일단 회원가입을 해야 텍스트 파일이 생성되므로 회원가입을 한번만 해서 그 내용이 저장되어 있어야 합니다
//				//   그 아이디를 읽어와서 디비에 그것과 같은 내용이 있으면 저장
//				String string = "";
//				File file = new File ( getContext ().getFilesDir () , "user_id.txt" );
//				try
//				{
//					BufferedReader reader = new BufferedReader ( new FileReader ( file ) );
//					String line;
//					while ( ( line = reader.readLine () ) != null )
//					{
//						string = line;
//					}
//					reader.close ();
//				} catch ( Exception e )
//				{
//					e.printStackTrace ();
//				}
//				db = dbHelper.getWritableDatabase ();
//				String tmp = data.getStringExtra ( "Latlng" );
//				Cursor cursor = db.rawQuery ( "select id from tb_user" , null );
//				while ( cursor.moveToNext () )
//				{
//					if ( cursor.getString ( 0 ).equals ( string ) )
//						db.execSQL ( "insert into tb_user(id, password, age, sex, address, name) values (?,?,?,?,?,?)" , new String[] { cursor.getString ( 0 ) , cursor.getString ( 1 ) , cursor.getString ( 2 ) , cursor.getString ( 3 ) , tmp , cursor.getString ( 5 ) } );
//				}
//
//
//			}
//
//			m_strApplyCode = "";
//
//			m_workAdapter.notifyDataSetChanged ();
//
//		}
//		else if ( 17 == requestCode && Activity.RESULT_OK == resultCode )
//		{
//
//		}
//	}
//
//	public void getCurrentAddress ()
//	{
//		Geocoder geocoder = new Geocoder ( getActivity () , Locale.getDefault () );
//		List < Address > addresses;
//
//		try
//		{
//			addresses = geocoder.getFromLocation ( m_latLng.latitude , m_latLng.longitude , 7 );
//			m_LocationText.setText ( addresses.get ( 0 ).getAddressLine ( 0 ).toString () );
//		} catch ( IOException e )
//		{
//			e.printStackTrace ();
//		}
//
//
//	}
//}