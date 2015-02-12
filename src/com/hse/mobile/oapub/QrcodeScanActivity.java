package com.hse.mobile.oapub;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hse.mobile.camera.CameraManager;
import com.hse.mobile.camera.CaptureActivityHandler;
import com.hse.mobile.camera.ViewfinderView;
import com.hse.mobile.camera.InactivityTimer;
import com.hbsh.beta.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class QrcodeScanActivity extends BaseActivity implements Callback {
	private CaptureActivityHandler handler;
	private ViewfinderView viewfindView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode_scan);
		
		CameraManager.init(getApplication());
		viewfindView=(ViewfinderView)findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		vibrate = true;
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * 处理扫描结果
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		Log.i("test", "扫描结果"+resultString);
		if (resultString.equals("")) {
		}
		else if(resultString.contains("DN-")){
			Intent intent=new Intent(QrcodeScanActivity.this,QrResult.class);
			Bundle bundle=this.getIntent().getExtras();
			bundle.putString("qrcode", resultString);
			intent.putExtras(bundle);
			navigate(intent, "设备详细信息", true);
		}
		else {
			//跳转页面进入显示资产页面
			showResult("扫描成功", "扫描结果"+resultString);
//			showResult("扫描成功", resultString);
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("test", "surfaceDestroyed");
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfindView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfindView.drawViewfinder();

	}


	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	/**
	 * 显示扫描结果
	 */
	private void showResult(String title,String result){
		Dialog alert=new AlertDialog.Builder(QrcodeScanActivity.this)
						.setTitle(title)
						.setMessage(result)
						.setNeutralButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								restartCapture();
							}
						})
						.create();
		alert.show();
	}
	/**
	 * 连续扫描
	 */
	private void restartCapture(){
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		initCamera(surfaceHolder);
		if(handler!=null);
		handler.restartPreviewAndDecode();
		
	}
}
