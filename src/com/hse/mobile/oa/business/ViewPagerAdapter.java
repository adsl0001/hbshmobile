package com.hse.mobile.oa.business;

import java.util.List;

import com.hbsh.beta.R;
import com.hse.mobile.oapub.Login;
import com.hse.mobile.oapub.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ViewPagerAdapter extends PagerAdapter {
	private List<View> views;
	private Activity activity;
	
	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}
	
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}
	
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1) {
			Button mStartWeiboImageButton = (Button) arg0
					.findViewById(R.id.guide_button);
			mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 设置已经引导
					goHome();

				}

			});
		}
		return views.get(arg1);
	}
	private void goHome() {
		// 跳转
		Intent intent = new Intent(activity, Main.class);
		activity.startActivity(intent);
		activity.finish();
	}
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}
	public void finishUpdate(View arg0) {
	}
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	public Parcelable saveState() {
		return null;
	}

	public void startUpdate(View arg0) {
	}
}
