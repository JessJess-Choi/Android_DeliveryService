package com.example.projectdelivery;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends Dialog
{
	Button m_btn_Positive;
	Button m_btn_Negative;
	TextView m_text_Name;
	TextView m_text_Distance;
	TextView m_text_Content;
	ImageView m_imageView;
	
	View.OnClickListener m_customPositiveListener;
	View.OnClickListener m_customNegativeListener;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
	}
	
	public CustomDialog ( Context context , View.OnClickListener positiveListener , View.OnClickListener negativeListener , String strName , String strDistance , String strContent , boolean bMale )
	{
		super ( context );
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams ();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = 0.8f;
		getWindow ().setAttributes ( layoutParams );
		
		setContentView ( R.layout.custom_register_dialog );
		
		m_btn_Positive = ( Button ) findViewById ( R.id.custom_Button_OK );
		m_btn_Negative = ( Button ) findViewById ( R.id.custom_Button_NO );
		m_text_Name = ( TextView ) findViewById ( R.id.custom_Name );
		m_text_Distance = ( TextView ) findViewById ( R.id.custom_Distance );
		m_text_Content = ( TextView ) findViewById ( R.id.custom_Content );
		m_imageView = ( ImageView ) findViewById ( R.id.custom_Image );
		
		m_customPositiveListener = positiveListener;
		m_customNegativeListener = negativeListener;
		m_text_Name.setText ( strName );
		m_text_Content.setText ( strContent );
		
		if ( null != strDistance )
			m_text_Distance.setText ( strDistance );
		if ( bMale )
		{
			m_imageView.setImageResource ( R.mipmap.icon_custom_male ) ;
		}
		else
		{
			m_imageView.setImageResource ( R.mipmap.icon_custom_female ) ;
		}
		
		
		m_btn_Positive.setOnClickListener ( m_customPositiveListener );
		m_btn_Negative.setOnClickListener ( m_customNegativeListener );
	}
}
