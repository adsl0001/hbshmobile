package com.hse.mobile.oa.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @ClassName SimpleGroupAdapter
 * @Description:支持分组的适配器(有BUG,已弃用)
 *  bug: 上下滚动后，分组头显示混乱
 * @author 刘万芬
 * @date 2013-10-12
 */
public class SimpleGroupAdapter extends SimpleAdapter {
	private String mGroupHeaderMark;
	private int mRealDropDownResource;
	private int mGroupHeaderResource;
	private LayoutInflater mInflater; 
	private int[] mTo;
	
	/** 
	 * @Description: 分组适配器构造函数
	 * @param context
	 * @param data
	 * @param resource
	 * @param groupHeaderResource 分组头layout资源id
	 * @param from
	 * @param to
	 * @param groupHeaderMark 分组头标记字段 
	 */
	public SimpleGroupAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, int groupHeaderResource, String[] from,
			int[] to, String groupHeaderMark) {
		super(context, data, resource, from, to);
		mGroupHeaderMark = groupHeaderMark;
		mGroupHeaderResource = groupHeaderResource;
		mRealDropDownResource = resource;
		mTo = to;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	}
	
	@Override
	public void setDropDownViewResource(int resource) {
		mRealDropDownResource = resource;
		super.setDropDownViewResource(resource);
	}
	
	
	/**
	 * @Description:判断该项是不是分组头
	 *              如果该项数据中包含有分组头标记，返回true,否则为false
	 * @param position
	 * @return
	 * boolean
	 * @throws 
	 * @author 刘万芬
	 */
	public boolean isGroupHeaderItem(int position){
		Map  dataSet = (Map) this.getItem(position);
		if(dataSet != null && dataSet.containsKey(mGroupHeaderMark)){
			Log.i("GruopHeader", dataSet.get("name").toString());
			return true;
		}else {
			return false;
		}
	}
	
	@Override
    public boolean isEnabled(int position) {
         if(isGroupHeaderItem(position)){
             return false;
         }
         return super.isEnabled(position);
    }
	
	public View getGroupHeaderView(int position, View convertView, ViewGroup parent){

		 this.setDropDownViewResource(mGroupHeaderResource);
		 View view = super.getDropDownView(position, convertView, parent);
		 this.setDropDownViewResource(mRealDropDownResource);
	
		/*
		 View view = mInflater.inflate(mGroupHeaderResource, null);	 
		 TextView v = (TextView) view.findViewById(mTo[0]);
		 Map  dataSet = (Map) this.getItem(position);
		 String text = dataSet.get("name").toString();
		 this.setViewText(v, text);
		 */
		 return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {  
		  if(isGroupHeaderItem(position)){
			  return getGroupHeaderView(position, convertView, parent);
		  }
		  
		  return super.getView(position, convertView, parent);
	}

}
