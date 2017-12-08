package com.itheima.mobilesafe74.service;

import com.itheima.mobilesafe74.db.dao.BlackNumberDao;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.text.NoCopySpan.Concrete;

public class BlackNumberService extends Service {

	private BlackNumberDao mDao;
	private InnerSmsReceiver receiver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		//拦截短信
		IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		receiver = new InnerSmsReceiver();
		registerReceiver(receiver, intentFilter);
		super.onCreate();
	}
	class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取短信内容,获取发送短信电话号码,如果此电话号码在黑名单中,并且拦截模式也为1(短信)或者3(所有),拦截短信
			//1,获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//2,循环遍历短信过程
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//4,获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				int mode=mDao.getMode(originatingAddress);
				if(mode == 1 || mode == 3){
					//拦截短信(android 4.4版本失效	短信数据库,删除)
					abortBroadcast();
				}
			}
			
		}
		
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}