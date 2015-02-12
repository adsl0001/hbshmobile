package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.BadgeView;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.DBAdapter;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.entity.NewsListData;
import com.hse.mobile.oa.entity.NewsListItem;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 新闻列表页
 */
public class NewsList extends BaseActivity {
	TextView btnMore;
	ListView listView;
	List<NewsListItem> listData = new ArrayList<NewsListItem>();
	SimpleAdapter adapter = null;
	String newsType;
	int	   pageIndex = 0;
	int    newsTotal = 0;
	static final int PAGE_SIZE = 10;
	DBAdapter db;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		newsType = this.getIntent().getStringExtra(MessageKey.NEWS_TYPE);
		
		this.setContentView(R.layout.newslist);
		ctx=this;
		
		db=new DBAdapter(this);
		listView = (ListView)findViewById(R.id.listview_newslist);  
		listView.setOnItemClickListener(onListItemClickListener);
//		listView.setOnItemSelectedListener(onListItemSelectListener);
		btnMore = (TextView)this.findViewById(R.id.btn_newlist_more);
		btnMore.setVisibility(View.GONE);
		btnMore.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				loadMore();
			}
		});
		
	}
	protected void  onResume() {
		super.onResume();
		Log.i("test", "onResume");
		loadNewsList(newsType);
	}
	private void showNewsDetail(NewsListItem newsItem){
		db.open();
		if(db.hasthenewsid(newsItem.getId())){
			
		}
		else{
			db.insertContact(newsItem.getId(), newsType,newsItem.get("title").toString(),newsItem.get("summary").toString());
		}
		db.close();
		Intent intent = new Intent(this, NewsDetail.class);
		Bundle bundle = this.getIntent().getExtras();
		if(newsType.equals("PushNews")){
			bundle.putString(MessageKey.ISPUSHNEWS, "ok");
		}
		else{
			bundle.putString(MessageKey.ISPUSHNEWS, "no");
		}
		bundle.putString(MessageKey.NEWS_ID, newsItem.getId());
		bundle.putBoolean(MessageKey.REUSEABLE, false);
		intent.putExtras(bundle);
		navigate(intent, "", true);
	}
	
	/*
	OnItemSelectedListener  onListItemSelectListener = new OnItemSelectedListener (){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			showNewsDetail(listData.get(rowIndex));
		}

		public void onNothingSelected(AdapterView<?> arg0) {		
		}
	};*/
	
	OnItemClickListener onListItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			showNewsDetail(listData.get(rowIndex));
		}
	};

	
	public boolean hasMore(){
		return (this.listData.size() < this.newsTotal);
	}
	
	/*
	 * 装载更多
	 */
	public void loadMore(){
		if(hasMore()){
			loadNewsList(newsType);
		}
	}
	
	/*
	 * 装载新闻列表
	 */
	public void loadNewsList(String newsType){
		Log.i("test","开始创建列表");
		String url="";
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
		String account = sp.getString("userName", "");
//		Log.i("test","account="+account);
//		Log.i("test","listData.size()"+(int)Math.ceil(listData.size()*1.0 / PAGE_SIZE));
		showProgressDialog("", "正在加载中...");
		String personcode=Constant.getUser_name();
		//如果是PushNews需要加入公积金号参数
		if(newsType.equals("PushNews")){
			url = String.format("%s?pageSize=%d&page=%d&account=%s&msgType=PushNews"
					, Constant.URL_MyMessage
					, PAGE_SIZE
					,(int)Math.ceil(listData.size()*1.0 / PAGE_SIZE)
					,account
					);
		}
		else{
			url = String.format("%s%s?pageSize=%d&page=%d&personcode=%s"
					, Constant.URL_NEWSLIST
					, newsType
					, PAGE_SIZE
					, (int)Math.ceil(listData.size()*1.0 / PAGE_SIZE)
					,personcode
					);
		}
//		String url = String.format("%s%s?pageSize=%d&page=%d"
//				, Constant.URL_NEWSLIST
//				, newsType
//				, PAGE_SIZE
//				, (int)Math.ceil(listData.size()*1.0 / PAGE_SIZE)
//				);
		new RestService().execute(url);
	}
	
	@Override
	public void onRestServiceResult(JSONObject result) {
		this.hideProgressDialog();
		if(result != null){
			NewsListData newsListData = JsonDataConvert.toNewsListData(result);
			newsTotal = newsListData.getTotal();
			List<NewsListItem> newsList = newsListData.getLists();
			for(NewsListItem newsItem : newsList){
				Object obj = newsItem.get("summary");
				if(obj !=null && obj.getClass() == String.class){
					newsItem.put("summary", Html.fromHtml(obj.toString()));
				}
				obj = newsItem.get("title");
				if(obj !=null && obj.getClass() == String.class){
					newsItem.put("title", Html.fromHtml(obj.toString()));
				}
			}
			this.listData.addAll(newsList);
//			if(adapter == null)
//			{
//				adapter = new SimpleAdapter(this
//						, listData
//						, R.layout.list_item
//						,new String []{"title","summary"}
//						,new int []{R.id.list_item_title,R.id.list_item_content
//					});  
//				listView.setAdapter(adapter);
//			}else{
//				adapter.notifyDataSetChanged();
//			}
			listView.setAdapter(new BadgeAdapter(this,listData.size()));
			this.btnMore.setVisibility(this.hasMore()? View.VISIBLE : View.GONE);
		}else{
			Toast.makeText(this, "加载新闻失败!", Toast.LENGTH_SHORT).show();
		}
	}
	private class BadgeAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private Context context;
		private int length;
		public BadgeAdapter(Context context, int length){
			mInflater=LayoutInflater.from(context);
			this.context=context;
			this.length=length;
		}
		public int getCount() {
			return length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.list_item, null);
				holder=new ViewHolder();
				holder.titleview=(TextView)convertView.findViewById(R.id.list_item_title);
				holder.contentview=(TextView)convertView.findViewById(R.id.list_item_content);
				holder.badge=new BadgeView(context,holder.contentview);
				convertView.setTag(holder);
			}
			else{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.titleview.setText(listData.get(position).get("title").toString());
			holder.contentview.setText(listData.get(position).get("summary").toString());
			if(newsType.equals("CompanyNotice")||newsType.equals("CompanyNews")){
				if(listData.get(position).get("status").toString().equals("0")){
					holder.badge.setText("new");
					holder.badge.show();
				}
				else{
					holder.badge.hide();
				}
			}
			return convertView;
		}
		
	}
	static class ViewHolder{
		TextView titleview;
		TextView contentview;
		BadgeView badge;
	}
}
