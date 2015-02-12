package com.hse.mobile.oa.ui;

import java.lang.reflect.Field;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class SubGridView extends GridView {
	public SubGridView(android.content.Context context,
			android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec); 
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获取GridView对应的Adapter
		ListAdapter listAdapter = this.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int rows;
		int columns = 0;
		int horizontalBorderHeight = 55;

		columns = 4;
		// 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
		if (listAdapter.getCount() % columns > 0) {
			rows = listAdapter.getCount() / columns + 1;
		} else {
			rows = listAdapter.getCount() / columns;
		}
		int totalHeight = 0;
	
		for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
			int tempHeight = 0;
			int colcount = Math.min(columns,listAdapter.getCount()-i*columns );
			for (int j = 0; j < colcount; j++) {
				View listItem = listAdapter.getView(i*columns+j, null, this);
				listItem.measure(0, 0); // 计算子项View 的宽高
				tempHeight = Math.max(listItem.getMeasuredHeight(), tempHeight);
			}
			totalHeight += tempHeight; // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = totalHeight + horizontalBorderHeight * (rows  );// 最后加上分割线总高度
		setMeasuredDimension(widthMeasureSpec, params.height);
//		// this.setLayoutParams(params);
	}

}