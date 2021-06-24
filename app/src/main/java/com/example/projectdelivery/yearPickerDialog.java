package com.example.projectdelivery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class yearPickerDialog extends DialogFragment
{
	private static final int MAX_YEAR = 2000;
	private static final int MIN_YEAR = 1900;
	private DatePickerDialog.OnDateSetListener m_listener;
	public int m_iYear = 2000;
	
	public void setListener ( DatePickerDialog.OnDateSetListener listener )
	{
		this.m_listener = listener;
	}
	
	Button btn_Confirm;
	Button btn_Cancel;
	
	@NonNull
	@Override
	public Dialog onCreateDialog ( @Nullable Bundle savedInstanceState )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );
		LayoutInflater inflater = getActivity ().getLayoutInflater ();
		View dialogView = inflater.inflate ( R.layout.year_picker , null );
		btn_Confirm = dialogView.findViewById ( R.id.singUp_Button_Confirm );
		btn_Cancel = dialogView.findViewById ( R.id.signUp_Button_Cancel );
		
		final NumberPicker yearPicker = ( NumberPicker ) dialogView.findViewById ( R.id.signUp_NumberPicker );
		
		
		btn_Cancel.setOnClickListener ( new View.OnClickListener ()
		{
			@Override
			public void onClick ( View view )
			{
				yearPickerDialog.this.getDialog ().cancel ();
			}
		} );
		
		btn_Confirm.setOnClickListener ( new View.OnClickListener ()
		{
			@Override
			public void onClick ( View view )
			{
				m_listener.onDateSet ( null , yearPicker.getValue () , 0 , 0 );
				yearPickerDialog.this.getDialog ().cancel ();
			}
		} );
		
		yearPicker.setMinValue ( MIN_YEAR );
		yearPicker.setMaxValue ( MAX_YEAR );
		yearPicker.setValue ( m_iYear );
		
		builder.setView ( dialogView );
		
		
		return builder.create ();
	}
}