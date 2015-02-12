package com.hse.mobile.oa.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * 自适应大小图片控件
 */
public class AspectRatioImageView extends ImageView {

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if(getDrawable()!= null && getDrawable().getIntrinsicWidth()>0){
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
            setMeasuredDimension(width, height);
    	}else{
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	}

    }
}
