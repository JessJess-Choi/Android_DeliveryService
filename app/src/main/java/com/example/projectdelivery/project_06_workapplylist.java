package com.example.projectdelivery;

import androidx.fragment.app.Fragment;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

public class project_06_workapplylist extends Fragment
{
	View m_view;
	Spinner m_applylist_Spinner;
	boolean m_bExpensive;
	ListView m_listView;
	workAdapter m_workAdapter;
	ArrayList < Work > m_works;
	LatLng m_latLng;
	String m_strUserName;
	boolean bInitial = true;

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
			ImageView image = ( ImageView ) view.findViewById ( R.id.work_Image );
			TextView subject = ( TextView ) view.findViewById ( R.id.work_Subject );
			TextView distance = ( TextView ) view.findViewById ( R.id.work_Distance );
			TextView value = ( TextView ) view.findViewById ( R.id.work_Value );

			if ( m_item.get ( position ).category.equals ( "CHECK DELIVERY" ) )
			{
				image.setImageResource ( R.drawable.ic_baseline_moped_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "FOOD" ) )
			{
				image.setImageResource ( R.drawable.ic_baseline_fastfood_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "HOME" ) )
			{
				image.setImageResource ( R.drawable.ic_baseline_home_24 );
			}
			else if ( m_item.get ( position ).category.equals ( "ETC" ) )
			{
				image.setImageResource ( R.drawable.ic_baseline_help_outline_24 );
			}
			subject.setText ( m_item.get ( position ).title );
			value.setText ( Integer.parseInt ( m_item.get ( position ).cost ) + "???" );

			if ( m_latLng != null )
			{
				LatLng tempLatLng = new LatLng ( m_item.get ( position ).latitude , m_item.get ( position ).longitude );
				int iDistance = ( int ) m_latLng.distanceTo ( tempLatLng );
				distance.setText ( Integer.toString ( iDistance ) + "m" );
			}

