package com.servir.frostmapper2;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.servir.frostmapper2.utils.Constantori;
import com.servir.frostmapper2.utils.DatabaseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CollectaLoc extends AppCompatActivity {

	Button btnBack, btnSend;
	String locno = "";
	String s_lon = "";
	String s_lat = "";
	String s001, s002, s003, s004, s005, s006, s007, s008, s009, s010;
	String locreg_status = "";
	String syes = "Yes";
	String sno = "No";
	ArrayAdapter<String> spinnerArrayAdapter;
	Spinner	s3, s4, s5, s8, s9, s10;
	EditText s1, s31, s87;
	RadioButton neww11A, prevv11A;
	RadioButton neww11B, prevv11B;
	RadioButton neww11C, prevv11C;

	String datindx;
	private RadioGroup radiopsA;
	private RadioGroup radiopsB;
	private RadioGroup radiopsC;

	android.view.View View;
	DatabaseHandler db = DatabaseHandler.getInstance(this);
	Context context = this;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kolek_loc);
        overridePendingTransition(0,0);

		btnBack = (Button) findViewById (R.id.backo);
		btnSend = (Button) findViewById (R.id.doneo);

		s1 = (EditText) findViewById(R.id.s11AAL);

		radiopsA = (RadioGroup) findViewById(R.id.radiops11AL);
		neww11A = (RadioButton) findViewById(R.id.neww11AL);
		prevv11A = (RadioButton) findViewById(R.id.prevv11AL);

		s3 = (Spinner) findViewById(R.id.s2L);
		s31 = (EditText) findViewById(R.id.s82L);

		s4 = (Spinner) findViewById(R.id.s1L);

		s5 = (Spinner) findViewById(R.id.s3L);

		radiopsB = (RadioGroup) findViewById(R.id.radiops11BL);
		neww11B = (RadioButton) findViewById(R.id.neww11BL);
		prevv11B = (RadioButton) findViewById(R.id.prevv11BL);

		radiopsC = (RadioGroup) findViewById(R.id.radiops11CL);
		neww11C = (RadioButton) findViewById(R.id.neww11CL);
		prevv11C = (RadioButton) findViewById(R.id.prevv11CL);

		s8 = (Spinner) findViewById(R.id.s6AL);
		s87= (EditText) findViewById (R.id.s87);

		s9 = (Spinner) findViewById(R.id.s7AL);
		s10 = (Spinner) findViewById(R.id.s8AL);

		doDBStuff("Entered");

		s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


			public void onItemSelected(AdapterView<?> arg0,
									   android.view.View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String mm = s3.getSelectedItem().toString();

				if (mm.equals("Other")) {
					s31.setEnabled(true);
				} else {
					s31.setText("");
					s31.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});


		s8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


			public void onItemSelected(AdapterView<?> arg0,
									   android.view.View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String mm = s8.getSelectedItem().toString();

				if (mm.equals("Other")){
					s87.setEnabled(true);
				}else{
					s87.setText("");
					s87.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});


		btnBack.setOnClickListener(new OnClickListener(){

			public void onClick(View view){

				finish();

				Intent intent = new Intent (context, MainActivity.class);
				startActivity(intent);
			}
		});
    
        	
    	btnSend.setOnClickListener(new OnClickListener(){
             	
    		public void onClick(View view) {

				s001 = s1.getText().toString().trim();
				s003 = s3.getSelectedItem().toString().trim();
				s004 = s4.getSelectedItem().toString().trim();
				s005 = s5.getSelectedItem().toString().trim();
				s008 = s8.getSelectedItem().toString().trim();

				if (s008.equals("Other")) {
					s008 = s87.getText().toString().trim();
				}

				s009 = s9.getSelectedItem().toString().trim();
				s010 = s10.getSelectedItem().toString().trim();

				if (s003.equals("Other")) {
					s003 = s31.getText().toString().trim();
				}

				if (neww11A.isChecked()) {
					s002 = sno;
				} else if (prevv11A.isChecked()) {
					s002 = syes;
				}

				if (neww11B.isChecked()) {
					s006 = sno;
				} else if (prevv11B.isChecked()) {
					s006 = syes;
				}


				if (neww11C.isChecked()) {
					s007 = sno;
				} else if (prevv11C.isChecked()) {
					s007 = syes;
				}


				if (s3.getSelectedItem().toString().trim().equals("SELECT") ||
					s4.getSelectedItem().toString().trim().equals("SELECT") ||
					s5.getSelectedItem().toString().trim().equals("SELECT") ||
					s8.getSelectedItem().toString().trim().equals("SELECT") ||
					s9.getSelectedItem().toString().trim().equals("SELECT") ||
					s10.getSelectedItem().toString().trim().equals("SELECT") ||
					s1.getText().toString().equals("")) {

					Toast.makeText(CollectaLoc.this, "Please complete the questionnaire first", Toast.LENGTH_LONG).show();

				} else if (s3.getSelectedItem().toString().trim().equals("Other") &&
					s31.getText().toString().equals("")) {

					Toast.makeText(CollectaLoc.this, "Please specify crop(s) affected", Toast.LENGTH_LONG).show();

				} else if (s8.getSelectedItem().toString().trim().equals("Other") &&
					s87.getText().toString().equals("")) {

					Toast.makeText(CollectaLoc.this, "Please specify mitigation measures", Toast.LENGTH_LONG).show();

				} else {

					try {

						JSONObject submap = new JSONObject();

						submap.put(Constantori.KEY_LOCLOC,s001);
						submap.put(Constantori.KEY_LOCAAF,s002);
						submap.put(Constantori.KEY_LOCCPA,s003);
						submap.put(Constantori.KEY_LOCAO,s004);
						submap.put(Constantori.KEY_LOCATP,s005);
						submap.put(Constantori.KEY_LOCF,s006);
						submap.put(Constantori.KEY_LOCW,s007);
						submap.put(Constantori.KEY_LOCMTM,s008);
						submap.put(Constantori.KEY_LOCFFS,s009);
						submap.put(Constantori.KEY_LOCFFA,s010);
						submap.put(Constantori.KEY_LOCLAT,s_lat);
						submap.put(Constantori.KEY_LOCLON,s_lon);
						submap.put(Constantori.KEY_LOCSTATUS,Constantori.SAVE_DATSTATUS);
						submap.put(Constantori.KEY_LOCINDEX,locno);
						submap.put(Constantori.KEY_LOCNO,locno);

						JSONArray subArray = new JSONArray();

						subArray.put(submap);

						if (locreg_status.equals(Constantori.LOC_NEW)){

							if (db.CheckIsDataAlreadyInDBorNot(Constantori.TABLE_LOC, Constantori.KEY_LOCLOC, s001)) {

								Toast.makeText(CollectaLoc.this, "Location name already exists. Please change it.", Toast.LENGTH_LONG).show();

							}else {

								db.insertDataToTable(Constantori.TABLE_LOC, subArray);
								dlg_saved(View);

							}

						}else{
							db.updateDataToTable(Constantori.TABLE_LOC, Constantori.KEY_LOCNO, locno, subArray);
							dlg_saved(View);
						}

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

				finish();

				Intent intent = new Intent (context, MainActivity.class);
	        	startActivity(intent);
	        	
	        	mbott.dismiss();
			}
		});
		mbott.show();
	}

	private void doDBStuff(String where){

		switch (where){

			case "Entered":

				Intent intent0 = getIntent();
				locreg_status = intent0.getStringExtra(Constantori.INTENT_LOCREG);

				Intent intent3 = getIntent();
				locno = intent3.getStringExtra(Constantori.INTENT_LOCNO);

				Log.e(Constantori.APP_ERROR_PREFIX + "_KolekNO", locno);

				Log.e(Constantori.APP_ERROR_PREFIX + "_KolekStat", locreg_status);

				if (locreg_status.equals(Constantori.LOC_NEW)) {

					Intent intent1 = getIntent();
					s_lon = intent1.getStringExtra(Constantori.INTENT_LON);
					Intent intent2 = getIntent();
					s_lat = intent2.getStringExtra(Constantori.INTENT_LAT);

				}else{

					List<HashMap<String, String>> allData = db.GetAllData(Constantori.TABLE_LOC, Constantori.KEY_LOCNO, locno);
					HashMap<String, String> allDetails = allData.get(0);

					Log.e(Constantori.APP_ERROR_PREFIX + "_KolekALL", allDetails.toString());

					s1.setText(allDetails.get(Constantori.KEY_LOCLOC));

					if (allDetails.get(Constantori.KEY_LOCAAF).equals(syes)){
						prevv11A.setChecked(true);
						neww11A.setChecked(false);
					}else{
						prevv11A.setChecked(false);
						neww11A.setChecked(true);
					}


					for (int i=0; i < s3.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCCPA).trim().equals(s3.getAdapter().getItem(i).toString())){
							s3.setSelection(i);
							s31.setText("");
							break;
						}else{
							s3.setSelection(5);
							s31.setText(allDetails.get(Constantori.KEY_LOCCPA));
						}
					}

					for (int i=0; i < s4.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCAO).trim().equals(s4.getAdapter().getItem(i).toString())){
							s4.setSelection(i);
							break;
						}
					}

					for (int i=0; i < s5.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCATP).trim().equals(s5.getAdapter().getItem(i).toString())){
							s5.setSelection(i);
							break;
						}
					}

					if (allDetails.get(Constantori.KEY_LOCF).equals(syes)){
						prevv11B.setChecked(true);
						neww11B.setChecked(false);
					}else{
						prevv11B.setChecked(false);
						neww11B.setChecked(true);
					}

					if (allDetails.get(Constantori.KEY_LOCW).equals(syes)){
						prevv11C.setChecked(true);
						neww11C.setChecked(false);
					}else{
						prevv11C.setChecked(false);
						neww11C.setChecked(true);
					}


					for (int i=0; i < s8.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCMTM).trim().equals(s8.getAdapter().getItem(i).toString())){
							s8.setSelection(i);
							s87.setText("");
							break;
						}else{
							s8.setSelection(4);
							s87.setText(allDetails.get(Constantori.KEY_LOCMTM));
						}
					}

					for (int i=0; i < s9.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCFFS).trim().equals(s9.getAdapter().getItem(i).toString())){
							s9.setSelection(i);
							break;
						}
					}

					for (int i=0; i < s10.getAdapter().getCount(); i++ ){
						if (allDetails.get(Constantori.KEY_LOCFFA).trim().equals(s10.getAdapter().getItem(i).toString())){
							s10.setSelection(i);
							break;
						}
					}

					s_lat =allDetails.get(Constantori.KEY_LOCLAT);
					s_lon =allDetails.get(Constantori.KEY_LOCLON);
					datindx = allDetails.get(Constantori.KEY_LOCINDEX);

				}

				break;

			default:

				break;

		}

	}

}
