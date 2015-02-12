package com.hse.mobile.oa.ui;

import org.apache.cordova.CordovaWebView;

import com.hse.mobile.oapub.HseApplication;
import com.hse.mobile.oapub.Main;
import com.hse.mobile.oapub.Portal;
import com.hbsh.beta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/*
 * ×Ô¶¨ÒåPhoneGap WebView
 */
public class CustomCordovaWebView extends CordovaWebView {

	public CustomCordovaWebView(Context context) {
		super(context);
	}

	public CustomCordovaWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomCordovaWebView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomCordovaWebView(Context context, AttributeSet attrs,
			int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}

	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	  if (this.backHistory()) {
        		  Log.d("Current url:", this.getUrl());
                  return true;
              }
        	  else{
                  ((HseApplication)this.getContext().getApplicationContext()).navigateBack();
              }

            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_MENU){
        	return true;
        }
        else{
            return super.onKeyUp(keyCode, event);
        }
    }

}
