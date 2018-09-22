package com.example.hbp;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

	public class MainActivity extends Activity {
		private final String TAG="hbp";
	    private Button btn_open, btn_bp,btn_ecg;
	    private ImageView iv_canvas;
	    private Bitmap baseBitmap;
	    private Canvas canvas;
	    private Paint paint;
	    private TextView infoText;
	    private Spinner spinner;
	    private List<String> data_list;
	    private Thread readThread;		//read thread
	    private ArrayAdapter<String> arr_adapter;
	    private boolean loopThread=false; 
	    private boolean ecgStatus=false;
	    private int threadTimeOut=0;
	    HbpJni hbp=new HbpJni();
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        spinner=(Spinner)findViewById(R.id.spinner1);
	        data_list=new ArrayList<String>();
	        data_list.add("AT+");
	        
	        arr_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data_list);
	        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(arr_adapter);
	        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO �Զ����ɵķ������
					Log.v("HBP","you choose "+arg2+data_list.get(arg2));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO �Զ����ɵķ������
					
				}
	        	
	        });
	        
	        // ��ʼ��һ�����ʣ��ʴ����Ϊ5����ɫΪ��ɫ
	        paint = new Paint();
	        paint.setStrokeWidth(5);
	        paint.setColor(Color.RED);

	      // iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
	        LinearLayout root=(LinearLayout) findViewById(R.id.linearLayout2);
	        
	        //����һ���Լ��̳���View�Ķ���
	        final DrawView drawView=new DrawView(this);
	        //�����Զ������������Ⱥ͸߶�
	        drawView.setMinimumWidth(300);
	        drawView.setMinimumHeight(500);
	  
	        drawView.setOnTouchListener(new View.OnTouchListener() {    
	            public boolean onTouch(View v, MotionEvent event) {
	                drawView.currentX=event.getX();
	                drawView.currentY=event.getY(); 
	                //�ػ�
	                drawView.invalidate();
	                //����true�������Ƿ����Ѿ�������¼�
	                return true;
	            }
	        });
	        root.addView(drawView);
	        
	        
	        btn_open = (Button) findViewById(R.id.btn_open);
	        btn_bp = (Button) findViewById(R.id.btn_BP);
	        btn_ecg=(Button)findViewById(R.id.btn_ECG);
	        infoText=(TextView)findViewById(R.id.textView1);

	        btn_open.setOnClickListener(click);
	        btn_bp.setOnClickListener(click);
	        btn_ecg.setOnClickListener(click);

	      //  resumeCanvas();
	      //  drawText();
	     //   CreateReadThread();
	       // iv_canvas.setOnTouchListener(touch);

	    }
	
	    private View.OnClickListener click = new OnClickListener() {

	        @Override
	        public void onClick(View v) {

	            switch (v.getId()) {
	            case R.id.btn_open:
	              //  saveBitmap();
	                hbp.HBPOpen("/dev/ttyUSB0");
	                break;
	            case R.id.btn_BP:
	            	hbp.SendCmd("AT+HVER");
	            	loopThread=false;
	            	CreateReadThread();
	             //   resumeCanvas();
//	            	drawText();
	                break;
	            case R.id.btn_ECG:
	            	if(ecgStatus)
	            	{
	            		ecgStatus=false;
	            		loopThread=false;
	            		hbp.SendCmd("AT+ECGON");
	
	            	}else{
	            		ecgStatus=true;
	            		loopThread=true;
	            		hbp.SendCmd("AT+ECGOFF");

	            	}
	            	
	            	Log.e(TAG,"btn_ECG");
	            	CreateReadThread();
	            	//resumeCanvas();
	             //   resumeCanvas();
	                break;    
	            default:
	                break;
	            }
	        }
	    };
	    private void CreateReadThread(){
	    	readThread=new Thread(){
	    		public void run()
	    		{
	    			super.run();
	    			while(true){
	    			try {
						Thread.sleep(100);
						threadTimeOut++;
					} catch (InterruptedException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
	    			final String info=hbp.GetData();
	    			if(info == null || info.length() == 0)
	    			{
	    				Log.e(TAG,"not get info");
	    			}else
	    			{
	    				Log.e(TAG,"info"+info);
	    				runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                        	infoText.setText(info);
	                        }
	                    });
	    				if(loopThread==false)
	    					break;	    				
	    			}
    				if(threadTimeOut>20&&loopThread==false)
    				{	threadTimeOut=0;
    					break;
    				}
	    			}
	    		}
	    	};
	    	readThread.start();
	    }
	    /**
	     * ����ͼƬ��SD����
	     */
	    protected void saveBitmap() {
	        try {
	            // ����ͼƬ��SD����
	            File file = new File(Environment.getExternalStorageDirectory(),
	                    System.currentTimeMillis() + ".png");
	            FileOutputStream stream = new FileOutputStream(file);
	            baseBitmap.compress(CompressFormat.PNG, 100, stream);
	            Toast.makeText(MainActivity.this, "����ͼƬ�ɹ�", 0).show();
	            
	            // Android�豸GalleryӦ��ֻ����������ʱ��ɨ��ϵͳ�ļ���
	            // ����ģ��һ��ý��װ�صĹ㲥������ʹ�����ͼƬ������Gallery�в鿴
	            Intent intent = new Intent();
	            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
	            intent.setData(Uri.fromFile(Environment
	                    .getExternalStorageDirectory()));
	            sendBroadcast(intent);
	        } catch (Exception e) {
	            Toast.makeText(MainActivity.this, "����ͼƬʧ��", 0).show();
	            e.printStackTrace();
	        }
	    }

	    /**
	     * �������
	     */
	    protected void resumeCanvas() {
	        // �ֶ��������Ļ�ͼ�����´���һ������
	        if (baseBitmap != null) {
	            baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
	                    iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
	            canvas = new Canvas(baseBitmap);
	          //  canvas.drawColor(Color.WHITE);
	            iv_canvas.setImageBitmap(baseBitmap);
	            Toast.makeText(MainActivity.this, "�������ɹ����������¿�ʼ��ͼ", 0).show();
	        }
	    }
	    protected void drawText(){
	        Paint paint=new Paint();
	        paint.setColor(Color.BLUE);  //���û�����ɫ

	        paint.setStrokeWidth (5);//���û��ʿ��
	        paint.setAntiAlias(true); //ָ���Ƿ�ʹ�ÿ���ݹ��ܣ����ʹ�ã���ʹ��ͼ�ٶȱ���
	        paint.setTextSize(20);//�������ִ�С

	        //��ͼ��ʽ����Ϊ���
	        paint.setStyle(Paint.Style.FILL);
	        canvas.drawText("����һ����������", 20,200, paint);
	    }
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

	}
