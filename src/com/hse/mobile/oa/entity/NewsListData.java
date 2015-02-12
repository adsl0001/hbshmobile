package com.hse.mobile.oa.entity;

import java.util.HashMap;
import java.util.List;

/*
 * 新闻列表数据类
 */
public class NewsListData {
	private int total = 0;
	
	private List<NewsListItem>  lists;
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public List<NewsListItem> getLists() {
		return lists;
	}

	public void setLists(List<NewsListItem> lists) {
		this.lists = lists;
	}
}
