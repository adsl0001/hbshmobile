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
	 * ���ò�����
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec); 
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��ȡGridView��Ӧ��Adapter
		ListAdapter listAdapter = this.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int rows;
		int columns = 0;
		int horizontalBorderHeight = 55;

		columns = 4;
		// �ж�������������ÿ�и����Ƿ��������������������ж��࣬��Ҫ��һ��
		if (listAdapter.getCount() % columns > 0) {
			rows = listAdapter.getCount() / columns + 1;
		} else {
			rows = listAdapter.getCount() / columns;
		}
		int totalHeight = 0;
	
		for (int i = 0; i < rows; i++) { // ֻ����ÿ��߶�*����
			int tempHeight = 0;
			int colcount = Math.min(columns,listAdapter.getCount()-i*columns );
			for (int j = 0; j < colcount; j++) {
				View listItem = listAdapter.getView(i*columns+j, null, this);
				listItem.measure(0, 0); // ��������View �Ŀ��
				tempHeight = Math.max(listItem.getMeasuredHeight(), tempHeight);
			}
			totalHeight += tempHeight; // ͳ������������ܸ߶�
		}
		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = totalHeight + horizontalBorderHeight * (rows  );// �����Ϸָ����ܸ߶�
		setMeasuredDimension(widthMeasureSpec, params.height);
//		// this.setLayoutParams(params);
	}

}