			return view;
		}
	}

	@Override
	public void setUserVisibleHint ( boolean isVisibleToUser )
	{
		super.setUserVisibleHint ( isVisibleToUser );
		if ( isVisibleToUser )
		{
			Log.d ( "CHECK DELIVERY" , "?????? ????????? ????????? ????????????." );

			m_workAdapter = new workAdapter ( this.getContext () , m_works );
			m_listView.setAdapter ( m_workAdapter );

			sortArrayList ();

			m_workAdapter.notifyDataSetChanged ();
		}
		else
		{

		}
	}

	public void initializeArrayList ()
	{
		m_works = new ArrayList < Work > ();

		try
		{
			File file = new File ( getContext ().getFilesDir () , "user_id.txt" );
			BufferedReader reader = new BufferedReader ( new FileReader ( file ) );
			StringBuffer buffer = new StringBuffer ();
			String line;
			while ( ( line = reader.readLine () ) != null )
			{
				buffer.append ( line );
			}
			m_strUserName = buffer.toString ();
			reader.close ();

			DBHelper dbHelper = new DBHelper ( getContext () );
			SQLiteDatabase dbWork = dbHelper.getReadableDatabase ();
			Cursor cursor = dbWork.rawQuery ( "select category, contents, cost, title, user1, user2, latitude, longitude from tb_posting" , null );

			while ( cursor.moveToNext () )
			{
				if ( m_strUserName.equals ( cursor.getString ( 5 ) ) )                                            // ?????? user2, ????????? ???????????????
				{
					m_works.add ( new Work ( cursor.getString ( 3 ) , cursor.getString ( 0 ) , cursor.getString ( 1 ) , cursor.getString ( 4 ) , cursor.getString ( 5 ) , Integer.parseInt ( cursor.getString ( 2 ) ) , cursor.getString ( 6 ) , cursor.getString ( 7 ) , cursor.getPosition () ) );
				}
			}

			dbHelper.close ();

			Log.d ( "CHECK DELIVERY" , "????????? ?????? ??????????????????." );

		} catch ( Exception e )
		{
			e.printStackTrace ();
		}
	}

	public void sortArrayList ()
	{
		if ( ! bInitial )
		{
			DBHelper dbHelper1 = new DBHelper ( getContext () );
			SQLiteDatabase db = dbHelper1.getReadableDatabase ();
			Cursor c = db.rawQuery ( "select id, password, age, sex, address, name from tb_user" , null );

			while ( c.moveToNext () )
			{
				if ( m_strUserName.equals ( c.getString ( 0 ) ) )
				{
					if ( c.getString ( 4 ) != "")
					{
						String strLocation = c.getString ( 4 );
						StringTokenizer stringTokenizer = new StringTokenizer ( strLocation , "," );
						double dLatitude = Double.parseDouble ( stringTokenizer.nextToken () );
						double dLongitude = Double.parseDouble ( stringTokenizer.nextToken () );
						m_latLng = new LatLng ( dLatitude , dLongitude );
					}

					break;
				}
			}
			db.close ();

			if ( m_bExpensive )
			{
				Collections.sort ( m_works , new Comparator < Work > ()
				{
					@Override
					public int compare ( Work o1 , Work o2 )
					{
						return Integer.compare ( Integer.parseInt ( o2.cost ) , Integer.parseInt ( ( o1.cost ) ) );
					}
				} );

				Log.d ( "CHECK DELIVERY" , "?????? ??????????????? ?????????????????????." );
			}
			else
			{      //????????? ????????? ?????? ????????? ???????????? String??? ????????? ????????? , ??? ???????????? ???????????????
				Collections.sort ( m_works , new Comparator < Work > ()
				{
					@Override
					public int compare ( Work o1 , Work o2 )
					{
//               double latitude = m_latLng.latitude;
//               double longitude = m_latLng.longitude;
//
//               double o1_latitude = o1.latitude;
//               double o1_longtitude = o1.longitude;
//
//               double o2_latitude = o2.latitude;
//               double o2_longtitude = o2.longitude;
//
//               return Double.compare ( Math.pow ( latitude - o1_latitude , 2 ) + Math.pow ( longitude - o2_longtitude , 2 ) , Math.pow ( latitude - o2_latitude , 2 ) + Math.pow ( longitude - o2_longtitude , 2 ) );
						return 0;
					}
				} );

				Log.d ( "CHECK DELIVERY" , "????????? ??????????????? ?????????????????????." );
			}


			m_workAdapter.notifyDataSetChanged ();
		}
		else
		{
			bInitial = false;
		}
	}

	@Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
	}

	@Override
	public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState )

	{
		m_view = inflater.inflate ( R.layout.project_06_workapplylist , container , false );
		m_applylist_Spinner = ( Spinner ) m_view.findViewById ( R.id.workapplylist_Spinner );

		m_applylist_Spinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ()
		{
			@Override
			public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id )
			{
				if ( 0 == position )
					m_bExpensive = true;
				else
					m_bExpensive = false;

				sortArrayList ();
			}

			@Override
			public void onNothingSelected ( AdapterView < ? > parent )
			{
				m_bExpensive = true;
			}
		} );

		initializeArrayList ();

		m_listView = ( ListView ) m_view.findViewById ( R.id.workapplylist_ListView );

		m_listView.setOnItemClickListener ( new AdapterView.OnItemClickListener ()
		{
			@Override
			public void onItemClick ( AdapterView < ? > parent , View view , int position , long id )
			{
				Intent intent = new Intent ( getContext () , project_07_workinfo.class );
				String m_strApplyCode = m_works.get ( position ).user1;    // ????????? ?????? id ????????????
				intent.putExtra ( "workID" , m_strApplyCode );                // ????????? ?????? ????????? ??? ?????? ????????? ??????
				intent.putExtra ( "workID" , - 1 );                    // ????????? ?????? ????????? ??? ?????? ????????? ??????
				intent.putExtra ( "Applied" , true );                // ????????? ???????????? ???

				Log.d ( "CHECK DELIVERY" , "????????? ??? " + m_works.get ( position).title + " ??? ???????????????." );

				startActivityForResult ( intent , 2 );
			}
		} );

		Log.d ( "CHECK DELIVERY" , "ListView ?????? ?????????????????????." );

		return m_view;
	}
}