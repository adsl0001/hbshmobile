package com.hse.mobile.oa.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hse.mobile.oa.entity.NewsListData;
import com.hse.mobile.oa.entity.NewsListItem;

/*
 * JSON Êý¾Ý×ª»»
 */
public class JsonDataConvert {
	
	public static HashMap<String, Object> parseObject(JSONObject json){
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONArray names = json.names();
		if(names != null){
			for(int i=0; i<names.length(); i++){
				String key;
				try {
					key = names.get(i).toString();
					map.put(key, json.get(key));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	public static List<HashMap<String, Object>> parseArray(JSONArray json){
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for(int i=0; i<json.length(); i++){
			try {
				list.add(parseObject(json.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static NewsListData toNewsListData(JSONObject json){
		NewsListData newsListData = new NewsListData();
		if(json != null){
			try {
				newsListData.setTotal(json.getInt("total"));
				JSONArray lists = json.getJSONArray("lists");
				
				List<NewsListItem> itemList = new ArrayList<NewsListItem>();
				newsListData.setLists(itemList);
				
				for(int i=0; i<lists.length(); i++){
					JSONObject item = lists.getJSONObject(i);
					NewsListItem newsItem = NewsListItem.parseJson(item);
					itemList.add(newsItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}

		return newsListData;
	}

}
