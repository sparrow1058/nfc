package com.example.m1card;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final String ACTION_NOTIFY = "android.intent.action.RFID_NOTIFY";
	final String ACTION_WRITE = "android.intent.action.RFID_WRITE";
	final String TAG="RFReceiver";
	final String EXTRA_ID = "_uid";
	final String EXTRA_CMD = "_cmd";
	final int MSG_READ=1;
	final int MSG_WRITE=2;
	final int MSG_CHANGEKEY=3;	
	private TextView infoText;
	private Button bt_read;
	private Button bt_write;
	Messenger messenger;
	Messenger reply;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		infoText=(TextView)findViewById(R.id.);
		infoText=(TextView)findViewById(R.id.textView1);
		bt_read=(Button)findViewById(R.id.btRead);
		bt_write=(Button)findViewById(R.id.btWrite);
		reply=new Messenger(handler);
	//	Intent intent=new Intent("com.example.rfid.RFIDService");		//绑定到服务
		Log.e("hzt Bind","Start Bind server ");

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
            	Log.e("hzt Bind","Bind server success");
                messenger = new Messenger(service);
            }
 
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("kk", "链接断开！");
            }
        };
	       Intent intent = new Intent();
	        intent.setComponent(new ComponentName("com.example.rfid", "com.example.rfid.RFIDService"));
	        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		bt_read.setOnClickListener(new View.OnClickListener() {

		
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				sendMessage(v,MSG_READ,2,"");
			}
		});

		bt_write.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				sendMessage(v,MSG_WRITE,2,"aa55aa55aa55");
			}
		});
		registerRFReceiver();
		
	}
	Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what)
			{
			case MSG_READ:
				 String info=msg.getData().getString("data");
				 Log.e("MSG",info);
				 infoText.setText(info);
				 break;
			case MSG_WRITE:
				int result=msg.getData().getInt("result");
				Log.e("MSG","="+result);
				break;
				
			}
			Log.d(TAG,"BACK CALL");
		}
	};
	public void sendMessage(View v,int what,int block,String dataStr){
		Message msg=Message.obtain(null,1);
		msg.replyTo=reply;
		Bundle bundle=new Bundle();
		bundle.putInt("block", block);
		bundle.putString("data",dataStr);	//write data , or Key		
		msg.setData(bundle);
		msg.what=what;
		
		try{
			messenger.send(msg);			
		}catch(RemoteException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	BroadcastReceiver rfReceiver =new BroadcastReceiver(){

		@Override
		public void onReceive(Context content, Intent intent) {
			// TODO 自动生成的方法存根
			Log.e(TAG," get rf uid");
			String uid = intent.getStringExtra(EXTRA_ID);
			infoText.setText(uid);
			Log.e(TAG,uid);
		}
		
	};
	private void registerRFReceiver(){
		IntentFilter filter=new IntentFilter(ACTION_NOTIFY);
		registerReceiver(rfReceiver,filter);
	}
}
