package com.hse.mobile.oapub;

import org.apache.cordova.CordovaActivity;

import cn.jpush.android.api.JPushInterface;

import com.hbsh.beta.R;
import com.ziteng.cn.test.ReadHistory;
import com.ziteng.cn.test.Weather;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/*
 * 设置
 */
public class Setting extends BaseActivity {
	ViewGroup layLogout,help,personsetting,passwordsetting,textsizesetting,suggesttsetting,newsSetting,cachesetting,readhistory,weather;
	CheckBox  chkEnablePush;
	CheckBox  chkAutoLogin, chkSavePassword;
	Button btn_theme_green,btn_theme_blue,btn_theme_red;
	Context ctx;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.setting);
		personsetting=(ViewGroup)this.findViewById(R.id.person_setting);
		personsetting.setOnClickListener(onPersonSetting);
		passwordsetting=(ViewGroup)this.findViewById(R.id.password_setting);
		passwordsetting.setOnClickListener(onPasswordSetting);
		layLogout = (ViewGroup)this.findViewById(R.id.layout_setting_logout);
		layLogout.setOnClickListener(onLogoutClickListener);
		help=(ViewGroup)this.findViewById(R.id.layout_setting_help);
		help.setOnClickListener(onHelpClickListener);
		chkEnablePush = (CheckBox)this.findViewById(R.id.chk_setting_enablepush);
		chkEnablePush.setOnCheckedChangeListener(onEnablePushChangeListener);
		chkAutoLogin = (CheckBox)this.findViewById(R.id.chk_setting_autologin);
		chkAutoLogin.setOnCheckedChangeListener(onCheckboxChangeListener);
		chkSavePassword = (CheckBox)this.findViewById(R.id.chk_setting_savepassword);
		chkSavePassword.setOnCheckedChangeListener(onCheckboxChangeListener);
		textsizesetting=(ViewGroup)this.findViewById(R.id.textsetting);
		textsizesetting.setOnClickListener(onTextSize);
		suggesttsetting=(ViewGroup)this.findViewById(R.id.layout_setting_seggestion);
		suggesttsetting.setOnClickListener(onSuggestion);
		newsSetting=(ViewGroup)this.findViewById(R.id.layout_setting_mynews);
		newsSetting.setOnClickListener(onMynewsSetting);
		
		btn_theme_green=(Button)this.findViewById(R.id.theme_green);
		btn_theme_green.setOnClickListener(greentheme);
		btn_theme_blue=(Button)this.findViewById(R.id.theme_blue);
		btn_theme_blue.setOnClickListener(bluetheme);
		btn_theme_red=(Button)this.findViewById(R.id.theme_red);
		
		btn_theme_red.setOnClickListener(redtheme);
		readhistory=(ViewGroup)this.findViewById(R.id.readhistory_setting);
		readhistory.setOnClickListener(onHistoryClickListener);
		/*
		weather=(ViewGroup)this.findViewById(R.id.weather_setting);
		weather.setOnClickListener(onWeatherClick);
		*/
		ctx=this;
		init();
	}
	
	void init(){
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
		boolean enablePush = sp.getBoolean("enable_push", true);
		chkEnablePush.setChecked(enablePush);
		this.chkAutoLogin.setChecked(sp.getBoolean(MessageKey.CONFIG_AUTOLOGIN, true));
		this.chkSavePassword.setChecked(sp.getBoolean(MessageKey.CONFIG_SAVEPASSWORD, true));
		String themecolor=sp.getString("ThemeColor", "red");
		Log.i("test", "themecolor"+themecolor);
		checkbtnselected(themecolor);
	}
	
	public static boolean isPushEnable(Context context){
		SharedPreferences sp = context.getSharedPreferences("HseOAConfig", 0);
		boolean enablePush = sp.getBoolean("enable_push", true);
		
		return enablePush;
	}
	
	public static boolean getBooleanConfig(Context context, String key, boolean defValue){
		SharedPreferences sp = context.getSharedPreferences("HseOAConfig", 0);
		boolean value = sp.getBoolean(key, defValue);
		return value;
	}
	//主题切换
	OnClickListener greentheme=new OnClickListener(){
		public void onClick(View arg0) {
			checkbtnselected("green");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("ThemeColor", "green");
			editor.commit();
			Intent intent=new Intent();
			intent.setAction("changethetheme");
			intent.putExtra("themecolor", "green");
			sendBroadcast(intent);
		}
	};
	OnClickListener bluetheme=new OnClickListener(){
		public void onClick(View arg0) {
			checkbtnselected("blue");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("ThemeColor", "blue");
			editor.commit();
			Intent intent=new Intent();
			intent.setAction("changethetheme");
			intent.putExtra("themecolor", "blue");
			sendBroadcast(intent);
		}
	};
	OnClickListener redtheme=new OnClickListener(){
		public void onClick(View arg0) {
			checkbtnselected("red");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("ThemeColor", "red");
			editor.commit();
			Intent intent=new Intent();
			intent.setAction("changethetheme");
			intent.putExtra("themecolor", "red");
			sendBroadcast(intent);
		}
	};
	/*
	OnClickListener onWeatherClick=new OnClickListener() {
		
		public void onClick(View arg0) {
			Intent intent=new Intent(Setting.this,Weather.class);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "天气预报", true);
			
		}
	};
	*/
	public void checkbtnselected(String themecolor){
		
		if(themecolor.equals("blue")){
			btn_theme_blue.setBackgroundResource(R.drawable.check_selected);
			btn_theme_green.setBackgroundResource(R.drawable.touming);
			btn_theme_red.setBackgroundResource(R.drawable.touming);
		}
		else if(themecolor.equals("red"))
		{
			btn_theme_red.setBackgroundResource(R.drawable.check_selected);
			btn_theme_blue.setBackgroundResource(R.drawable.touming);
			btn_theme_green.setBackgroundResource(R.drawable.touming);
		}
		else if(themecolor.equals("green")){
			btn_theme_green.setBackgroundResource(R.drawable.check_selected);
			btn_theme_blue.setBackgroundResource(R.drawable.touming);
			btn_theme_red.setBackgroundResource(R.drawable.touming);
		}
	}
	//个人信息维护
	OnClickListener onPersonSetting=new OnClickListener() {
		public void onClick(View v) {
			String url=Constant.URL_PERSON;
			Intent intent=new Intent(Setting.this,CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "员工自助", true);
		}
	};
	OnClickListener onMynewsSetting=new OnClickListener() {
		public void onClick(View v) {
			Log.i("test","点击我的消息");
			Intent intent = new Intent(Setting.this, NewsList.class);
			intent.putExtra(MessageKey.NEWS_TYPE, "PushNews");
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "我的消息", true);
		}
	};
	//阅读历史
		OnClickListener onHistoryClickListener=new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(Setting.this,ReadHistory.class);
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigate(intent, "阅读历史", true);
			}
		};
	//修改密码
	OnClickListener onPasswordSetting=new OnClickListener() {
		public void onClick(View v) {
			String url=Constant.URL_PASSWORD;
			Intent intent=new Intent(Setting.this,CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "修改密码", true);
		}
	};
	//关于页面
	OnClickListener onHelpClickListener=new OnClickListener(){
		public void onClick(View v) {
//			String helpUrl=Constant.URL_HELP;
//			String helpUrl="http://221.195.72.41:81/help/help1.html";
//			Intent intent = new Intent(Setting.this, CondovaActivity.class);
//			intent.putExtra(MessageKey.URL, helpUrl);
//			intent.putExtra(MessageKey.REUSEABLE, false);
//			navigate(intent, "帮助", true);
			Intent intent=new Intent(Setting.this,AboutHelpActivity.class);
			navigate(intent, "关于", true);
		}
		
	};
	//字体放大
	OnClickListener onTextSize=new OnClickListener() {
		public void onClick(View v) {
			Intent intent=new Intent(Setting.this,TextSetting.class);
			navigate(intent, "字体设置", true);
		}
	};
	//意见反馈
	OnClickListener onSuggestion=new OnClickListener() {
		public void onClick(View v) {
			Intent intent=new Intent(Setting.this,Suggestion.class);
			navigate(intent, "意见反馈", true);
		}
	};
	OnClickListener onLogoutClickListener = new OnClickListener(){
		public void onClick(View v) {
//			((HseApplication)getApplicationContext()).setLoginUserId("");
//			((HseApplication)getApplicationContext()).setIsLogged(false);
//			Intent intent = new Intent(Setting.this, Login.class);
//			intent.putExtra(MessageKey.LOGOUT, true);
//			startActivity(intent);
//			finish();
			myFinish();
		}
	};
	private void myFinish(){
		new AlertDialog.Builder(this).setTitle("退出移动办公平台")
		.setMessage("退出后，你将接收不到新的消息")
		.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				return;
				
			}
		}).setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((HseApplication)getApplicationContext()).setIsLogged(false);
				JPushInterface.stopPush(Setting.this);
				System.exit(0);
			}
		}).show();
	}
	OnCheckedChangeListener onEnablePushChangeListener = new  OnCheckedChangeListener(){
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putBoolean("enable_push", isChecked);
			editor.commit();
			
			if(isChecked){
				JPushInterface.resumePush(Setting.this);
			}else{
				JPushInterface.stopPush(Setting.this);
			}
		}
	};
	
	OnCheckedChangeListener onCheckboxChangeListener = new  OnCheckedChangeListener(){
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			
			String key = "";
			if(buttonView.getId() == R.id.chk_setting_autologin){
				key = MessageKey.CONFIG_AUTOLOGIN;
			}else if(buttonView.getId() == R.id.chk_setting_savepassword){
				key = MessageKey.CONFIG_SAVEPASSWORD;
			}
			editor.putBoolean(key, isChecked);
			editor.commit();
		}
	};
	//修改密码
		OnClickListener cachedelete=new OnClickListener() {
			public void onClick(View v) {
				 new AlertDialog.Builder(ctx)
				.setTitle("温馨提示")
				.setMessage("确定要清除所有缓存？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
						DeleteCache();
						Toast.makeText(ctx, "所有缓存已经清除!", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();
			}
		};
		public void DeleteCache(){
			
		}
}
