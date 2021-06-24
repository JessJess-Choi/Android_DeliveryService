package com.example.projectdelivery;

import android.graphics.Bitmap;

public class message_item
{
	public String m_strMessage ;
	boolean m_bWorker ;
	boolean m_bPhoto ;
	Bitmap m_Bitmap ;

	message_item ( DBHelper dbHelper )
	{
		// dbHelper를 통해서 db 가져오기
	}
	message_item ( String strMessage , boolean bWorker , boolean bPhoto )         // 구현 시범용
	{
		m_strMessage = strMessage ;
		m_bWorker = bWorker ;
		m_bPhoto = bPhoto ;
	}

	message_item ( Bitmap bitmap , boolean bWorker )         // 구현 시범용
	{
		m_strMessage = null ;
		m_Bitmap = bitmap ;
		m_bWorker = bWorker ;
		m_bPhoto = true ;
	}
}