package com.ziteng.cn.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.DBAdapter;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.entity.NewsListItem;
import com.hse.mobile.oapub.BaseActivity;
import com.hse.mobile.oapub.NewsDetail;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ReadHistory extends BaseActivity {
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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_history);
		ctx=this;
		db=new DBAdapter(this);
		listView = (ListView)findViewById(R.id.readlist);  
		listView.setOnItemClickListener(onListItemClickListener);
		//listView.setOnItemSelectedListener(onListItemSelectListener);
		loadNewsList();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.read_history, menu);
		return true;
	}
	OnItemClickListener onListItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			showNewsDetail(listData.get(rowIndex));
		}
	};
	private void showNewsDetail(NewsListItem newsItem){
		Intent intent = new Intent(this, NewsDetail.class);
		Bundle bundle = this.getIntent().getExtras();
		newsType=newsItem.get("newstype").toString();
		if(newsType.equals("PushNews")){
			bundle.putString(MessageKey.ISPUSHNEWS, "ok");
		}
		else{
			bundle.putString(MessageKey.ISPUSHNEWS, "no");
		}
		bundle.putString(MessageKey.NEWS_TYPE, newsType);
		bundle.putString(MessageKey.NEWS_ID, newsItem.getId());
		bundle.putBoolean(MessageKey.REUSEABLE, false);
		intent.putExtras(bundle);
		navigate(intent, "", true);
	}
	//家在新闻
	private void loadNewsList(){
		db.open();
		Cursor c=db.getAllContacts();
		newsTotal=c.getCount();
		Log.i("test", newsTotal+"条数据");
		 if (c.moveToFirst()) 
	      { 
			 do { 
				 NewsListItem item=new NewsListItem();
				 item.put("id", c.getString(1));
				 item.put("newstype", c.getString(2));
				 item.put("summary", c.getString(4));
				 item.put("title", c.getString(3));
				 Log.i("test", "阅读历史："+"id"+c.getString(0)+"/n newsid"+c.getString(1)+"/n newstype"+c.getString(2));
				 this.listData.add(item);
			 }while (c.moveToNext()); 
	       } 
	      db.close();
	      if(adapter == null)
			{
				adapter = new SimpleAdapter(this
						, listData
						, R.layout.list_item
						,new String []{"title","summary"}
						,new int []{R.id.list_item_title,R.id.list_item_content
				});  
				listView.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
			
	}
}
