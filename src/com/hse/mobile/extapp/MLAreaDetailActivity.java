package com.hse.mobile.extapp;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.util.ImageCache;
import com.hse.mobile.oa.util.NetImageGetter;
import com.hse.mobile.oapub.BaseActivity;

/**
 * 美丽社区信息详情
 * 
 * @author dbz
 *
 */
public class MLAreaDetailActivity extends BaseActivity implements
		CordovaInterface {
	CordovaWebView appView;
	private static String url = "http://web.mlarea.com/view/inf/informationUrl.html?c=DiscountSaleInfo&n=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("detail", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mlarea_detail);
		appView = (CordovaWebView) this.findViewById(R.id.mlarea_webview);
		appView.getSettings().setJavaScriptEnabled(true);
		appView.setWebChromeClient(new CordovaChromeClient(this, appView));
		appView.setWebViewClient(new WebViewClient());
		initView();
	}

	private void initView() {
		String data = (String) this.getIntent().getStringExtra(
				MessageKey.MLAREA_DETAIL_ID);
		String t = "http://web.mlarea.com/view/inf/informationUrl.html?n="
				+ data + "&c=DiscountSaleInfo";
		appView.loadUrl(url + data);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	@Override
	public ExecutorService getThreadPool() {
		// TODO Auto-generated method stub
		return threadPool;
	}

	public static String TAG = "DroidGap";

	@Override
	public Object onMessage(String id, Object data) {
		org.apache.cordova.LOG.d(TAG, (new StringBuilder())
				.append("onMessage(").append(id).append(",").append(data)
				.append(")").toString());
		if ("splashscreen".equals(id)) {
			// if("hide".equals(data.toString()))
			// removeSplashScreen();
			// else
			// if(splashDialog == null || !splashDialog.isShowing())
			// {
			// splashscreen = getIntegerProperty("splashscreen", 0);
			// showSplashScreen(splashscreenTime);
			// }
		} else if ("spinner".equals(id)) {
			if ("stop".equals(data.toString())) {
				// spinnerStop();
				// appView.setVisibility(0);
			}
		} else if ("onReceivedError".equals(id)) {
			JSONObject d = (JSONObject) data;
			try {
				onReceivedError(d.getInt("errorCode"),
						d.getString("description"), d.getString("url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if ("exit".equals(id))
			endActivity();
		return null;
	}

	public void displayError(final String title, final String message,
			final String button, final boolean exit) {
		final MLAreaDetailActivity me = this;
		me.runOnUiThread(new Runnable() {
			public void run() {
				try {
					android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(
							me);
					dlg.setMessage(message);
					dlg.setTitle(title);
					dlg.setCancelable(false);
					dlg.setPositiveButton(
							button,
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (exit)
										me.endActivity();
								}
							});
					dlg.create();
					dlg.show();
				} catch (Exception e) {
					finish();
				}
			}
		});
	}

	private void onReceivedError(int errorCode, final String description,
			final String failingUrl) {
		final MLAreaDetailActivity me = this;
		{
			final boolean exit = errorCode != -2;
			me.runOnUiThread(new Runnable() {
				public void run() {
					if (exit) {
						me.appView.setVisibility(8);
						me.displayError("Application Error",
								(new StringBuilder()).append(description)
										.append(" (").append(failingUrl)
										.append(")").toString(), "OK", exit);
					}
				}
			});
		}
	}

	private void endActivity() {
		// activityState = ACTIVITY_EXITING;
		super.finish();
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin cordovaplugin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivityForResult(CordovaPlugin cordovaplugin,
			Intent intent, int i) {
		// activityResultCallback = command;
		// activityResultKeepRunning = keepRunning;
		// if (command != null)
		// keepRunning = false;
		super.startActivityForResult(intent, i);

	}

}
