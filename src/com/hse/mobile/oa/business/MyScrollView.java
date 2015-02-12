package com.hse.mobile.oa.business;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

//为了实现流畅的缩放
public class MyScrollView extends ScrollView {
	public MyScrollView(Context context){
		super(context);
	}
	public MyScrollView(Context context,AttributeSet attrs){
		super(context,attrs);
	}
	public MyScrollView(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
	}
	public boolean onInterceptTouchEvent(MotionEvent event){
		super.onInterceptTouchEvent(event);
		return false;
	}
	public boolean onTuchEvent(MotionEvent event){
		super.onTouchEvent(event);
		return true;
	}
}
