package com.example.projectdelivery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;

public class project_08_messenger extends AppCompatActivity implements View.OnClickListener
{
	Button m_btn_Back;
	ImageView m_image_Category;
	TextView m_text_Subject;
	ListView m_listView;
	EditText m_editText;
	Button m_btn_Send;
	Button m_btn_Camera;
	Button m_btn_Gallery;
	Button m_btn_Pay;
	ArrayList < message_item > m_items;
	CustomDialog m_customDialog;
	UserBootPay m_bootPay;
	int m_iWorkCode;                  // ?????? id??? ?????? ?????????
	boolean m_bWorker;                    // true??? ?????? ???????????????
	messageAdapter m_messageAdapter;
	File tempFile;
	ArrayList < Chat > chatData; // ?????? ???????????? ???????????? ??? ?????????

	int countFireBaseMember;
	String m_strUserName;
	String filename = "";
	String m_strPay ;

	private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
	private DatabaseReference databaseReference = firebaseDatabase.getReference ();

	public class messageAdapter extends BaseAdapter
	{
		Context m_Context = null;
		LayoutInflater m_layoutInflater = null;
		ArrayList < message_item > m_item;

		public messageAdapter ( Context context , ArrayList < message_item > item )
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
			View view = m_layoutInflater.inflate ( R.layout.message_listview , null );
			TextView messageText = ( TextView ) view.findViewById ( R.id.message_Text );
			ImageView imageUser = ( ImageView ) view.findViewById ( R.id.message_Image_User );
			ImageView imagePhoto = ( ImageView ) view.findViewById ( R.id.message_Image_Photo );
			RelativeLayout.LayoutParams imgLayoutParams = new RelativeLayout.LayoutParams ( ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT );
			RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams ( ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT );
			boolean bPhoto = m_item.get ( position ).m_bPhoto ;

