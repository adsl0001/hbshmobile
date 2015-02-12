package com.hse.mobile.oapub;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;

import com.hbsh.beta.R;

/*
 * 程序启动界面
 */
public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);
		 new Handler().postDelayed(new Runnable(){ 
	         @Override 
	         public void run() { 
	        	 
	     		Intent loginIntent = new Intent(Splash.this,Login.class); 
	     		Splash.this.startActivity(loginIntent); 
	             Splash.this.finish(); 
	         } 
	            
	        }, 1000); 
	    } 
}
