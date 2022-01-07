package com.servir.frostmapper2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView; 

public class AboutUs extends AppCompatActivity {
 
	Button tokaa;
	Context context = this;
	TextView tvStn;
    
 
      @SuppressLint("NewApi") @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
 
            setContentView(R.layout.aboutus);
            overridePendingTransition(0,0);
            tokaa = (Button) findViewById(R.id.rback);

		  getSupportActionBar().setDisplayShowHomeEnabled(true);
		  getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            
            
        		
    		tvStn = (TextView) findViewById(R.id.txte);

    		
    		 tvStn.setMovementMethod(new ScrollingMovementMethod());
            
                		
            tokaa.setOnClickListener(new OnClickListener(){
      	    	
      	    	public void onClick(View view){
      	      	Intent intent = new Intent (context, MainActivity.class);
      	    	startActivity(intent);
      	    	finish();
      	    	}
      	      });  
      
          }
      
      
      
    
  	public void onBackPressed(){
    	 
  	}		
      
      
  }