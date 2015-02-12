package com.hse.mobile.oa.util;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/*
 * 图片工具
 */
public class BitmapUtil {
	
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
	
	/*
	 * 获取位图
	 */
	public static Bitmap getBitmap(Context c, int resId, int width, int height){
		//获取图片信息
		Bitmap bmp;
    	BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(c.getResources(), resId, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, width * height);
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeResource(c.getResources(), resId, opts);
		return bmp;
	}
	
	public static Bitmap getBitmap(String filePath, int width, int height){
		//获取图片信息
		Bitmap bmp;
    	BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath,opts);

		opts.inSampleSize = computeSampleSize(opts, -1, width * height);
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(filePath,opts);
		return bmp;
	}
	
	public static Bitmap getBitmap(InputStream is, int width, int height){
		Bitmap bmp;
		Rect outPadding = new Rect();
    	BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, outPadding, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, width * height);
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeStream(is, outPadding, opts);
		return bmp;
	}
	
	private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    } 
}
