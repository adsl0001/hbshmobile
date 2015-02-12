package com.hse.mobile.oa.business;

import java.io.File;

import com.hbsh.beta.R;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadCast extends BroadcastReceiver {
		
	public void onReceive(Context context, Intent intent) {
		String param=intent.getStringExtra("file");
		if(param!=null);{
			Log.i("test","接收到下载地址："+param);
			File currentPath=new File(param);
			if(currentPath!=null&&currentPath.isFile())
	        {
	            String fileName = currentPath.toString();
	            Intent intent1;
	            try{
	            	if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingImage))){
	                    intent1 = OpenFiles.getImageFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingWebText))){
	                    intent1 = OpenFiles.getHtmlFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingPackage))){
	                    intent1 = OpenFiles.getApkFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingrar))){
	                    intent1 = OpenFiles.getRARFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingzip))){
	                    intent1 = OpenFiles.getZIPFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingAudio))){
	                    intent1 = OpenFiles.getAudioFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingVideo))){
	                    intent1 = OpenFiles.getVideoFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingText))){
	                    intent1 = OpenFiles.getTextFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingPdf))){
	                    intent1 = OpenFiles.getPdfFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingWord))){
	                    intent1 = OpenFiles.getWordFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingExcel))){
	                    intent1 = OpenFiles.getExcelFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }else if(checkEndsWithInStringArray(fileName, context.getResources().
	                        getStringArray(R.array.fileEndingPPT))){
	                    intent1 = OpenFiles.getPPTFileIntent(currentPath);
	                    context.startActivity(intent1);
	                }
	            }
	            catch(Exception e){
	            	Log.i("test", "附件打开失败");
	            	Toast.makeText(context, "无法打开，请安装相应的软件！", Toast.LENGTH_SHORT).show();
	            }
	        }	else
	        {
	        	Log.i("test", "找不到文件");
	            //showMessage("对不起，这不是文件！");
	        }
		}
	}
	private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings){
		for(String aEnd : fileEndings){
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
}
