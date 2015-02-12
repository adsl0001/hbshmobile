package com.hse.mobile.oapub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.HandleMyTask;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.MessageType;
import com.hse.mobile.oa.business.MyBroadCast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 主框架界面
 */
public class Main extends ActivityGroup  {
	private RadioButton buttonHome, buttonPortal, buttonmobile, buttonSetting;
	private Button		btnBack, btnTitleRight;
	private ViewGroup	layContent;
	private TextView	txtTitle;
	private ViewGroup	layHead;
	private ViewGroup   Tab_Menu;
	private String themecolor;
	static int acvitityId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.comm_baseactivity);
		//初始化按钮
		buttonHome = (RadioButton)this.findViewById(R.id.button_home);
		buttonHome.setOnClickListener(tabItemClickListener);
		buttonPortal = (RadioButton)this.findViewById(R.id.button_portal);
		buttonPortal.setOnClickListener(tabItemClickListener);
		buttonmobile=(RadioButton)this.findViewById(R.id.button_mobile);
		buttonmobile.setOnClickListener(tabItemClickListener);
		/**
		 * 合并按钮
		 * buttonReport = (RadioButton)this.findViewById(R.id.button_report);
		buttonReport.setOnClickListener(tabItemClickListener);
		buttonTickets = (RadioButton)this.findViewById(R.id.button_tickets);
		buttonTickets.setOnClickListener(tabItemClickListener);
		*/
		buttonSetting = (RadioButton)this.findViewById(R.id.button_setting);
		buttonSetting.setOnClickListener(tabItemClickListener);
		//底部按钮栏
		Tab_Menu=(ViewGroup)this.findViewById(R.id.comm_menubar);
		//顶部标题栏
		layHead = (ViewGroup)this.findViewById(R.id.comm_base_header); 
		layContent = (ViewGroup)this.findViewById(R.id.layContent); 
		
		txtTitle = (TextView)this.findViewById(R.id.txtTitle); 
		btnBack = (Button)this.findViewById(R.id.btnBack); 
		btnBack.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onBack();
			}
		});
		btnTitleRight = (Button)this.findViewById(R.id.btn_title_right); 
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
		themecolor=sp.getString("ThemeColor", "red");
		switchTheme(themecolor);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("changethetheme");
		this.registerReceiver(ThemeChagne, intentFilter);
		((HseApplication)this.getApplication()).setFrameworkMessageHandler(handler);
		
		onLoad();
		
		if(this.getIntent().hasExtra(JPushInterface.EXTRA_EXTRA)){
			processCustomMessage(this, this.getIntent().getExtras());
		}
		//registerMessageReceiver(); 
	}
	/*
	 * 菜单按钮
	 */
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST+1, 0, "退出").setIcon(R.drawable.back);
		return true;
	}
	//点击了菜单按钮中的值
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case Menu.FIRST+1:
			//moveTaskToBack(true);
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
					stoppush();
					System.exit(0);
				}
			}).show();
			break;
		default:
			break;
		}
		return false;
	}
	/*
	 * 停止推送
	 */
	public void stoppush(){
		((HseApplication)this.getApplication()).stoppush();
	}
	/*
	 * 系统BACK键处理
	 */
	public void onBackPressed() {
		if(((HseApplication)this.getApplication()).goBack()){
			((HseApplication)this.getApplication()).mygoBack();
		}
		else{
			moveTaskToBack(true);
		}
		//onBack();
		// moveTaskToBack(true);
	}
	
	/*
	 * 内容页里面的Activity无法接收到onTouchEvent, 需要在框架页里面处理
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		GestureDetector gestureDetector = ((HseApplication)this.getApplicationContext()).getGestureDetector();
		if(gestureDetector!=null){
			return gestureDetector.onTouchEvent(event);
		}else return super.onTouchEvent(event);
	}
	
	private void onLoad(){
		//启动进入代办列表
//		buttonHome.setChecked(true);
//		navigateRoot("公司首页", new Intent(Main.this, Home.class));
		buttonHome.setChecked(true);
		navigateRoot("公司首页", new Intent(Main.this, Home.class));
	}
	
	private void onBack(){
		((HseApplication)this.getApplication()).navigateBack();
	}
	
	@SuppressWarnings("deprecation")
	protected void onPause() {
		super.onPause();
		((HseApplication)this.getApplication()).setIsInBackground(true);
	}
	
	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();
		((HseApplication)this.getApplication()).setIsInBackground(false);
	}
	
	@SuppressWarnings("deprecation")
	protected void onDestroy() {
		((HseApplication)this.getApplication()).setFrameworkMessageHandler(null);
		//unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	};
	
	/*
	 * 消息处理器
	 */
	Handler handler = new Handler(new Handler.Callback(){
		
        public boolean handleMessage(Message msg){
        	switch(msg.what){
        	case MessageType.NAVIGATE:
        		Intent intent = (Intent)msg.obj;
        		boolean showBack = msg.getData().getBoolean(MessageKey.SHOW_TITLE_BACK);
        		boolean showRight = msg.getData().getBoolean(MessageKey.SHOW_TITLE_RIGHT);
        		String title = msg.getData().getString(MessageKey.TITLE);
        		navigate(intent, title, showBack, showRight);
        		break;
        	case MessageType.SHOW_HEAD:
        		intent = (Intent)msg.obj;
        		boolean showHead = msg.getData().getBoolean(MessageKey.SHOW_TITLE);
        		showHead(showHead);
        		break;
        	case MessageType.SHOW_HEAD_RIGHTBUTTON:
        		title = msg.getData().getString(MessageKey.TITLE);
        		OnClickListener listener = (OnClickListener)msg.obj;
        		showHeadRightButton(title, listener);
        		break;
        	} 
        	
        	return true;
        }
	});
	
	/*
	 * tab 按钮事件处理
	 */
	OnClickListener tabItemClickListener = new OnClickListener(){
		public void onClick(View v) {
			RadioButton[] tabButtons = {buttonHome, buttonPortal,buttonmobile, buttonSetting};
			for(RadioButton button:tabButtons){
				if(v != button){
					button.setChecked(false);
				}
			}
			
			if(v.getId() == R.id.button_home){
				set("portal");
				navigateRoot("公司首页", new Intent(Main.this, Home.class));
			}else if(v.getId() == R.id.button_portal){
				set("integration");
				SwitchMytask();
			}
			else if(v.getId()==R.id.button_mobile){
				//合并后的移动应用按钮
				Log.i("test", "进入移动应用");
				navigateRoot("移动应用", new Intent(Main.this, NewMobileActivity.class));
			}
			/*
			 * 
			 
			else if(v.getId() == R.id.button_report){
				set("report");
				navigateRoot("报表", new Intent(Main.this,Report.class));
			}
			else if(v.getId() == R.id.button_tickets){
				set("suishoupai");
				Intent intent = new Intent(Main.this, TakePhoto.class);
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigateRoot("工程施工随手拍", intent);
			}
			*/
			else if(v.getId() == R.id.button_setting){
				set("setup");
				Intent intent = new Intent(Main.this, Setting.class);
				navigateRoot("设置", intent);
			}
		}
	};
	
	private void navigateRoot(String title, Intent intent){
		((HseApplication)this.getApplication()).navigateRoot(intent, title);
		showHead(true);
	}
	
	@SuppressWarnings("deprecation")
	private void navigate(Intent intent, String title, boolean showBack, boolean showRight){

		
		layContent.removeAllViews();
		String activityName = intent.getComponent().getClassName();
		Log.i("navigate", activityName);
		
		Window window = null;
		boolean useOld = false;
		if(getLocalActivityManager().getActivity(activityName) == null){
			window = getLocalActivityManager().startActivity(activityName
					, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}else{
			Activity activity = getLocalActivityManager().getActivity(activityName);

			if(intent.hasExtra(MessageKey.REUSEABLE) 
					&& !intent.getBooleanExtra(MessageKey.REUSEABLE, true)){
				//禁止重用
				//activity.recreate();
				//activity.finish();

				activityName += acvitityId;
				acvitityId++;
				window = getLocalActivityManager().startActivity(activityName
						, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}else{
				window = activity.getWindow();	
				useOld = true;
			}
		}
		
		layContent.addView(window.getDecorView()
                , new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
		);
		if(useOld){
			Activity activity = getLocalActivityManager().getActivity(activityName);
			if(activity instanceof BaseActivity){
				((BaseActivity)activity).onResumeFromExist();
			}
		}
		/*
		layContent.addView(getLocalActivityManager().startActivity(
				"",
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .getDecorView());
                */
		setTitle(title);
		showBack(showBack);
		showHeadRightButton("", null);
	}
	private void SwitchMytask(){
		new getHandleTask().execute("account");
	}
	private void setTitle(String title){
		if(title != null && !title.isEmpty()){
			txtTitle.setText(title);
		}
	}
	
	private void showBack(boolean showBack){
		btnBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
	}
	
	private void showHead(boolean showHead){
		layHead.setVisibility(showHead ? View.VISIBLE : View.GONE);
	}
	
	@SuppressLint("NewApi")
	private void showHeadRightButton(String title, OnClickListener listener){
		if(title.equals("天气")){
			this.btnTitleRight.setText(" ");
			this.btnTitleRight.setBackgroundResource(R.drawable.weather);
		}
		else{
			this.btnTitleRight.setText(title);
			this.btnTitleRight.setBackgroundResource(R.drawable.touming);
			if(themecolor.equals("blue")){
				btnTitleRight.setBackgroundResource(R.drawable.btn_blue_bg);
			}
			else if(themecolor.endsWith("green")){
				btnTitleRight.setBackgroundResource(R.drawable.btn_green_bg);
			}
			else if(themecolor.equals("red")){
				btnTitleRight.setBackgroundResource(R.drawable.btn_red_bg);
			}
			
		}
		this.btnTitleRight.setOnClickListener(listener);
		
		if(title==null || title.isEmpty()){
			this.btnTitleRight.setVisibility(View.GONE);
		}else{
			this.btnTitleRight.setVisibility(View.VISIBLE);
		}
	}
	
	//for receive customer msg from jpush server
	/*
		private MessageReceiver mMessageReceiver;
		public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
		public static final String KEY_TITLE = "title";
		public static final String KEY_MESSAGE = "message";
		public static final String KEY_EXTRAS = "extras";
		
		public void registerMessageReceiver() {
			mMessageReceiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
			filter.addAction(MESSAGE_RECEIVED_ACTION);
			registerReceiver(mMessageReceiver, filter);
		}

		public class MessageReceiver extends BroadcastReceiver {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {	              
	              processCustomMessage(intent.getExtras());
				}
			}
		}
		*/
		
		/*
		 * 处理推送消息
		 */
		public static void processCustomMessage(Context context, Bundle bundle) {
			HseApplication application = ((HseApplication)context.getApplicationContext());
			if(application.getIsLogged() == false){
				return;
			}
			
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
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
				//通知推送{"cmd":"news","cat":"CompanyNotice","nid":"522198cda90f308370fcaea9cf4d7c81"}
				String category = extrasJson.optString("cat");
				String nid = extrasJson.optString("nid");
				Intent intent = new Intent(context, NewsDetail.class);
				intent.putExtra(MessageKey.ISPUSHNEWS, "no");
				intent.putExtra(MessageKey.NEWS_TYPE, category);
				intent.putExtra(MessageKey.NEWS_ID, nid);
				intent.putExtra(MessageKey.REUSEABLE, false);
				application.navigate(intent, "公司通知", true);
			}
			//要问推送
			else if(command.equals("importantnews")){
				//要闻推送{"cmd":"importantnews","cat":"CompanyNews","nid":"522198cda90f308370fcaea9cf4d7c81"}
				String category = extrasJson.optString("cat");
				String nid = extrasJson.optString("nid");
				Intent intent = new Intent(context, NewsDetail.class);
				intent.putExtra(MessageKey.ISPUSHNEWS, "no");
				intent.putExtra(MessageKey.NEWS_TYPE, category);
				intent.putExtra(MessageKey.NEWS_ID, nid);
				intent.putExtra(MessageKey.REUSEABLE, false);
				application.navigate(intent, "公司要闻", true);
				
			}
			//个人推送新闻
			else if(command.equals("PushNews")){
				//cat--PushNews
				Log.i("test","json:"+extrasJson);
				String category=extrasJson.optString("cat");
				String nid=extrasJson.optString("nid");
				Intent intent=new Intent(context,NewsDetail.class);
				intent.putExtra(MessageKey.ISPUSHNEWS, "ok");
				intent.putExtra(MessageKey.NEWS_TYPE, category);
				intent.putExtra(MessageKey.NEWS_ID, nid);
				intent.putExtra(MessageKey.REUSEABLE, false);
				application.navigate(intent, "我的消息", true);
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
						
						String keySrcName = key;
						if(key.equals("w")){
							keySrcName = "workEffortId";
						}
						if(key.equals("i")){
							keySrcName = "workflowPackageId";
						}
						if(key.equals("v")){
							keySrcName = "workflowPackageVersion";
						}
						if(key.equals("p")){
							keySrcName = "procWorkEffortId";
						}
						if(key.equals("u")){
							keySrcName = "partyId";
						}				
						
						query += String.format("%s=%s", keySrcName, extrasData.get(key).toString());
					}
				}
				url += query;
				
				//String url = extrasJson.optString("url");
				Log.i("task url:", url);
				if(url!=null && !url.isEmpty()){
					Intent intent = new Intent(context, CondovaActivity.class);
					intent.putExtra(MessageKey.URL, url);
					intent.putExtra(MessageKey.REUSEABLE, false);
					application.navigate(intent, "待办任务", false);
					//application.showTitlebar(false);
				}
			}
		}
		/*
		 * 未读推送计数
		 */
		public void setBadgeNumber(String newstype){
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			SharedPreferences sp=getSharedPreferences("HseOAConfig", 0);
			int badgeNumber=sp.getInt(newstype, 0);
			badgeNumber++;
			editor.putInt(newstype, badgeNumber);
			editor.commit();
		}
		private void set(String moduletype){
			Log.i("test", "开始统计次数");
			new AppClientUseServlet().execute(moduletype);
		}
		
		public class AppClientUseServlet extends AsyncTask<String, Integer, Boolean>{
			protected Boolean doInBackground(String... arg0) {
				if(arg0.length!=1){
					return null;
				}
				else{
					return setAppClientUseServlet(arg0[0]);
				}
			}
		}
		private Boolean setAppClientUseServlet(String moduletype){
			String account=Constant.User_name;
			String identify=Constant.User_imei;
			String url=Constant.URL_UseServlet+"?account="+account+"&identify="+identify+"&moduletype="+moduletype;
			HttpGet httpRequest=new HttpGet(url);
			try{
				 HttpClient httpclient=new DefaultHttpClient();
				 HttpResponse httpResponse=httpclient.execute(httpRequest);
			}
			catch(Exception e){
				Log.i("test", e.toString());
				return false;
			}
			return false;
		}
		//获取待办任务的参数
		public class getHandleTask extends AsyncTask<String, Integer, Boolean>{
			protected Boolean doInBackground(String... params) {
				try {
					return HandleMyTask.getMytask(params[0]);
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("test", e.toString());
				}
				return false;
			}
			protected void onPostExecute(Boolean result){
				Constant.setBackfromMyTask(false);
				navigateRoot("集成平台", new Intent(Main.this, Portal.class));
			}
			
		}
		private BroadcastReceiver ThemeChagne = new BroadcastReceiver(){
			 public void onReceive(Context context, Intent intent){
				Log.i("test", "主题的颜色是：");
				String param=intent.getStringExtra("themecolor");
				Log.i("test", "主题的颜色是："+param);
				switchTheme(param);
			}
		};
		public void switchTheme(String themecolor){
			if(themecolor.equals("blue")){
				Tab_Menu.setBackgroundResource(R.drawable.bg_tab_bar_home);				
				layHead.setBackgroundResource(R.drawable.common_title_bg_blue);
				
				buttonHome.setBackgroundResource(R.drawable.btn_tab_home_blue);
				buttonHome.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				buttonPortal.setBackgroundResource(R.drawable.btn_tab_portal_blue);
				buttonPortal.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				buttonmobile.setBackgroundResource(R.drawable.btn_tab_mobile_blue);
				buttonmobile.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				/*
				buttonReport.setBackgroundResource(R.drawable.btn_tab_report_blue);
				buttonReport.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				buttonTickets.setBackgroundResource(R.drawable.btn_tab_tickets_blue);
				buttonTickets.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				*/
				buttonSetting.setBackgroundResource(R.drawable.btn_tab_setting_blue);
				buttonSetting.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_blue));
				btnTitleRight.setBackgroundResource(R.drawable.btn_blue_bg);
			}
			else if(themecolor.equals("red")){
			Tab_Menu.setBackgroundResource(R.drawable.bg_tab_bar_home);				
			layHead.setBackgroundResource(R.drawable.common_title_bg_red);
			
			buttonHome.setBackgroundResource(R.drawable.btn_tab_home_red);
			buttonHome.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			buttonPortal.setBackgroundResource(R.drawable.btn_tab_portal_red);
			buttonPortal.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			buttonmobile.setBackgroundResource(R.drawable.btn_tab_mobile_red);
			buttonmobile.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			/*
			buttonReport.setBackgroundResource(R.drawable.btn_tab_report_red);
			buttonReport.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			buttonTickets.setBackgroundResource(R.drawable.btn_tab_tickets_red);
			buttonTickets.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			*/
			buttonSetting.setBackgroundResource(R.drawable.btn_tab_setting_red);
			buttonSetting.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_red));
			
			btnTitleRight.setBackgroundResource(R.drawable.btn_red_bg);
			}
			else if(themecolor.equals("green")){
				Tab_Menu.setBackgroundResource(R.drawable.bg_tab_bar_home);				
				layHead.setBackgroundResource(R.drawable.common_title_bg_green);
				
				buttonHome.setBackgroundResource(R.drawable.btn_tab_home_green);
				buttonHome.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				buttonPortal.setBackgroundResource(R.drawable.btn_tab_portal_green);
				buttonPortal.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				buttonmobile.setBackgroundResource(R.drawable.btn_tab_mobile_green);
				buttonmobile.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				/*
				buttonReport.setBackgroundResource(R.drawable.btn_tab_report_green);
				buttonReport.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				buttonTickets.setBackgroundResource(R.drawable.btn_tab_tickets_green);
				buttonTickets.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				*/
				buttonSetting.setBackgroundResource(R.drawable.btn_tab_setting_green);
				buttonSetting.setTextColor(getResources().getColorStateList(R.color.menu_tab_textcolor_green));
				btnTitleRight.setBackgroundResource(R.drawable.btn_green_bg);
			}
			
		}
}	
