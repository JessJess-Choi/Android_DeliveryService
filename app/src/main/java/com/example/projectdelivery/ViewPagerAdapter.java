package com.example.projectdelivery;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
	int m_iTabCnt ;
	
	public ViewPagerAdapter ( FragmentManager fm , int iTabCnt )
	{
		super( fm , iTabCnt );
		m_iTabCnt = iTabCnt ;
	}
	
	@Override
	public int getCount ()
	{
		return m_iTabCnt ;
	}
	
	@Override
	public Fragment getItem ( int iPosition )
	{
		if ( 0 == iPosition )
		{
			return new project_05_work_list () ;
		}
		else
		{
			return new project_06_workapplylist () ;
		}
	}
}
