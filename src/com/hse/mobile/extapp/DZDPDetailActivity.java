package com.hse.mobile.extapp;

import java.net.URL;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.util.ImageCache;
import com.hse.mobile.oa.util.NetImageGetter;
import com.hse.mobile.oapub.BaseActivity;

/**
 * ���ڵ�����Ϣ����
 * 
 * @author dbz
 *
 */
public class DZDPDetailActivity extends BaseActivity {
	TextView
	/** �̼� */
	txt_shangjia,
	/** ���� */
	txt_jieshao,
	/** �۸� */
	txt_jiage,
	/** ԭ�� */
	txt_yuanjia,
	/** ���� */
	txt_xiangqing,
	/**
	 * ��ַ
	 */
	txt_dizhi,
	/**
	 * �绰
	 */
	txt_dianhua;
	/**
	 * ����ͼ
	 */
	ImageView imageView;
	Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			initView();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("detail", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dianping_detail);
		initView();
	}

	private ImageGetter netImageGetter;
	Handler handler = new Handler(new Handler.Callback() {
		public boolean handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ImageCache.IMAGE_DOWNLOAD_FINISH:
				// ͼƬ������ɣ����¼���
				txt_xiangqing.setText(Html.fromHtml(
						(String) data.get("xiangqing"), netImageGetter, null));
				break;
			}
			return true;
		}
	});
	HashMap<String, Object> data;

	/**
	 * ��ʼ������
	 */
	private void initView() {

		txt_jiage = (TextView) this.findViewById(R.id.dianping_detail_jiage);
		txt_shangjia = (TextView) this
				.findViewById(R.id.dianping_detail_shangjia);
		txt_jieshao = (TextView) this
				.findViewById(R.id.dianping_detail_jieshao);
		txt_yuanjia = (TextView) this.findViewById(R.id.dianping_yuanjia);
		txt_yuanjia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		txt_xiangqing = (TextView) this.findViewById(R.id.dianping_xiangqing);
		txt_dizhi = (TextView) this.findViewById(R.id.dianping_dizhi);
		txt_dianhua = (TextView) this.findViewById(R.id.dianping_dianhua);
		imageView = (ImageView) this
				.findViewById(R.id.dianping_detail_imageView);
		data = (HashMap<String, Object>) this.getIntent().getSerializableExtra(
				MessageKey.DZDP_DETAIL);
		netImageGetter = new NetImageGetter(handler, this.getWindowManager()
				.getDefaultDisplay().getWidth()-20);

		txt_jiage.setText("��" + data.get("jiage"));
		txt_shangjia.setText((String) data.get("shangjia"));
		txt_jieshao.setText((String) data.get("jieshao"));
		txt_yuanjia.setText("��" + (String) data.get("yuanjia"));
		txt_xiangqing.setText(Html.fromHtml((String) data.get("xiangqing"),
				netImageGetter, null));
//		txt_xiangqing.setMovementMethod(LinkMovementMethod.getInstance());
		txt_dizhi.setText((String) data.get("dizhi"));
		txt_dianhua.setText((String) data.get("dianhua"));
		imageView.setImageDrawable((Drawable) data.get("img"));
	}

	class InitViewThread implements Runnable {

		@Override
		public void run() {
			Message msg = new Message();
			Bundle b = new Bundle();// �������
			b.putString("color", "�ҵ�");
			msg.setData(b);
			DZDPDetailActivity.this.h.sendMessage(msg);
		}

	}
}
