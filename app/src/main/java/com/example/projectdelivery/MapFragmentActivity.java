package com.example.projectdelivery;

import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener
{
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1009;
	private FusedLocationSource m_locationSource;
	private NaverMap m_naverMap;
	private LatLng m_latLng;
	private UiSettings m_uiSettings;
	private boolean m_bCustomLocation = false;
	private Button m_btn_Apply;
	private Marker m_marker;
	
	
	@Override
	protected void onCreate ( @Nullable Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.map_fragment );
		
		m_btn_Apply = ( Button ) findViewById ( R.id.map_Button );
		m_btn_Apply.setOnClickListener ( this );
		
		m_marker = new Marker ();
		
		FragmentManager fragmentManager = getSupportFragmentManager ();
		MapFragment mapFragment = ( MapFragment ) fragmentManager.findFragmentById ( R.id.map_fragment );
		
		if ( null == mapFragment )
		{
			mapFragment = MapFragment.newInstance ();
			fragmentManager.beginTransaction ().add ( R.id.map_fragment , mapFragment ).commit ();
		}
		
		mapFragment.getMapAsync ( this );
		
		m_locationSource = new FusedLocationSource ( this , LOCATION_PERMISSION_REQUEST_CODE );
	}
	
	@Override
	public void onMapReady ( @NonNull final NaverMap naverMap )
	{
		m_naverMap = naverMap;
		m_uiSettings = naverMap.getUiSettings ();
		
		m_uiSettings.setZoomControlEnabled ( true );
		m_uiSettings.setLocationButtonEnabled ( true );
		
		m_naverMap.setLocationSource ( m_locationSource );
		m_naverMap.setLocationTrackingMode ( LocationTrackingMode.Follow );
		
		m_naverMap.addOnLocationChangeListener ( new NaverMap.OnLocationChangeListener ()
		{
			@Override
			public void onLocationChange ( @NonNull Location location )
			{
				if ( ! m_bCustomLocation )
				{
					m_latLng = new LatLng ( location.getLatitude () , location.getLongitude () );
				}
			}
		} );
		
		m_naverMap.setOnMapClickListener ( new NaverMap.OnMapClickListener ()
		{
			@Override
			public void onMapClick ( @NonNull PointF pointF , @NonNull LatLng latLng )
			{
				m_latLng = latLng;
				
				m_marker.setPosition ( m_latLng );
				m_marker.setMap ( m_naverMap );
				m_naverMap.setCameraPosition ( new CameraPosition ( m_latLng , m_naverMap.getCameraPosition ().zoom ) );
				
				m_bCustomLocation = true;
			}
		} );
	}
	
	@Override
	public void onRequestPermissionsResult ( int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults )
	{
		if ( m_locationSource.onRequestPermissionsResult ( requestCode , permissions , grantResults ) )
		{
			if ( ! m_locationSource.isActivated () )
			{
				m_naverMap.setLocationTrackingMode ( LocationTrackingMode.None );
			}
		}
		
		super.onRequestPermissionsResult ( requestCode , permissions , grantResults );
	}
	
	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_Apply )
		{
			if ( m_latLng == null )
			{
				Toast.makeText ( MapFragmentActivity.this , "위치를 선택해주시기 바랍니다." , Toast.LENGTH_SHORT ).show ();
			}
			else
			{
				Intent intent = new Intent ();

//			intent.putExtra ( "Latlng" , m_latLng );
//			setResult ( 17 , intent );
//			finish ();
//			intent.putExtra ( "apply" , true );
				intent.putExtra ( "Latlng" , m_latLng );
				setResult ( RESULT_OK , intent );
				finish ();
			}
			
		}
	}
}
