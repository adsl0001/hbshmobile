package com.hse.mobile.oa.entity;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * 新闻条目类
 */
public class NewsListItem extends HashMap<String, Object>{
	public String getPersoncode(){
		return (String)super.get("personcode");
	}
	public void setPersonCode(String personcode){
		super.put("personcode", personcode);
	}
	public String getId() {
		return (String) super.get("id");
	}
	public void setId(String id) {
		super.put("id", id);
	}
	public String getpushtime(){
		return (String)super.get("pushtime");
	}
	public void setpushtime(String pushtime){
		super.put("pushtime", pushtime);
	}
	public String getTitle() {
		return (String) super.get("title");
	}
	public void setTitle(String title) {
		super.put("title", title);
	}
	public String getContent() {
		return (String) super.get("content");
	}
	
	public String getDate() {
		return (String) super.get("date");
	}
	
	public void setContent(String content) {
		super.put("content", content);
	}
	public String getSummary() {
		return (String) super.get("summary");
	}
	public void setSummary(String summary) {
		super.put("summary", summary);
	}
	
	public String getUrl() {
		return (String) super.get("url");
	}
	public void setUrl(String url) {
		super.put("url", url);
	}
	public String getNewstype(){
		return (String)super.get("newstype");
	}
	public void setNewstype(String newstype){
		super.put("newstype", newstype);
	}
	public static NewsListItem parseJson(JSONObject json){
		NewsListItem item = new NewsListItem();
		JSONArray names = json.names();
		if(names != null){
			for(int i=0; i<names.length(); i++){
				String key;
				try {
					key = names.get(i).toString();
					item.put(key, json.get(key));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return item;
	}
}
