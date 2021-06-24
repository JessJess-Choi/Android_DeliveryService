package com.example.projectdelivery;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.naver.maps.geometry.LatLng;

public class project_04_tab_main extends AppCompatActivity
{
	TabLayout m_tabLayout;
	ViewPager m_viewPager;
	ViewPagerAdapter m_viewPagerAdapter;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_04_tab_main );

		m_tabLayout = ( TabLayout ) findViewById ( R.id.tabmain_TabLayout );
		m_viewPager = ( ViewPager ) findViewById ( R.id.tabmain_ViewPager );

		m_tabLayout.addTab ( m_tabLayout.newTab ().setIcon ( R.drawable.ic_baseline_done_24 ) );                            // 이거랑 밑에꺼 수정해라
		m_tabLayout.addTab ( m_tabLayout.newTab ().setIcon ( R.drawable.ic_baseline_fastfood_24 ) );

		m_viewPagerAdapter = new ViewPagerAdapter ( getSupportFragmentManager () , m_tabLayout.getTabCount () );

		m_viewPager.setAdapter ( m_viewPagerAdapter );

		m_tabLayout.addOnTabSelectedListener ( new TabLayout.OnTabSelectedListener ()
		{
			@Override
			public void onTabSelected ( TabLayout.Tab tab )
			{
				m_viewPager.setCurrentItem ( tab.getPosition () );
			}

			@Override
			public void onTabUnselected ( TabLayout.Tab tab )
			{

			}

			@Override
			public void onTabReselected ( TabLayout.Tab tab )
			{

			}
		} );

		m_viewPager.addOnPageChangeListener ( new TabLayout.TabLayoutOnPageChangeListener ( m_tabLayout ) );

		Log.d ( "CHECK DELIVERY" , "project_04, ViewPager 및 TabLayout 설정하였습니다." );

	}

	//   @Override
//   public void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data )
//   {
//      Fragment fragment = ( Fragment ) m_viewPagerAdapter.instantiateItem ( m_viewPager , m_viewPager.getCurrentItem () );
//
//      if ( resultCode == RESULT_OK )
//      {
//         fragment.onActivityResult ( 1 , 1 , data );
//         super.onActivityResult ( requestCode , resultCode , data );
//      }
//   }
}