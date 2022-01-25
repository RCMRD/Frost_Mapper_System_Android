package com.servir.frostmapper2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.servir.frostmapper2.utils.Constantori;
import com.servir.frostmapper2.utils.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;

public class Collecta extends AppCompatActivity {

	Button btnBack, btnSend, btnFoto;
	String datno, locno;
	String s001, s002, s003, s004, s005, s006, s007, s008, s00811, s02x;
	Spinner	s2,s3, s4, s5, s6, s7, s2x;
	EditText s811;
	RadioButton neww11A, prevv11A, neww11, prevv11 ;
	String syes = "Yes";
	String sno = "No";
	private RadioGroup radiops11A;
	private RadioGroup radiops11;

	ImageView sceneimage;
	String picnm, s_Lat, s_Lon;
	Context context = this;
	String naniask = "";
	File f, pathGeneral;
	String photosuffix = ".jpg";
	String taken = "nope";
	int erer = 0;
	String lepicpath;
	private final static int PICTURE_STUFF = 1;
	private final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 13;

	android.view.View View;
	DatabaseHandler db = DatabaseHandler.getInstance(this);

	private Uri fileUri;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolek);
        overridePendingTransition(0,0);

		btnBack = (Button) findViewById (R.id.backo);
		btnSend = (Button) findViewById (R.id.doneo);
		btnFoto = (Button) findViewById(R.id.bfopic);
		sceneimage = (ImageView) findViewById(R.id.s12pic);

		s2= (Spinner) findViewById (R.id.s1);
		s3= (Spinner) findViewById (R.id.s3);
		s4= (Spinner) findViewById (R.id.s6A);
		s2x= (Spinner) findViewById (R.id.s2x);
		s5= (Spinner) findViewById (R.id.s4);
		s6= (Spinner) findViewById (R.id.s5);
		s7= (Spinner) findViewById (R.id.s8);
		s811= (EditText) findViewById (R.id.s811);

		neww11A = (RadioButton) findViewById(R.id.neww11A);
		prevv11A = (RadioButton) findViewById(R.id.prevv11A);
		neww11 = (RadioButton) findViewById(R.id.neww11);
		prevv11 = (RadioButton) findViewById(R.id.prevv11);
		radiops11A = (RadioGroup) findViewById(R.id.radiops11A);
		radiops11 = (RadioGroup) findViewById(R.id.radiops11);

		doDBStuff("Entered");


		s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0,
									   android.view.View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String mm = s2.getSelectedItem().toString();

				if (mm.equals("None")){
					s2x.setEnabled(false);
				}else{
					s2x.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});


		radiops11A.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				if (neww11A.isChecked()){
					s2.setSelection(6);
					s3.setSelection(5);
					s2x.setSelection(0);
					s2.setEnabled(false);
					s3.setEnabled(false);
					s2x.setEnabled(false);

				}else{
					s2.setSelection(0);
					s3.setSelection(0);
					s2x.setSelection(0);
					s2.setEnabled(true);
					s3.setEnabled(true);
					s2x.setEnabled(true);
				}

			}
		});


        btnFoto.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View view){

				pathGeneral = Constantori.getFolderImages();

				picnm = datno + photosuffix;

				if (Build.VERSION.SDK_INT < 23) {
					PigaPicha();
				} else {

					if (
							ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
									ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
									ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
									ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

					) {

						PigaPicha();

					} else {

						reqPermission("Camera");

					}
				}
			}

		});
       
       
    	btnBack.setOnClickListener(new OnClickListener(){
        	
        	public void onClick(View view){
        		Intent intent = new Intent (context, MainActivity.class);
				startActivity(intent);
        	}
    	});
    
        	
    	btnSend.setOnClickListener(new OnClickListener(){
             	
    		public void onClick(View view){

				s002 = s2.getSelectedItem().toString().trim();
				s003 = s3.getSelectedItem().toString().trim();
				s004 = s4.getSelectedItem().toString().trim();
				s005 = s5.getSelectedItem().toString().trim();
				s006 = s6.getSelectedItem().toString().trim();
				s007 = s7.getSelectedItem().toString().trim();
				s02x = s2x.getSelectedItem().toString().trim();

				if(s811.getText().toString().trim().length() == 0){
					s00811 = "None";
				}else{
					s00811 = s811.getText().toString().trim();
				}

				if (neww11.isChecked()){
					s008 = sno;
				}else if (prevv11.isChecked()){
					s008 = syes;
				}

				if (neww11A.isChecked()){
					s001 = sno;
				}else if (prevv11A.isChecked()){
					s001 = syes;
				}

				if (s2.getSelectedItem().toString().trim().equals("SELECT")||
					s3.getSelectedItem().toString().trim().equals("SELECT")||
					s4.getSelectedItem().toString().trim().equals("SELECT")||
					s5.getSelectedItem().toString().trim().equals("SELECT")||
					s6.getSelectedItem().toString().trim().equals("SELECT")||
					s7.getSelectedItem().toString().trim().equals("SELECT")){

					Toast.makeText(Collecta.this, "Please complete the questionnaire first", Toast.LENGTH_LONG ).show();

				}else if(!s2.getSelectedItem().toString().trim().equals("None") &&
						 !s2.getSelectedItem().toString().trim().equals("SELECT") &&
						 s2x.getSelectedItem().toString().trim().equals("UNITS")) {

					Toast.makeText(Collecta.this, "Please specify units", Toast.LENGTH_LONG).show();
				}else if(!taken.equals("yap")){
					Toast.makeText(Collecta.this, "Please take scene photograph", Toast.LENGTH_LONG ).show();
				}else{

					s002 = s002 + " " + s02x;

					try {

						JSONObject submap = new JSONObject();

						submap.put(Constantori.KEY_DATNO, datno);
						submap.put(Constantori.KEY_LOCNO,locno);
						submap.put(Constantori.KEY_DATINDEX,locno);
						submap.put(Constantori.KEY_DATFP,s001);
						submap.put(Constantori.KEY_DATEAA,s002);
						submap.put(Constantori.KEY_DATSEV,s003);
						submap.put(Constantori.KEY_DATCW,s004);
						submap.put(Constantori.KEY_DATCC,s005);
						submap.put(Constantori.KEY_DATWS,s006);
						submap.put(Constantori.KEY_DATLRD,s007);
						submap.put(Constantori.KEY_DATFCR,s008);
						submap.put(Constantori.KEY_DATPICNM,picnm);
						submap.put(Constantori.KEY_DATCOM,s00811);
						submap.put(Constantori.KEY_DATSTATUS,Constantori.SAVE_DATSTATUS);

						JSONArray subArray = new JSONArray();

						subArray.put(submap);

						if (db.CheckIsDataAlreadyInDBorNot(Constantori.TABLE_DAT, Constantori.KEY_DATNO, datno)){
							db.updateDataToTable(Constantori.TABLE_DAT, Constantori.KEY_DATNO, datno, subArray);
						}else{
							db.insertDataToTable(Constantori.TABLE_DAT, subArray);
						}





						JSONObject submap2 = new JSONObject();
						submap2.put(Constantori.KEY_USERPIC,picnm);
						submap2.put(Constantori.KEY_USERPICPATH,lepicpath);
						submap2.put(Constantori.KEY_SENDSTAT,Constantori.SAVE_DATSTATUS);


						JSONArray subArray2 = new JSONArray();

						subArray2.put(submap2);

						db.insertDataToTable(Constantori.TABLE_PIC, subArray2);

						dlg_saved(View);

					} catch (JSONException e) {
						Log.e(Constantori.APP_ERROR_PREFIX + "_KolekError", "exception", e);
					}



				}

             		
    		}
        });

       
    }
	
	@Override
	public void onBackPressed(){
		
	}


	public void dlg_saved(View v) {
		final Dialog mbott = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		mbott.setContentView(R.layout.mbaind_sav);
		mbott.setCanceledOnTouchOutside(false);
		mbott.setCancelable(false);
		WindowManager.LayoutParams lp = mbott.getWindow().getAttributes();
		lp.dimAmount=0.85f;
		mbott.getWindow().setAttributes(lp);
		mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		Button mbaok = (Button) mbott.findViewById(R.id.mbabtn1);
		mbaok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){

				File[] tempFiles = Constantori.getFolderImages().listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("temp");
                    }
                });

                for (File file : tempFiles) {
                    if (file.exists()){
                        file.delete();
                    }
                }

				Intent intent = new Intent (context, MainActivity.class);
				startActivity(intent);

				mbott.dismiss();
			}
		});
		mbott.show();
	}

	 private void saveImage(Bitmap finalBitmap) {

		 Log.e(Constantori.APP_ERROR_PREFIX+"_CollectPics",pathGeneral.getAbsolutePath() + " : " + picnm);

		 File file = new File(pathGeneral.getAbsolutePath(), picnm);

		 if (file.exists()) {
			 file.delete();
		 }

		 try {
			 FileOutputStream out = new FileOutputStream(file);
			 finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			 out.flush();
			 out.close();

			 taken = "yap";
			 erer = 1;

			 if (file.exists()) {
				 Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				 sceneimage.setImageBitmap(bitmap);
				 lepicpath = file.getAbsolutePath();

				 ExifInterface exif = new ExifInterface(lepicpath);
				 exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,s_Lat);
				 exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, s_Lon);
				 exif.saveAttributes();

			 }



		 }
		 catch (Exception e) {
			 Log.e(Constantori.APP_ERROR_PREFIX+"_CollectPicsError","Error", e);
		 }

		 // Tell the media scanner about the new file so that it is
		 // immediately available to the user.
		 MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
				 new MediaScannerConnection.OnScanCompletedListener() {
					 public void onScanCompleted(String path, Uri uri) {

					 }
				 });
	 }
     


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICTURE_STUFF && resultCode == RESULT_OK) {


			Bitmap bitmap;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds= false;
			bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
			MediaStore.Images.Media.insertImage(getContentResolver(),
					bitmap,
					String.valueOf(System.currentTimeMillis()),
					"Description");

			saveImage(bitmap);

		}


	}


	private File createImageFile() throws IOException {
		// Create an image file name

		
		File image = File.createTempFile(
				"temp",
				".jpg",
				pathGeneral
		);

		f = image;


		return f;
	}

	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		try {
			outState.putParcelable("file_uri", fileUri);
		}catch (Exception ss){

		}
	}

	/*
    * Here we restore the fileUri again
    */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		try{
			fileUri = savedInstanceState.getParcelable("file_uri");
		}catch (Exception ss){

		}
	}

	@SuppressLint("NewApi")
	private void reqPermission(final String nini) {

		naniask = nini;

		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
			permissionsNeeded.add("Location");
		if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
			permissionsNeeded.add("Storage");
		if (!addPermission(permissionsList, Manifest.permission.CAMERA))
			permissionsNeeded.add("Camera");
		if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
			permissionsNeeded.add("Cell Network");

		if (permissionsList.size() > 0) {
			if (permissionsNeeded.size() > 0) {
				// Need Rationale
				String message = "This application needs to access your " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++)
					message = message + ", " + permissionsNeeded.get(i);
				showMessageOKCancel(message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
										REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
							}
						});
				return;
			}
			requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			return;
		}

		return;

	}

	@SuppressLint("NewApi")
	private boolean addPermission(List<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!shouldShowRequestPermissionRationale(permission))
				return false;
		}
		return true;
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(context)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
			{
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(
						Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						) {
					// All Permissions Granted
					if (naniask.equals("Camera")) {
						PigaPicha();
					}
						Toast.makeText(context, "Permissions enabled", Toast.LENGTH_LONG).show();

				} else {
					// Permission Denied
					Toast.makeText(context, "Some Permission is Denied", Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void PigaPicha() {

		if (Build.VERSION.SDK_INT < 23) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			f = new File(getExternalStorageDirectory(), picnm);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(intent, PICTURE_STUFF);

		} else {

			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(Collecta.this, Collecta.this.getApplicationContext().getPackageName() + ".provider", createImageFile()));
				startActivityForResult(intent, PICTURE_STUFF);

			} catch (Exception zz) {
				Log.e(Constantori.APP_ERROR_PREFIX+"_Camera", "exception", zz);
				Toast.makeText(context,  Constantori.ERROR_NO_MEDIA_PERMISSION, Toast.LENGTH_LONG).show();
			}

		}
	}

	private void doDBStuff(String where){

		switch (where){

			case "Entered":

				Intent intent0 = getIntent();
				locno = intent0.getStringExtra(Constantori.INTENT_LOCNO);

				Log.e(Constantori.APP_ERROR_PREFIX + "_Kolek_0", locno);

				Intent intent1 = getIntent();
				datno = intent1.getStringExtra(Constantori.INTENT_DATNO);

				List<HashMap<String, String>> allData = db.GetAllData(Constantori.TABLE_LOC, Constantori.KEY_LOCNO, locno);
				HashMap<String, String> allDetails = allData.get(0);

				s_Lat = allDetails.get(Constantori.KEY_LOCLAT);
				s_Lon = allDetails.get(Constantori.KEY_LOCLON);

				break;

			default:
				//System.out.println("Uuuuuwi");
				break;

		}

	}

}
