package com.example.projectdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	Button m_btn_SignIn;
	Button m_btn_SignUp;
	TextView m_view_WrongInput;

	String[] REQUIRED_PERMISSIONS = { Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION };

	ArrayList < User > userData;
	ArrayList < Work > workData;
	EditText editTextId;
	EditText editTextPassword;
	String Id = "";
	String Password = "";
	DBHelper helper, helper1;
	SQLiteDatabase db, db1;
	int index = 0;
	FirebaseDatabase firebaseDatabase;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.project_01_signin );

		askPermission ();

		try
		{
			File file = new File ( "/data/user/0/com.example.projectdelivery/databases/UserData" );
			if ( file.exists () )
			{
				file.delete ();
			}
		} catch ( Exception e )
		{
			e.printStackTrace ();
		}

		m_btn_SignIn = ( Button ) findViewById ( R.id.signIn_Button_SignIn );
		m_btn_SignUp = ( Button ) findViewById ( R.id.signIn_Button_SignUp );
		m_view_WrongInput = ( TextView ) findViewById ( R.id.signIn_WrongInput );

		m_btn_SignIn.setOnClickListener ( this );
		m_btn_SignUp.setOnClickListener ( this );

		editTextId = ( EditText ) findViewById ( R.id.signIn_Edit_ID );
		editTextPassword = ( EditText ) findViewById ( R.id.signIn_Edit_Password );

		// 파이어베이스 users 데이터 가져와서 userData에 넣
		userData = new ArrayList < User > ();
		workData = new ArrayList < Work > ();

		firebaseDatabase = FirebaseDatabase.getInstance ();
		DatabaseReference databaseReference = firebaseDatabase.getReference ( "users" );
		databaseReference.addValueEventListener ( new ValueEventListener ()
		{
			@Override
			public void onDataChange ( @NonNull DataSnapshot snapshot )
			{
				for ( DataSnapshot data : snapshot.getChildren () )
				{

					try
					{
						String tmpId = data.child ( "user_id" ).getValue ( String.class );
						String tmpPw = data.child ( "user_pw" ).getValue ( String.class );
						String tmpAge = Integer.toString ( data.child ( "age" ).getValue ( Integer.class ) );
						String tmpSex = Boolean.toString ( data.child ( "sex" ).getValue ( boolean.class ) );
						String tmpName = data.child ( "name" ).getValue ( String.class );
						String tmpLocation = data.child ( "location" ).getValue ( String.class );

						userData.add ( new User ( tmpId , tmpPw , tmpAge , tmpSex , tmpLocation , tmpName ) );
					} catch ( Exception e )
					{
					}
				}
			}

			@Override
			public void onCancelled ( @NonNull DatabaseError error )
			{
				showToast ( "정보를 읽어오는데 실패하였습니다" );
			}
		} );

		//파이어베이스에서 works 데이터 가져오기
		DatabaseReference reference = firebaseDatabase.getReference ( "works" );
		reference.addValueEventListener ( new ValueEventListener ()
		{
			@Override
			public void onDataChange ( @NonNull DataSnapshot snapshot )
			{
				int i = 0;
				for ( DataSnapshot data : snapshot.getChildren () )
				{
					try
					{
						String tmpCategory = data.child ( "category" ).getValue ( String.class );
						String tmpContents = data.child ( "contents" ).getValue ( String.class );
						String tmpCost = data.child ( "cost" ).getValue ( String.class );
						String tmpTitle = data.child ( "title" ).getValue ( String.class );
						String tmpUser1 = data.child ( "user1_id" ).getValue ( String.class );
						String tmpUser2 = data.child ( "user2_id" ).getValue ( String.class );
						String tmpLati = data.child ( "latitude" ).getValue ( String.class );
						String tmpLong = data.child ( "longitude" ).getValue ( String.class );
						workData.add ( new Work ( tmpTitle , tmpCategory , tmpContents , tmpUser1 , tmpUser2 , Integer.parseInt ( tmpCost ) , tmpLati , tmpLong , i ) );
						++ i;
					} catch ( Exception e )
					{
//                  showToast ( "에러2" );
					}
				}
			}

			@Override
			public void onCancelled ( @NonNull DatabaseError error )
			{
				showToast ( "정보를 읽어오는데 실패하였습니다" );
			}
		} );
	}

	@Override
	public void onClick ( View view )
	{
		if ( view == m_btn_SignIn )
		{

			while ( true )
			{
				boolean check = true;
				helper = new DBHelper ( this );
				db = helper.getWritableDatabase ();
				Cursor C = db.rawQuery ( "select id from tb_user" , null );
				while ( C.moveToNext () )
				{
					if ( C.getString ( 0 ).equals ( userData.get ( index ).UserId ) )
					{
						check = false;
						break;
					}
				}

				if ( check )
					db.execSQL ( "insert into tb_user(id, password, age, sex, address, name) values (?,?,?,?,?,?)" , new String[] { userData.get ( index ).UserId , userData.get ( index ).UserPassword , String.valueOf ( userData.get ( index ).Age ) , String.valueOf ( userData.get ( index ).Sex ) , userData.get ( index ).Location , userData.get ( index ).Name } );

				if ( ( index + 1 ) == userData.size () )
				{
					break;
				}
				index++;
			}
			db.close ();


/***** 여기서 자꾸 에러발생 ******/
			//파이어베이스에서 가져온 일 데이터 기기DB에 저장 -> 이건 여러가지 있을수 있어서 중복처리 안함
			index = 0;

			while ( true )
			{
				boolean check = true;
				helper1 = new DBHelper ( this );
				db1 = helper1.getReadableDatabase ();

				Cursor C = db1.rawQuery ( "select category, contents, cost, latitude, longitude, title, user1, user2 from tb_posting" , null );
				while ( C.moveToNext () )
				{
					if ( C.getString ( 0 ).equals ( workData.get ( index ).category ) && C.getString ( 1 ).equals ( workData.get ( index ).contents ) && C.getString ( 2 ).equals ( workData.get ( index ).cost ) && C.getString ( 3 ).equals ( workData.get ( index ).latitude ) && C.getString ( 4 ).equals ( workData.get ( index ).longitude ) && C.getString ( 5 ).equals ( workData.get ( index ).title ) && C.getString ( 6 ).equals ( workData.get ( index ).user1 ) && C.getString ( 7 ).equals ( workData.get ( index ).user2 ) )
					{
						check = false;
						break;
					}
				}
				if ( check )
					db1.execSQL ( "insert into tb_posting (category, contents, cost, latitude, longitude, title, user1, user2) values (?,?,?,?,?,?,?,?)" , new String[] { workData.get ( index ).category , workData.get ( index ).contents , workData.get ( index ).cost , Double.toString ( workData.get ( index ).latitude ) , Double.toString ( workData.get ( index ).longitude ) , workData.get ( index ).title , workData.get ( index ).user1 , workData.get ( index ).user2 } );

				Log.d ( "CHECK DELIVERY" , "FireBase에서 User 및 Work를 받아 DB로 저장하였습니다." );

				if ( ( index + 1 ) == workData.size () )
				{
					break;
				}
				index++;
			}
			db1.close ();

			Id = editTextId.getText ().toString ();
			Password = editTextPassword.getText ().toString ();

//         do something
			if ( Id.equals ( "" ) || Password.equals ( "" ) )
			{
				showToast ( "정보를 제대로 입력하세요." );
				Log.d ( "CHECK DELIVERY" , "일부 정보를 입력하지 않았습니다." );
			}
			else
			{
				boolean check = false;
				SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase ();
				Cursor cursor = sqLiteDatabase.rawQuery ( "select id from tb_user" , null );
				while ( cursor.moveToNext () )
				{
					String tmp = cursor.getString ( 0 );
					if ( tmp.equals ( Id ) )
					{
						check = true;
						break;
					}
				}//DB에서 읽온 데이터와 아이디가 같으면 checkDB = true
				if ( check )
				{
					Cursor tmp = sqLiteDatabase.rawQuery ( "select id, password, age, sex, address from tb_user" , null );
					int cnt = 0;
					while ( tmp.moveToNext () )
					{
						if ( tmp.getString ( 0 ).equals ( Id ) )
						{
							if ( tmp.getString ( 1 ).equals ( Password ) )
							{
								showToast ( "로그인합니다" );
								Log.d ( "CHECK DELIVERY" , "로그인. ID : " + tmp.getString ( 0 ) + " , PW : " + tmp.getString ( 1 ) );
								cursor.close ();
								sqLiteDatabase.close ();

								FileWriter fileWriter;
								try
								{
									File file = new File ( getFilesDir () , "user_id.txt" );
									if ( file.exists () )
									{
										file.delete ();
									}

									file.createNewFile ();
									fileWriter = new FileWriter ( file , true );
									fileWriter.write ( Id );
									fileWriter.flush ();
									fileWriter.close ();
									Log.d ( "CHECK DELIVERY" , "로그인한 유저의 아이디 저장 완료" );

								} catch ( Exception e )
								{
									e.printStackTrace ();
								}

								Intent intent = new Intent ( getApplicationContext () , project_03_selection.class );
								Log.d ( "CHECK DELIVERY" , "project_03, 구직 및 구인 선택 화면으로 이동합니다." );
								startActivity ( intent );
							}
							else
							{
								showToast ( "비밀번호가 틀렸습니다" );
								Log.d ( "CHECK DELIVERY" , "비밀번호를 잘못 입력하였습니다." );

							}
						}
					}
					sqLiteDatabase.close ();
				}
			}

          /*
         Intent intent = new Intent(getApplicationContext(), project_03_selection.class);
         startActivity(intent);

           */
		}
		else if ( view == m_btn_SignUp )
		{
			Intent intent = new Intent ( getApplicationContext () , project_02_signup.class );
			intent.putExtra ( "firebaseSize" , userData.size () );

			Log.d ( "CHECK DELIVERY" , "project_02, 회원가입 화면으로 이동합니다." );

			startActivity ( intent );
		}
	}

	public void askPermission ()
	{
		PermissionListener permissionListener = new PermissionListener ()
		{
			@Override
			public void onPermissionGranted ()
			{
				Toast.makeText ( MainActivity.this , "상세 위치 권한 허가" , Toast.LENGTH_SHORT ).show ();
			}

			@Override
			public void onPermissionDenied ( List < String > deniedPermissions )
			{

			}
		};

		TedPermission.with ( this ).setPermissionListener ( permissionListener ).setRationaleMessage ( "일을 구하거나 등록하기 위해서는 상세 위치 권한이 필요합니다." ).setDeniedMessage ( "[설정] -> [권한] 에서 권한을 허용해주시기 바랍니다." ).setPermissions ( Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION ).check ();
		Log.d ( "CHECK DELIVERY" , "권한 요청을 하였습니다." );
	}

	public void showToast ( String string ) {Toast.makeText ( this , string , Toast.LENGTH_SHORT ).show ();}
}