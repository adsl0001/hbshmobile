package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
//计数标签
import com.hse.mobile.oa.business.BadgeView;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.DBAdapter;
import com.hse.mobile.oa.business.GetBadgeNumberService;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.entity.NewsListData;
import com.hse.mobile.oa.entity.NewsListItem;
import com.hse.mobile.oa.ui.InternetGalleryAdapter;
import com.ziteng.cn.test.Weather;

import android.R.integer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 门户页
 */
public class Home extends BaseActivity {
	Gallery gallery;
	InternetGalleryAdapter adapter;
	TextView txtGalleryPage;
	List<NewsListItem> listData = new ArrayList<NewsListItem>();
	BadgeView badgeview1;
	BadgeView badgeview2;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	Button button8;
	DBAdapter db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.home);
		db=new DBAdapter(this);
		button1 = (Button)this.findViewById(R.id.button_CompanyNews);
		button1.setTag("CompanyNews");
		
		badgeview1=new BadgeView(this, button1);
		badgeview1.setTextSize(12);
		button1.setOnClickListener(newsItemClickListener);
		
		button2 = (Button)this.findViewById(R.id.button_CompanyNotice);
		badgeview2=new BadgeView(this, button2);
		badgeview2.setTextSize(12);
		button2.setTag("CompanyNotice");
		button2.setOnClickListener(newsItemClickListener);
		
		button3 = (Button)this.findViewById(R.id.button_BaseLevelDynamic);
		button3.setTag("BaseLevelDynamic");
		button3.setOnClickListener(newsItemClickListener);
		
		button4 = (Button)this.findViewById(R.id.button_EducationGarden);
		button4.setTag("EducationGarden");
		button4.setOnClickListener(newsItemClickListener);
		
		button5 = (Button)this.findViewById(R.id.button_LeadershipSpeech);
		button5.setTag("LeadershipSpeech");
		button5.setOnClickListener(newsItemClickListener);
		
		button6 = (Button)this.findViewById(R.id.button_SpecialReport);
		button6.setTag("SpecialReport");
		button6.setOnClickListener(newsItemClickListener);
		
		button7 = (Button)this.findViewById(R.id.button_SecurityNews);
		button7.setTag("SecurityNews");
		button7.setOnClickListener(newsItemClickListener);
		
		button8 = (Button)this.findViewById(R.id.button_SecurityColumn);
		button8.setTag("SecurityColumn");
		button8.setOnClickListener(newsItemClickListener);
		
		gallery = (Gallery)this.findViewById(R.id.gallery_photonews);
		gallery.setOnItemClickListener(onPhotoItemClickListener);
		gallery.setOnItemSelectedListener(onPhotoItemSelectListener);
		
		txtGalleryPage = (TextView)this.findViewById(R.id.txt_gallery_page);
		loadPhotoNews();
		
	}
	/*
	 * 在onResumeFromExist和onResume刷新页面重新计数
	 * 
	 */
	public void onResumeFromExist(){
		Log.i("test","111");
		super.setTitleBarRightButton("天气",onWeather);
		new getNumberTask().execute("123");
	}
	protected void  onResume() {
		Log.i("test","222");
		new getNumberTask().execute("123");
		super.setTitleBarRightButton("天气",onWeather);
		super.onResume();
	}
	private void showPhotoNewsDetail(NewsListItem newsItem){
		db.open();
		if(db.hasthenewsid(newsItem.getId())){
			
		}
		else{
			db.insertContact(newsItem.getId(), Constant.NEWS_TYPE_PHOTONEWS,newsItem.getTitle()," ");
		}
		db.close();
		Intent intent = new Intent(this, NewsDetail.class);
		Bundle bundle = this.getIntent().getExtras();
		bundle.putString(MessageKey.NEWS_TYPE, Constant.NEWS_TYPE_PHOTONEWS);
		bundle.putString(MessageKey.NEWS_ID, newsItem.getId());
		bundle.putBoolean(MessageKey.REUSEABLE, false);
		bundle.putString(MessageKey.ISPUSHNEWS, "no");
		intent.putExtras(bundle);
		navigate(intent, "焦点新闻", true);
	}
	
	/*
	 * 设置相册页信息
	 */
	private void setPhotoPageInfo(int pageIndex){
		txtGalleryPage.setText(String.format("%d/%d", pageIndex+1, listData.size()));
	}
	
	OnItemSelectedListener onPhotoItemSelectListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			setPhotoPageInfo(arg2);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
	
		}
	};
	
	OnItemClickListener onPhotoItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			showPhotoNewsDetail(listData.get(rowIndex));
		}
	};
	
	private void loadPhotoNews(){
		String url = Constant.URL_PHOTONEWS;
		new RestService().execute(url);
		
		//loadPhotos(new String[]{"1", "2", "3", "4", "5"});
	}
	
	private void loadPhotos(String[] imageUrls){
		 adapter = new InternetGalleryAdapter(this, imageUrls);   
		 gallery.setAdapter(adapter);  
	}
	
	@Override
	public void onRestServiceResult(JSONObject result) {
		if(result != null){
			NewsListData newsListData = JsonDataConvert.toNewsListData(result);
			listData = newsListData.getLists();
			int length = newsListData.getLists().size();
			String[] urls = new String[length];
			int index = 0;
			for(NewsListItem newsItem : newsListData.getLists()){
				urls[index++] = newsItem.getSummary();
			}
			loadPhotos(urls);
		}else{
			Toast.makeText(this, "加载焦点新闻失败!", Toast.LENGTH_SHORT).show();
		}
	}
	
	OnClickListener newsItemClickListener = new OnClickListener(){
		public void onClick(View v) {
			Button target = (Button)v;
			if(v.getTag().toString().equals("CompanyNews")){
				Log.i("test", "要闻");
				Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
				editor.putInt("CompanyNews", 0);
				editor.commit();
			}
			else if(v.getTag().toString().equals("CompanyNotice")){
				Log.i("test", "通知");
				Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
				editor.putInt("CompanyNotice", 0);
				editor.commit();
			}
			if(v.getTag().toString().equals("LeadershipSpeech")){
				Intent intent = new Intent(Home.this, NewsList_talk.class);
				intent.putExtra(MessageKey.NEWS_TYPE, v.getTag().toString());
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigate(intent, target.getText().toString(), true);
			}
			else{
				Intent intent = new Intent(Home.this, NewsList.class);
				intent.putExtra(MessageKey.NEWS_TYPE, v.getTag().toString());
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigate(intent, target.getText().toString(), true);
			}
		}
	};
	OnClickListener onWeather=new OnClickListener(){

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(Home.this, Weather.class);
			navigate(intent, "天气预报", true);
		}
		
	};
	//获取badgenumber 并显示
		public class getNumberTask extends AsyncTask<String, Integer, Boolean>{
			protected Boolean doInBackground(String... params) {
				try {
					return GetBadgeNumberService.getnumber(params[0]);
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("test", e.toString());
				}
				return false;
			}
			protected void onPostExecute(Boolean result){
				Log.i("test", "获取badgenumber:"+result);
				setBadgeNumber(result);
			}
		}
		public void setBadgeNumber(Boolean result) {
			if(result==null){
				return;
			}
			else if(result){
				String CompanyNewsNumber=Constant.getNewsnumber();
				String CompanyNoticeNumber=Constant.getNoticenumber();
				if(CompanyNewsNumber=="0"){
					badgeview1.hide();
				}
				else{
					badgeview1.setText(CompanyNewsNumber);
					badgeview1.show();
				}
				if(CompanyNoticeNumber=="0"){
					badgeview2.hide();
				}
				else{
					badgeview2.setText(CompanyNoticeNumber);
					badgeview2.show();
				}
			}
			else{
				return;
			}
		}
}
