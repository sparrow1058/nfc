package com.example.rfid;
import java.util.Arrays;
import java.util.StringTokenizer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
public class RFIDService extends Service {
	final String ACTION_NOTIFY = "android.intent.action.RFID_NOTIFY";
	final String ACTION_WRITE = "android.intent.action.RFID_WRITE";
	final String EXTRA_ID = "_uid";
	final String EXTRA_CMD = "_cmd";
	final String TAG="RF Server";
	final int RF_GETUID=0;
	final int RF_READ=1;
	final int RF_WRITE=2;
	final int MSG_READ=1;
	final int MSG_WRITE=2;
	final int MSG_CHANGEKEY=3;
	
	final int RF_CHANGEKEY=3;
	private int rfCmd	=0;			//0=getuid 1= read
	private  Jni rfjni=new Jni();
	private int threadTime=1000;
	private boolean cardExist=false;
	private byte[] emptyUID={0,0,0,0};
	private byte[] uid;
	private int block=2;
	private byte[] txBuf;
	private byte[] rxBuf;
	private Thread rfidThread;

    Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.e("kk", msg.getData().getString("data"));
        	Message message=Message.obtain();
        	Bundle bundle = new Bundle();
        	switch(msg.what)
        	{
        	case MSG_READ:
				block=msg.getData().getInt("block");
				rxBuf=rfjni.RFReadBlock(block);
				bundle.putString("data",bytesToHexString(rxBuf));		//leaf
				message.what=MSG_READ;
                message.setData(bundle);
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        		break;
        	case MSG_WRITE:
        		block=msg.getData().getInt("block");
        		String hexString=msg.getData().getString("data");
        		txBuf=hexStringToBytes(hexString);
        		int result=rfjni.RFWriteBlock(block, txBuf);
        		bundle.putInt("result", result);
        		message.what=MSG_WRITE;
        		message.setData(bundle);
        		try{
        			msg.replyTo.send(message);
        		}catch(RemoteException e){
        			e.printStackTrace();
        		}     		
        		break;
        	case MSG_CHANGEKEY:
        		break;
        	default:
        		break;
        	}
            

            super.handleMessage(msg);
        }
    });
 

	@Override
	public void onCreate() {
		super.onCreate();
		rfjni.RFInit();
		rfCmd=RF_GETUID;
		registerWriteReceiver();
		rfidThread	=new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				while (true){
					try {
						Thread.sleep(threadTime);
						//hanleRFMessage();
						uid=rfjni.RFGetUid();
						if(Arrays.equals(uid, emptyUID))
						{	
							threadTime=1000;
							cardExist=false;
							Log.e(TAG,"NO Card");
						}else{
							threadTime=3000;
							cardExist=true;
							String uidStr=bytesToHexString(uid);
							Log.e(TAG,"Get Card UID is :"+uidStr);
							sendNotify(uidStr);
						//Log.e(TAG,Arrays.toString(res));
							
						}
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
						
				}
			}
			
		});
		rfidThread.start();
	}
	int[] convertByteArray(byte[] arr){
		if(arr != null && arr.length > 0){
			int[] result = new int[arr.length];
			for(int i = 0; i < arr.length; i ++){
				result[i] = (0xFF & arr[i]);
			}
			return result;
		}
		return null;
	}
	protected void hanleRFMessage(int cmd) {
		// TODO 自动生成的方法存根
		Log.e(TAG,"RFID hanle message");
		switch(cmd)
		{
		case RF_GETUID:
		//	Log.e(TAG,"Get UID");
			uid=rfjni.RFGetUid();
			if(Arrays.equals(uid, emptyUID))
			{	
				threadTime=3000;
				cardExist=false;
				Log.e(TAG,"NO Card");
			}else{
				threadTime=5000;
				cardExist=true;
				rfCmd=RF_WRITE;
				int[] res=convertByteArray(uid);
				Log.e(TAG,"Get Card UID is :"+Arrays.toString(res));
			//Log.e(TAG,Arrays.toString(res));
				
			}
			break;
		case RF_READ:
			Log.e(TAG,"RF Read");
			rxBuf=rfjni.RFReadBlock(block);
			//if(rxBuf.length==16)
			rfCmd=RF_GETUID;
			Log.e(TAG,Arrays.toString(rxBuf));
			break;
		case RF_WRITE:
			txBuf="abcdefghabcdefgh".getBytes();
			rfjni.RFWriteBlock(block, txBuf);
			rfCmd=RF_READ;
			break;
		case RF_CHANGEKEY:
			break;
		}
		
	}

	BroadcastReceiver writeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context ctx, Intent intent) {
			writeRfid(intent);			
		}		
	};
	private void registerWriteReceiver(){
		IntentFilter filter = new IntentFilter(ACTION_WRITE);
		registerReceiver(writeReceiver, filter);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.e("hzt kk","Bind Success!");
		return messenger.getBinder();
	}
	
	
	private void sendNotify(String str){
		Intent intent = new Intent(ACTION_NOTIFY);
		intent.putExtra(EXTRA_ID, str);
		sendStickyBroadcast(intent);
	}
	
	
	private void writeRfid(Intent content){
		String pwd = content.getStringExtra(EXTRA_CMD);
		//TODO write to device
	}
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder=new StringBuilder("");
		if(src==null|| src.length<=0){
			return null;
			}
		for(int i=0;i<src.length;i++){
			int v=src[i]&0xFF;
			String hv=Integer.toHexString(v);
			if(hv.length()<2){
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	public static byte[] hexStringToBytes(String hexString){
		if(hexString==null||hexString.equals("")){
			return null;
		}
		hexString=hexString.toUpperCase();
		int length=hexString.length()/2;
		char[] hexChars=hexString.toCharArray();
		byte[] d=new byte[length];
		for(int i=0;i<length;i++){
			int pos=i*2;
			d[i]=(byte)(charToByte(hexChars[pos])<<4|charToByte(hexChars[pos+1]));
		}
		return d;
	}
	private static  byte charToByte(char c) {
		// TODO 自动生成的方法存根
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
}
