package com.hse.mobile.oapub;

import com.hse.mobile.oa.business.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 推送消息接收器
 */
public class PushMesssageReceiver extends BroadcastReceiver {
	private static final String TAG = "PushMesssageReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");     
            //新闻通知
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	
	
	/*
	 * 打印所有的 intent extra 数据
	 */
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	/*
	 * 处理自定义消息
	 */
	private void processCustomMessage(Context context, Bundle bundle) {
		HseApplication application = ((HseApplication)context.getApplicationContext());
		if(application.getIsLogged() == false){
			Log.i("test", "没有登录");
			PackageManager pm = context.getPackageManager();
			//测试版
			Intent i=pm.getLaunchIntentForPackage("com.ziteng.cn.test");
			//正式版
//			Intent i=pm.getLaunchIntentForPackage("com.hbsh.beta");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			return;
		}

		
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		
		if(application.isInBackground()){
			Log.i("test", "在后台运行");
			Intent i = new Intent(context, Main.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(JPushInterface.EXTRA_MESSAGE, message);
			i.putExtra(JPushInterface.EXTRA_EXTRA, extras);
			context.startActivity(i);
		}else{
			Log.i("test","在前台");
			Main.processCustomMessage(context, bundle);
		}

		
		/*
		Intent msgIntent = new Intent(Main.MESSAGE_RECEIVED_ACTION);
		msgIntent.putExtra(Main.KEY_MESSAGE, message);
		msgIntent.putExtra(Main.KEY_EXTRAS, extras);
		context.sendBroadcast(msgIntent);
		*/
		
		/*
		String command = "";
		JSONObject extrasJson = null;
		try {
			if(extras != null){
				extrasJson = new JSONObject(extras);
				command = extrasJson.optString("cmd");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(command.equals("news")){
			//新闻推送
			String category = extrasJson.optString("cat");
			String nid = extrasJson.optString("nid");
			
			Intent intent = new Intent(context, NewsDetail.class);
			intent.putExtra(MessageKey.NEWS_TYPE, category);
			intent.putExtra(MessageKey.NEWS_ID, nid);
			intent.putExtra(MessageKey.REUSEABLE, false);
			application.navigate(intent, "新闻", true);
		}
		else if(command.equals("task")){
			//待办推送
			
			//拼接待办URL
			HashMap<String, Object> extrasData = JsonDataConvert.parseObject(extrasJson);
			Set<String> keys = extrasData.keySet();
			String url = Constant.URL_TODOTASK;
			String query = "";
			for(String key: keys){
				if(!key.equals("cmd") && !key.equals("cat")){
					if(query.length()>0){
						query += "&";
					}
					
					query += String.format("%s=%s", key, extrasData.get(key).toString());
				}
			}
			url += query;
			
			//String url = extrasJson.optString("url");
			if(url!=null && !url.isEmpty()){
				Intent intent = new Intent(context, CondovaActivity.class);
				intent.putExtra(MessageKey.URL, url);
				intent.putExtra(MessageKey.REUSEABLE, false);
				application.navigate(intent, "待办任务", false);
				//application.showTitlebar(false);
			}
		}*/
	}
	
	
}

