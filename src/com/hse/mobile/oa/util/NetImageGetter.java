package com.hse.mobile.oa.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View.MeasureSpec;


/*
 * ÍøÂçÍ¼Æ¬»ñÈ¡Æ÷
 */
public class NetImageGetter implements Html.ImageGetter{
	private Handler handler;
	private int maxWidth;
	
	public NetImageGetter(Handler handler, int maxWidth){
		this.handler = handler;
		this.maxWidth = maxWidth;
	}
	
    public Drawable getDrawable(String url) {   
        Drawable drawable = null;  
        if(ImageCache.hasImage(url)){
        	drawable = new BitmapDrawable(ImageCache.getImage(url));
        	int width = MeasureSpec.getSize(maxWidth);
            int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, width, height);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
//                   drawable.getIntrinsicHeight()); 
        }else{
        	ImageCache.asyncDownloadImage(url, handler);
        }

        return drawable;  
    }  
}
