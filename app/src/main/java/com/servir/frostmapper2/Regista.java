package com.servir.frostmapper2;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.servir.frostmapper2.utils.ApplicationContextor;
import com.servir.frostmapper2.utils.AsyncTaskCompleteListener;
import com.servir.frostmapper2.utils.Constantori;
import com.servir.frostmapper2.utils.NetPost;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Regista extends AppCompatActivity implements AsyncTaskCompleteListener {

	EditText edtUseremail,edtuserpassword,edtUserpasswordconfirm, edtUsername, edtUserphone;
	TextInputLayout textIL1, textIL2, textIL3, textIL4, textIL5;
	View View;
	String ssLat, ssLon;
	String redirectFrom = "";
	public Context context = this;
	private String ssCountry;
	private String ssUsername;
	private String ssUserphone;
	private String ssUseremail;
	private String ssUserpassword;
	GoogleApiClient client;

	private Button butreg;
	private android.view.View parent_view;

	TextView txtCountry;

	private boolean isLocationRequestShowing = false;

	private ArrayList<String> URL_LINKS = new ArrayList<String>();
	private String URL_LINK = "";

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regista_new);
		overridePendingTransition(0,0);

		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtUserphone = (EditText) findViewById(R.id.edtUserphone);
		edtUseremail = (EditText) findViewById(R.id.edtUseremail);
		edtuserpassword = (EditText) findViewById(R.id.edtUserpassword);
		edtUserpasswordconfirm = (EditText) findViewById(R.id.edtUserpasswordconfirm);
		txtCountry =  (TextView) findViewById(R.id.txtCountry);

		textIL1 = (TextInputLayout)  findViewById(R.id.textIL1);
		textIL2 = (TextInputLayout)  findViewById(R.id.textIL2);
		textIL3 = (TextInputLayout)  findViewById(R.id.textIL3);
		textIL4 = (TextInputLayout)  findViewById(R.id.textIL4);
		textIL5 = (TextInputLayout)  findViewById(R.id.textIL5);

		edtUsername.addTextChangedListener(new MyTextWatcher(textIL1));
		edtUserphone.addTextChangedListener(new MyTextWatcher(textIL2));
		edtUseremail.addTextChangedListener(new MyTextWatcher(textIL3));
		edtuserpassword.addTextChangedListener(new MyTextWatcher(textIL4));
		edtUserpasswordconfirm.addTextChangedListener(new MyTextWatcher(textIL5));

		parent_view = findViewById(android.R.id.content);
		butreg = findViewById (R.id.logreg);

		URL_LINKS = Constantori.getURLs(Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE));
		URL_LINK = URL_LINKS.get(0);

		doDBstuff("Open");

		Intent intentqa = getIntent();
		ssLat = intentqa.getStringExtra(Constantori.INTENT_LAT);
		Intent intentqaa = getIntent();
		ssLon = intentqaa.getStringExtra(Constantori.INTENT_LON);
		Intent intentqo = getIntent();
		redirectFrom = intentqo.getStringExtra(Constantori.PAGE_REDIRECT);

		ssCountry = Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRY);
		txtCountry.setText(ssCountry);



		if (ssLat.equals("0.0") && ssLon.equals("0.0")) {

			final Timer timer = new Timer("swcha");
			timer.schedule(new TimerTask() {

				public void run() {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if (!ssLat.equals("0.0") && !ssLon.equals("0.0")) {

								//Former - get Country by Lat and Lon
								//ssCountry = Constantori.getUserCountry(context, Double.valueOf(ssLat), Double.valueOf(ssLon));
								isLocationRequestShowing = false;
								timer.cancel();

							} else {
								GetLocation();
							}


						}

					});

				}

			}, 0, 2000);

		}



		butreg.setOnClickListener(new OnClickListener(){

			public void onClick(View view){


				if (!validateName()) {
					return;
				}

				if (!validateCountry()) {
					return;
				}

				if (!validatePhone()) {
					return;
				}

				if (!validateEmail()) {
					return;
				}

				if (!validatePass()) {
					return;
				}

				if (!validatePass2()) {
					return;
				}

				ssCountry  = txtCountry.getText().toString().trim();
				ssUsername= edtUsername.getText().toString().trim();
				ssUserphone = edtUserphone.getText().toString().trim();
				ssUserphone.replaceAll("[^0-9+]", "");
				ssUseremail = edtUseremail.getText().toString().trim();

				if(ssUseremail.length()==0){
					ssUseremail = "None";
				}

				ssUserpassword = edtuserpassword.getText().toString().trim();

				try {

					JSONArray json = new JSONArray();
					JSONObject postData = new JSONObject();

					postData.put(Constantori.KEY_USERNEM, ssUsername);
					postData.put(Constantori.KEY_USERTEL, ssUserphone);
					postData.put(Constantori.KEY_USERCNTRY, ssCountry);
					postData.put(Constantori.KEY_USEREMAIL, ssUseremail);
					postData.put(Constantori.KEY_USERPASS, ssUserpassword);
					postData.put(Constantori.KEY_USERLAT, ssLat);
					postData.put(Constantori.KEY_USERLON, ssLon);

					json.put(postData);

					Log.e(Constantori.APP_ERROR_PREFIX+"_RegSend", postData.toString());

					if (Constantori.isConnectedToInternet()) {

						new NetPost(context, "regista_PostJSON", json, getResources().getString(R.string.signup_message), "", "", Regista.this).execute(new String[]{URL_LINK});

					}else{
						Constantori.dlgNoNet(View, context);
					}


				}catch (Exception x){
					Log.e(Constantori.APP_ERROR_PREFIX+"_UserReg", x.getMessage(), x);
				}

			}
		});

	}

	public void onClick_su(View v)
	{
		Intent intent = new Intent(context, Loginno.class);
		startActivity(intent);
	}


	private class MyTextWatcher implements TextWatcher {

		private View view;

		private MyTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void afterTextChanged(Editable editable) {
			switch (view.getId()) {
				case R.id.textIL1:
					validateName();
					break;
				case R.id.textIL2:
					validatePhone();
					break;
				case R.id.textIL3:
					validateEmail();
					break;
				case R.id.textIL4:
					validatePass();
					break;
				case R.id.textIL5:
					validatePass2();
					break;
			}
		}
	}

	private boolean validateName() {
		if (edtUsername.getText().toString().trim().isEmpty()) {
			textIL1.setError(getString(R.string.err_msg_name));
			requestFocus(edtUsername);
			return false;
		} else {
			textIL1.setErrorEnabled(false);
		}

		return true;
	}

	private boolean validateCountry() {
		if (txtCountry.getText().toString().trim().isEmpty()) {
			if(txtCountry.getText().toString().trim().equals(Constantori.ERROR_NO_SIM)) {
				return false;
			}
		}

		return true;
	}

	private boolean validateEmail() {
		String email = edtUseremail.getText().toString().trim();

		if(!email.isEmpty()) {

			if (!isValidEmail(email)) {
				textIL3.setError(getString(R.string.err_msg_email));
				requestFocus(edtUseremail);
				return false;
			} else {
				textIL3.setErrorEnabled(false);
			}

		}

		return true;
	}

	private boolean validatePass() {
		if (edtuserpassword.getText().toString().trim().isEmpty()) {
			textIL4.setError(getString(R.string.err_msg_password));
			requestFocus(edtuserpassword);
			return false;
		} else {
			textIL4.setErrorEnabled(false);
		}

		return true;
	}

	private boolean validatePass2() {
		if (!edtUserpasswordconfirm.getText().toString().trim().equals(edtUserpasswordconfirm.getText().toString().trim())) {
			textIL5.setError(getString(R.string.err_msg_password2));
			requestFocus(edtUserpasswordconfirm);
			return false;
		} else {
			textIL5.setErrorEnabled(false);
		}

		return true;
	}

	private boolean validatePhone() {
		if (edtUserphone.getText().toString().trim().length()!=0 && edtUserphone.getText().toString().trim().length() < 10) {
			textIL2.setError(getString(R.string.err_msg_phone));
			requestFocus(edtUserphone);
			return false;
		} else {
			textIL2.setErrorEnabled(false);
		}

		return true;
	}

	private static boolean isValidEmail(String email) {
		return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}


	public void dlgSent(View v) {
		final Dialog mbott = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		mbott.setContentView(R.layout.mbaind_user_saved);
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

				if (redirectFrom.equals(Constantori.PAGE_REDIRECT_MAIN)) {

					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(context, Loginno.class);
					startActivity(intent);
				}

				mbott.dismiss();
			}
		});
		mbott.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);

				if (redirectFrom.equals(Constantori.PAGE_REDIRECT_MAIN)) {

					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(context, Loginno.class);
					startActivity(intent);
				}

				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){

		Intent intentqo = getIntent();
		redirectFrom = intentqo.getStringExtra(Constantori.PAGE_REDIRECT);

		if (redirectFrom.equals(Constantori.PAGE_REDIRECT_MAIN)) {

			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(context, Loginno.class);
			startActivity(intent);
		}
	}


	public void doDBstuff(String where){

		switch (where){

			case "Open":

				try {

					String status = Constantori.getFromSharedPreference(Constantori.KEY_USERSTATUS);

					if (status.equals(Constantori.USERACTIVE)) {

						edtUsername.setText(Constantori.getFromSharedPreference(Constantori.KEY_USERNEM));
						edtUserphone.setText(Constantori.getFromSharedPreference(Constantori.KEY_USERTEL));
						edtUseremail.setText(Constantori.getFromSharedPreference(Constantori.KEY_USEREMAIL));
						edtuserpassword.setText(Constantori.getFromSharedPreference(Constantori.KEY_USERPASS));

					}

				}catch(Exception xx){

				}

				break;

			default:
				break;
		}

	}

	@Override
	public void AsyncTaskCompleteListener(String result, String sender, String TableName, String FieldName) {
		switch (sender) {

			case "regista_PostJSON":

				if(result.equals(null)) {
					Snackbar.make(parent_view, Constantori.ERROR_SERVER_ISSUE, Snackbar.LENGTH_SHORT).show();
				}else if(result.equals("Issue")) {
					Constantori.dlgNoNet(View, context);
				}else if(result.equals("404")) {
					Snackbar.make(parent_view, Constantori.ERROR_USER_EXISTS, Snackbar.LENGTH_SHORT).show();
				}else if(result.equals("303")) {
					Snackbar.make(parent_view, Constantori.ERROR_SERVER_ISSUE, Snackbar.LENGTH_SHORT).show();
				}else {

					try {

						JSONArray storesArray = new JSONArray(result);
						JSONObject storeObject = storesArray.getJSONObject(0);
						String pass = storeObject.getString(Constantori.POST_RESPONSEKEY);

						if (pass.equals(Constantori.POST_RESPONSEVAL)) {
							dlgSent(View);
						}

					} catch (Exception xx) {
						Log.e(Constantori.APP_ERROR_PREFIX + "_RegistaJSON", Objects.requireNonNull(xx.getMessage()));
						xx.printStackTrace();
					}

				}

				break;

			default:
				break;

		}
	}

	public void GetLocation(){
		if(ApplicationContextor.getGoogleApiHelper().isConnected())
		{

			Log.e(Constantori.APP_ERROR_PREFIX + "_Location", "one");

			//ApplicationContextor.getGoogleApiHelper().onGPSOFF(context, Regista.this);


			if ( Build.VERSION.SDK_INT >= 23 &&
					ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
					ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				ActivityCompat.requestPermissions(Regista.this,
						new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
						Constantori.REQUEST_LOCATION);

				ActivityCompat.requestPermissions(Regista.this,
						new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
						Constantori.REQUEST_LOCATION);
			} else {

				client = ApplicationContextor.getGoogleApiHelper().getGoogleApiClient();
				Location ll = LocationServices.FusedLocationApi.getLastLocation(client);

				if (ll != null && ssLat.equals("0.0") && ssLon.equals("0.0")) {
					ssLon = String.valueOf(ll.getLongitude());
					ssLat = String.valueOf(ll.getLatitude());
				}
			}

		}else{



			Log.e(Constantori.APP_ERROR_PREFIX + "_Location", "two");

			ArrayList<String> coords =  Constantori.nonGPlayServices(context, Regista.this);

			if(coords.size() != 2){
				if(!isLocationRequestShowing) {
					ApplicationContextor.getGoogleApiHelper().onGPSOFF(context, Regista.this);
					isLocationRequestShowing = true;
				}
			}else{
				ssLon = coords.get(1);
				ssLat = coords.get(0);
			}

		}
	}

}