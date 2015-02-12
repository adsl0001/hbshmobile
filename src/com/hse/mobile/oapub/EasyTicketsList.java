package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.entity.NewsListData;
import com.hse.mobile.oa.entity.NewsListItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 随手拍列表页(已废弃)
 */
public class EasyTicketsList extends BaseActivity {
	TextView btnMore;
	ListView listView;
	List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
	String category;
	int	   pageIndex = 0;
	int    newsTotal = 0;
	static final int PAGE_SIZE = 10;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		category = this.getIntent().getStringExtra(MessageKey.TICKETS_TYPE);
				
		this.setContentView(R.layout.newslist);
		
		listView = (ListView)findViewById(R.id.listview_newslist);  
		listView.setOnItemClickListener(onListItemClickListener);
		btnMore = (TextView)this.findViewById(R.id.btn_newlist_more);
		btnMore.setVisibility(View.GONE);
		btnMore.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				loadMore();
			}
		});
		
		loadNewsList(category);
		this.setTitleBarRightButton("增加", onAddClickListener);
	}
	
	private void showTicketDetail(HashMap<String, Object> ticket){
		Intent intent = new Intent(this, IllegalTicket.class);
		Bundle bundle = this.getIntent().getExtras();
		bundle.putString("uniqueId", ticket.get("uniqueId").toString());
		bundle.putBoolean(MessageKey.REUSEABLE, false);
		intent.putExtras(bundle);
		
		String title = category.equals(Constant.TICKET_ILLEGAL) ? "修改罚款单" : "修改正激励";
		navigate(intent, title, true);
	}
	
	OnClickListener onAddClickListener = new OnClickListener(){
		public void onClick(View v) {
			Intent intent = new Intent(EasyTicketsList.this, IllegalTicket.class);
			Bundle bundle = getIntent().getExtras();
			bundle.putBoolean(MessageKey.REUSEABLE, false);
			intent.putExtras(bundle);
			String title = category.equals(Constant.TICKET_ILLEGAL) ? "新增罚款单" : "新增正激励";
			navigate(intent, title, true);		
		}
	};
	
	OnItemClickListener onListItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			showTicketDetail(listData.get(rowIndex));
		}
	};

	
	public boolean hasMore(){
		return (this.listData.size() < this.newsTotal);
	}
	
	private OnClickListener OnClickListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 装载更多
	 */
	public void loadMore(){
		if(hasMore()){
			loadNewsList(category);
		}
	}
	
	/*
	 * 装载新闻列表
	 */
	public void loadNewsList(String category){
		showProgressDialog("", "正在加载中...");
		String url = String.format("%s%s?pageSize=%d&page=%d"
				, Constant.URL_EASYTICKETS_LIST
				, category
				, PAGE_SIZE
				, (int)Math.ceil(listData.size()*1.0 / PAGE_SIZE)
				);
		new RestService().execute(url);
	}
	
	@Override
	public void onRestServiceResult(JSONObject result) {
		this.hideProgressDialog();
		
		try
		{
			if(result != null){
				HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
				newsTotal = Integer.parseInt(resultData.get("total").toString());
				List<HashMap<String, Object>> data = JsonDataConvert.parseArray((JSONArray)resultData.get("simpleticket"));
				for(HashMap<String, Object> item : data){
					String type = item.get("type").toString();
					String amount = item.get("amount").toString();
					String ticketDate = item.get("ticketDate").toString();
					item.put("money", "￥" + amount);
					item.put("content", type + " | " + ticketDate);			
				}
				this.listData.addAll(data);
				
				Log.i("HseOA", String.format("Total=%d, show=%d", newsTotal, listData.size()));
				
				SimpleAdapter adapter = new SimpleAdapter(this
						, listData
						, R.layout.ticket_list_item
						,new String []{"ticketName","content", "money"}
						,new int []{R.id.list_item_title,R.id.list_item_content, R.id.ticket_list_item_money
					});  
				listView.setAdapter(adapter);
				
				this.btnMore.setVisibility(this.hasMore()? View.VISIBLE : View.GONE);
			}else{
				Toast.makeText(this, "加载新闻失败!", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception ex){
			Log.e("HseMobileOA", "onRestServiceResult", ex);
		}
	}
}
