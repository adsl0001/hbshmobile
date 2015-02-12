package com.hse.mobile.oa.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;


/**
 * @ClassName PageGallery
 * @Description: 一次只滑动一页的Gallery
 * @author Administrator
 * @date 2013-10-25
 */
public class PageGallery extends Gallery {

	public PageGallery(Context context) {
		super(context);
	}

	public PageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PageGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
}