			if ( m_item.get ( position ).m_bWorker )                                                // ?????? ????????????
			{
				//imageUser.setImageResource ( R.drawable.tab_icon2 );                                    // ?????? ??? ????????? ????????? ????????? ??????????????? ?????? ??????????????? ???
				imgLayoutParams.addRule ( RelativeLayout.ALIGN_PARENT_RIGHT );                        // ????????? ???????????? ?????????
				imageUser.setLayoutParams ( imgLayoutParams );

				textLayoutParams.addRule ( RelativeLayout.LEFT_OF , imageUser.getId () );                // SOMETHING |LEFTOF| IMAGE


				if ( bPhoto )                                                                        // ?????? ??????
				{
					//???????????? ????????? -> ???????????? ????????????...

					String filename = m_item.get ( position ).m_strMessage ;
					FirebaseStorage storage = FirebaseStorage.getInstance ();
					Context mContext = getApplicationContext();

					storage.getReferenceFromUrl ( "gs://soongsil-7a66a.appspot.com") .child("images/"+filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							// Got the download URL for 'users/me/profile.png'
							imagePhoto.setLayoutParams ( textLayoutParams);
							//imagePhoto.setImageURI(uri);
							Glide.with(mContext).load(uri).into(imagePhoto);
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception exception) {
							// Handle any errors
						}
					});

				}
				else
				{
					messageText.setLayoutParams ( textLayoutParams );
					messageText.setText ( m_item.get ( position ).m_strMessage );

					firebaseDatabase = FirebaseDatabase.getInstance ();
					DatabaseReference databaseReference = firebaseDatabase.getReference ( "chats" );
					databaseReference.addValueEventListener ( new ValueEventListener ()
					{
						@Override
						public void onDataChange ( @NonNull DataSnapshot snapshot )
						{
							for ( DataSnapshot data : snapshot.getChildren () )
							{

								try
								{
									String work = data.child ( "work" ).getValue ( String.class );
									//??? ??????
									if ( work == "???????????????" )
									{

										String sender = data.child ( "s" ).getValue ( String.class );
										String text = data.child ( "text" ).getValue ( String.class );
										String time = data.child ( "time" ).getValue ( String.class );
										messageText.setLayoutParams ( textLayoutParams );
										messageText.setText ( m_item.get ( position ).m_strMessage );
									}

								} catch ( Exception e )
								{
								}
							}
						}

						@Override
						public void onCancelled ( @NonNull DatabaseError error )
						{//
						}
					} );

				}
			}
			else
			{
				//image.setImageResource ( R.drawable.tab_icon3 );                                    // ?????? ??? ????????? ????????? ????????? ??????????????? ?????? ??????????????? ???
				textLayoutParams.addRule ( RelativeLayout.RIGHT_OF , imageUser.getId () );            // IMAGE |RIGHTOF| SOMETHING


				if ( bPhoto )                                                                        // ?????? ??????
				{
					//???????????? ????????? -> ???????????? ????????????...
					String filename = m_item.get ( position ).m_strMessage ;

					FirebaseStorage storage = FirebaseStorage.getInstance ();
					StorageReference photoRef = storage.getReference ().child ( filename );

					final long ONE_MEGABYTE = 1024 * 1024;
					photoRef.getBytes ( ONE_MEGABYTE ).addOnSuccessListener ( new OnSuccessListener < byte[] > ()
					{
						@Override
						public void onSuccess ( byte[] bytes )
						{
							//file.png return ??? ?????? ???????????? ????????? ?????????
						}
					} ).addOnFailureListener ( new OnFailureListener ()
					{
						@Override
						public void onFailure ( @NonNull Exception exception )
						{
							// Handle any errors
						}
					} );
					imagePhoto.setLayoutParams ( textLayoutParams );
					imagePhoto.setImageBitmap ( m_item.get ( position ).m_Bitmap );


				}
				else
				{
					firebaseDatabase = FirebaseDatabase.getInstance ();
					DatabaseReference databaseReference = firebaseDatabase.getReference ( "chats" );
					databaseReference.addValueEventListener ( new ValueEventListener ()
					{
						@Override
						public void onDataChange ( @NonNull DataSnapshot snapshot )
						{
							for ( DataSnapshot data : snapshot.getChildren () )
							{

								try
								{
									String work = data.child ( "work" ).getValue ( String.class );
									//??? ??????
									if ( work == "???????????????" )
									{

										String sender = data.child ( "s" ).getValue ( String.class );
										String text = data.child ( "text" ).getValue ( String.class );
										String time = data.child ( "time" ).getValue ( String.class );
										messageText.setLayoutParams ( textLayoutParams );
										messageText.setText ( m_item.get ( position ).m_strMessage );
									}

								} catch ( Exception e )
								{
								}
							}
						}

						@Override
						public void onCancelled ( @NonNull DatabaseError error )
						{//
						}
					} );

				}
			}

			return view;
		}
	}

	public void addChattoItem ( Chat chat )
	{
		boolean bUserSend = chat.sender.equals ( m_strUserName );
		if ( chat.msg.equals ( "" ) )
		{
			m_items.add ( new message_item ( chat.msg , bUserSend , false ) );
		}
		else
		{
			StringTokenizer stringTokenizer = new StringTokenizer ( chat.msg , "." );

			if ( stringTokenizer.countTokens () == 1 )                            // MSG
			{
				m_items.add ( new message_item ( chat.msg , bUserSend , false ) );
			}
			else
			{
				m_items.add ( new message_item ( chat.msg , bUserSend , true ) );
			}
		}
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_08_messenger );

		Log.d ( "CHECK DELIVERY" , "????????? ????????? ????????? ???????????????." );

		m_iWorkCode = getIntent ().getExtras ().getInt ( "workID" );
		m_bWorker = getIntent ().getExtras ().getBoolean ( "User" );

		m_btn_Back = ( Button ) findViewById ( R.id.messenger_Button_Back );
		m_image_Category = ( ImageView ) findViewById ( R.id.messenger_Image_Category );
		m_text_Subject = ( TextView ) findViewById ( R.id.messenger_Subject );
		m_editText = ( EditText ) findViewById ( R.id.messenger_EditText );
		m_btn_Send = ( Button ) findViewById ( R.id.messenger_Button_Send );
		m_btn_Camera = ( Button ) findViewById ( R.id.messenger_Button_Camera );
		m_btn_Gallery = ( Button ) findViewById ( R.id.messenger_Button_Gallery );

		m_bootPay = new UserBootPay ();
		m_bootPay.initialize ();

		m_btn_Back.setOnClickListener ( this );
		m_btn_Send.setOnClickListener ( this );
		m_btn_Camera.setOnClickListener ( this );
		m_btn_Gallery.setOnClickListener ( this );

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

		/**** ???????????????????????? ?????? ???????????? ??? ???????????? ***/
		chatData = new ArrayList < Chat > ();
		firebaseDatabase = FirebaseDatabase.getInstance ();
		DatabaseReference databaseReference = firebaseDatabase.getReference ( "chats" ).child ( String.valueOf ( m_iWorkCode ) );
		databaseReference.addValueEventListener ( new ValueEventListener ()
		{
			@Override
			public void onDataChange ( @NonNull DataSnapshot snapshot )
			{
				for ( DataSnapshot data : snapshot.getChildren () )
				{

					try
					{
						String sender = data.child ( "sender" ).getValue ( String.class );
						String msg = data.child ( "msg" ).getValue ( String.class );
						chatData.add ( new Chat ( sender , msg ) );
						addChattoItem ( chatData.get ( chatData.size () - 1 ) );

					} catch ( Exception e )
					{
					}
				}

				Log.d ( "CHECK DELIVERY" , "???????????????????????? ?????? ????????? ???????????????." );

				m_messageAdapter.notifyDataSetChanged ();
			}

			@Override
			public void onCancelled ( @NonNull DatabaseError error )
			{

				//
			}
		} );


		m_items = new ArrayList < message_item > ();


		m_listView = ( ListView ) findViewById ( R.id.messenger_ListView );
		Intent intent = getIntent ();
		countFireBaseMember = intent.getIntExtra ( "firebaseSize" , 0 );

		if ( ! m_bWorker )
		{
			m_btn_Pay = ( Button ) findViewById ( R.id.messenger_Button_Pay );
			m_btn_Pay.setVisibility ( View.VISIBLE );
			m_btn_Pay.setEnabled ( true );
			m_listView.setPadding ( 0 , 0 , 0 , 130 );
			m_btn_Pay.setOnClickListener ( this );
		}

		m_messageAdapter = new messageAdapter ( this.getApplicationContext () , m_items );
		m_listView.setAdapter ( m_messageAdapter );

		Log.d ( "CHECK DELIVERY" , "???????????? ListView??? ?????????????????????." );
	}

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_Back )
		{
			Log.d ( "CHECK DELIVERY" , "???????????? ????????? ??????????????????." );

			finish ();
		}
		else if ( view == m_btn_Send )
		{
			String msg;
			// editText.getText() ???????????? ????????? ????????????, ?????? ???????????? ????????? ???????????? ??????
			// ???????????? ?????? ??????
			if ( m_editText.getText ().toString ().equals ( "" ) )
				return;
			msg = String.valueOf ( m_editText.getText () );

			m_items.add ( new message_item ( msg , true , false ) );

			Chat chat = new Chat ( m_strUserName , msg );
			databaseReference.child ( "chats" ).child ( String.valueOf ( m_iWorkCode ) ).push ().setValue ( chat );

			m_editText.setText ( "" );
			m_messageAdapter.notifyDataSetChanged ();

			Log.d ( "CHECK DELIVERY" , msg + " ???????????? ???????????????." );
		}
		else if ( view == m_btn_Camera )
		{
			if ( bCameraPermission () )                                          // ???????????? ?????? ????????? ?????????
			{
//            Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
//            if ( intent.resolveActivity ( getPackageManager () ) != null )
//               startActivityForResult ( intent , 23 );
				Log.d ( "CHECK DELIVERY" , "????????? ????????? ???????????????." );

				Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );

				try
				{
					tempFile = createImageFile ();
				} catch ( IOException e )
				{
					Toast.makeText ( this , "????????? ?????? ??????! ?????? ??????????????????." , Toast.LENGTH_SHORT ).show ();
					finish ();
					e.printStackTrace ();
				}
				if ( tempFile != null )
				{
					Uri photoUri;
					if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
					{
						photoUri = FileProvider.getUriForFile ( this , "com.example.projectdelivery.provider" , tempFile );
					}
					else
					{
						photoUri = Uri.fromFile ( tempFile );
					}
					intent.putExtra ( MediaStore.EXTRA_OUTPUT , photoUri );
					startActivityForResult ( intent , 23 );
				}
			}
			else
			{
				PermissionListener permissionListener = new PermissionListener ()
				{
					@Override
					public void onPermissionGranted ()
					{
						Toast.makeText ( project_08_messenger.this , "????????? ??? ?????? ?????? ??????" , Toast.LENGTH_SHORT ).show ();
					}

					@Override
					public void onPermissionDenied ( List < String > deniedPermissions )
					{

					}
				};
				TedPermission.with ( this ).setPermissionListener ( permissionListener ).setRationaleMessage ( "????????? ???????????? ????????? ??? ?????? ????????? ???????????????." ).setDeniedMessage ( "[??????] -> [??????] ?????? ????????? ?????????????????? ????????????." ).setPermissions ( Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE ).check ();

				Log.d ( "CHECK DELIVERY" , "????????? ??? ?????? ????????? ???????????????." );
			}

		}
		else if ( view == m_btn_Gallery )
		{
			Log.d ( "CHECK DELIVERY" , "??????????????? ????????? ???????????????." );

			if ( ContextCompat.checkSelfPermission ( this , Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED )                // ????????? ????????? ?????????
			{
				Intent intent = new Intent ( Intent.ACTION_PICK );
				intent.setType ( MediaStore.Images.Media.CONTENT_TYPE );
				startActivityForResult ( intent , 29 );
			}
			else
			{
				PermissionListener permissionListener = new PermissionListener ()
				{
					@Override
					public void onPermissionGranted ()
					{
						Toast.makeText ( project_08_messenger.this , "????????? ?????? ??????" , Toast.LENGTH_SHORT ).show ();
					}

					@Override
					public void onPermissionDenied ( List < String > deniedPermissions )
					{

					}
				};
				TedPermission.with ( this )
						.setPermissionListener ( permissionListener )
						.setRationaleMessage ( "?????? ????????? ???????????? ????????? ?????? ????????? ???????????????." )
						.setDeniedMessage ( "[??????] -> [??????] ?????? ????????? ?????????????????? ????????????." )
						.setPermissions ( Manifest.permission.READ_EXTERNAL_STORAGE )
						.check ();

				Log.d ( "CHECK DELIVERY" , "?????? ????????? ???????????????." );
			}
		}
		else if ( view == m_btn_Pay )
		{
			String strValue = "3,000???";                                    // ????????? Value ??? 3,000??? ?????? ??????????????? ???
			boolean bMale = true;                                            // ??? ?????? ????????? ?????? ????????????


			m_strPay = strValue ;

			m_customDialog = new CustomDialog ( project_08_messenger.this , positiveListener , negativeListener , strValue , "" , "??? ???????????? ?????? ????????? ?????????????????????????" , bMale );



			m_customDialog.show ();
		}
	}

	public boolean bCameraPermission ()
	{
		if ( ( ContextCompat.checkSelfPermission ( this , Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED ) && ( ContextCompat.checkSelfPermission ( this , Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED ) && ( ContextCompat.checkSelfPermission ( this , Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) )
			return true;
		else
			return false;
	}

	private File createImageFile () throws IOException
	{

		// ????????? ?????? ?????? ( blackJin_{??????}_ )
		String timeStamp = new SimpleDateFormat ( "HHmmss" ).format ( new Date () );
		String imageFileName = "blackJin_" + timeStamp + "_";

		// ???????????? ????????? ?????? ?????? ( blackJin )
		File storageDir = new File ( Environment.getExternalStorageDirectory () + "/blackJin/" );
		if ( ! storageDir.exists () )
			storageDir.mkdirs ();

		// ?????? ??????
		File image = File.createTempFile ( imageFileName , ".jpg" , storageDir );
//      Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

		Log.d ( "CHECK DELIVERY" , "???????????? ?????? ????????? ???????????????." );


		return image;
	}

	@Override
	protected void onActivityResult ( int requestCode , int resultCode , @Nullable Intent data )
	{
		super.onActivityResult ( requestCode , resultCode , data );

		if ( 23 == requestCode && resultCode == RESULT_OK )
		{
//         Bundle extras = data.getExtras ();
//         Bitmap bitmap = ( Bitmap ) extras.get ( "data" );

			BitmapFactory.Options options = new BitmapFactory.Options ();
			options.inJustDecodeBounds = true;
			try
			{
				InputStream in = new FileInputStream ( tempFile );
				BitmapFactory.decodeStream ( in , null , options );
				in.close ();
				in = null;
			} catch ( Exception e )
			{
				e.printStackTrace ();
			}
			Bitmap bitmap = BitmapFactory.decodeFile ( tempFile.getAbsolutePath () );

			Log.d ( "CHECK DELIVERY" , "???????????? ?????? ????????? ????????????." );

			m_items.add ( new message_item ( bitmap , ! m_bWorker ) );

			//????????? ?????? = ??????????????????
			SimpleDateFormat cur_time = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
			Date cur = new Date ();
			String sent_time = cur_time.format ( cur );

			/****?????????????????? ???????????? ****/
			FirebaseStorage storage = FirebaseStorage.getInstance ();
			SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMHH_mmss" );
			Date now = new Date ();
			filename = formatter.format ( now ) + ".png";
			//storage ????????? ?????? ???????????? ????????? ??????.
			StorageReference storageRef = storage.getReferenceFromUrl ( "gs://soongsil-7a66a.appspot.com" ).child ( "images/" + filename );
			// get the data from an ImageView as bytes

			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			bitmap.compress ( Bitmap.CompressFormat.PNG , 100 , baos );
			byte[] bit_data = baos.toByteArray ();

			UploadTask uploadTask = storageRef.putBytes ( bit_data );
			uploadTask.addOnFailureListener ( new OnFailureListener ()
			{
				@Override
				public void onFailure ( @NonNull Exception exception )
				{
					Toast.makeText ( getApplicationContext () , "????????? ??????!" , Toast.LENGTH_SHORT ).show ();
					// Handle unsuccessful uploads
				}
			} ).addOnSuccessListener ( new OnSuccessListener < UploadTask.TaskSnapshot > ()
			{
				@Override
				public void onSuccess ( UploadTask.TaskSnapshot taskSnapshot )
				{
					Toast.makeText ( getApplicationContext () , "????????? ??????!" , Toast.LENGTH_SHORT ).show ();

					Chat chat = new Chat ( m_strUserName , filename );
					databaseReference.child ( "chats" ).child ( String.valueOf ( m_iWorkCode ) ).push ().setValue ( chat );


					// taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
					// ...
				}
			} );


			m_messageAdapter.notifyDataSetChanged ();
		}
		else if ( 29 == requestCode && resultCode == RESULT_OK )
		{
			Uri photoUri = data.getData ();

			Cursor cursor = null;

			try
			{

				/*
				 *  Uri ????????????
				 *  content:/// ?????? file:/// ???  ????????????.
				 */
				String[] proj = { MediaStore.Images.Media.DATA };

				assert photoUri != null;
				cursor = getContentResolver ().query ( photoUri , proj , null , null , null );

				assert cursor != null;
				int column_index = cursor.getColumnIndexOrThrow ( MediaStore.Images.Media.DATA );

				cursor.moveToFirst ();

				tempFile = new File ( cursor.getString ( column_index ) );

				BitmapFactory.Options options = new BitmapFactory.Options ();
				Bitmap originalBm = BitmapFactory.decodeFile ( tempFile.getAbsolutePath () , options );

				//????????? ?????? = ??????????????????
				SimpleDateFormat cur_time = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
				Date cur = new Date ();
				String sent_time = cur_time.format ( cur );

				/****?????????????????? ????????????***/
				FirebaseStorage storage = FirebaseStorage.getInstance ();
				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMHH_mmss" );
				Date now = new Date ();
				filename = formatter.format ( now ) + ".png";
				//storage ????????? ?????? ???????????? ????????? ??????.
				StorageReference storageRef = storage.getReferenceFromUrl ( "gs://soongsil-7a66a.appspot.com" ).child ( "images/" + filename );
				// get the data from an ImageView as bytes

				ByteArrayOutputStream baos = new ByteArrayOutputStream ();
				originalBm.compress ( Bitmap.CompressFormat.PNG , 100 , baos );
				byte[] bit_data = baos.toByteArray ();

				UploadTask uploadTask = storageRef.putBytes ( bit_data );
				uploadTask.addOnFailureListener ( new OnFailureListener ()
				{
					@Override
					public void onFailure ( @NonNull Exception exception )
					{
						Toast.makeText ( getApplicationContext () , "????????? ??????!" , Toast.LENGTH_SHORT ).show ();
						// Handle unsuccessful uploads
					}
				} ).addOnSuccessListener ( new OnSuccessListener < UploadTask.TaskSnapshot > ()
				{
					@Override
					public void onSuccess ( UploadTask.TaskSnapshot taskSnapshot )
					{
						Toast.makeText ( getApplicationContext () , "????????? ??????!" , Toast.LENGTH_SHORT ).show ();
						//???????????? ??????????????? ??????
						Chat chat = new Chat ( m_strUserName , filename );
						databaseReference.child ( "chats" ).child ( String.valueOf ( m_iWorkCode ) ).push ().setValue ( chat );

						// taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
						// ...
					}
				} );


//            imageView.setImageBitmap(originalBm);

				Log.d ( "CHECK DELIVERY" , "??????????????? ????????? ????????? ????????????." );

				m_items.add ( new message_item ( originalBm , ! m_bWorker ) );

				m_messageAdapter.notifyDataSetChanged ();
			} finally
			{
				if ( cursor != null )
				{
					cursor.close ();
				}
			}
		}
	}

	View.OnClickListener positiveListener = new View.OnClickListener ()
	{
		@Override
		public void onClick ( View view )
		{
			// ??? ?????? ????????? ?????? ??????
			Log.d ( "CHECK DELIVERY" , "????????? ???????????????." );

			m_bootPay.onClick_request ( view );

			m_customDialog.dismiss ();

		}
	};

	View.OnClickListener negativeListener = new View.OnClickListener ()
	{
		@Override
		public void onClick ( View view )
		{
			Log.d ( "CHECK DELIVERY" , "????????? ?????????????????????." );

			m_customDialog.dismiss ();
		}
	};

	public class UserBootPay
	{
		private int m_stuck = 10;

		public void initialize ()
		{
			BootpayAnalytics.init ( getApplicationContext () , "5fc78ae82fa5c2001d037b9e" );
		}

		public void onClick_request ( View view )
		{
			BootUser bootUser = new BootUser ().setPhone ( "010-1234-5678" );
			BootExtra bootExtra = new BootExtra ().setQuotas ( new int[] { 0 , 2 , 3 } );
			//        ????????????

			Bootpay.init ( getFragmentManager () ).setApplicationId ( "5fc78ae82fa5c2001d037b9e" ) // ?????? ????????????(???????????????)??? application id ???
					.setPG ( PG.KCP ) // ????????? PG ???
					.setMethod ( Method.KAKAO ) // ????????????
					.setContext ( getApplicationContext () ).setBootUser ( bootUser ).setBootExtra ( bootExtra ).setUX ( UX.PG_DIALOG )
//                .setUserPhone("010-1234-5678") // ????????? ????????????
					.setName ( "?????????" ) // ????????? ?????????
					.setOrderId ( "1234" ) // ?????? ????????????expire_month
					.setPrice ( 10000 ) // ????????? ??????
					.addItem ( "????????? ?????????" , 1 , "ITEM_CODE_DELIVER" , 100 ) // ??????????????? ?????? ????????????, ????????? ?????? ??????
					.onConfirm ( new ConfirmListener ()
					{ // ????????? ???????????? ?????? ?????? ???????????? ?????????, ?????? ???????????? ?????? ????????? ??????
						@Override
						public void onConfirm ( @Nullable String message )
						{

							if ( 0 < m_stuck )
								Bootpay.confirm ( message ); // ????????? ?????? ??????.
							else
								Bootpay.removePaymentWindow (); // ????????? ?????? ????????? ???????????? ?????? ?????? ??????
							Log.d ( "BOOTPAY confirm" , message );
						}
					} ).onDone ( new DoneListener ()
			{ // ??????????????? ??????, ????????? ?????? ??? ????????? ????????? ????????? ???????????????
				@Override
				public void onDone ( @Nullable String message )
				{
					Log.d ( "BOOTPAY done" , message );
				}
			} ).onReady ( new ReadyListener ()
			{ // ???????????? ?????? ??????????????? ???????????? ???????????? ???????????????.
				@Override
				public void onReady ( @Nullable String message )
				{
					Log.d ( "BOOTPAY ready" , message );
				}
			} ).onCancel ( new CancelListener ()
			{ // ?????? ????????? ??????
				@Override
				public void onCancel ( @Nullable String message )
				{
					Log.d ( "BOOTPAY cancel" , message );
					finish ();
				}
			} ).onError ( new ErrorListener ()
			{ // ????????? ????????? ???????????? ??????
				@Override
				public void onError ( @Nullable String message )
				{
					Log.d ( "BOOTPAY error" , message );
				}
			} ).onClose ( new CloseListener ()
			{ //???????????? ????????? ???????????? ??????
				@Override
				public void onClose ( String message )
				{
					Log.d ( "BOOTPAY close" , "close" );
					m_items.add ( new message_item ( m_strUserName + "?????? " + m_strPay + "?????? ??????????????????." , true , false ) );
					Log.d ( "CHECK DELIVERY" , "????????? ?????????????????????." );

				}
			} ).request ();
		}
	}
}