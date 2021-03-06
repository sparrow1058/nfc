package com.example.bluetooth;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;



public class MainActivity extends Activity {
    
    private static final String TAG = "BluetoothTest";
    

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothSocket btSocket = null;

    private OutputStream outStream = null;
    
    private InputStream inStream = null;
    
    private SeekBar vskb = null;


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //这条是蓝牙串口通用的UUID，不要更改


    private static String address = "44:C3:46:05:C6:7F"; // <==要连接的蓝牙设备MAC地址


    
    /** Called when the activity is first created. */

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.vskb = (SeekBar)super.findViewById(R.id.seekBar1);
        this.vskb.setOnSeekBarChangeListener(new OnSeekBarChangeListenerX());

        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) 
        {
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        if(!mBluetoothAdapter.isEnabled()) 
        {
            Toast.makeText(this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }


    }


    private class  OnSeekBarChangeListenerX implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Main.this.clue.setText(seekBar.getProgress());
        /*    String message;
            byte [] msgBuffer;
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG,"ON RESUME : Output Stream creation failed.", e);
            }
            message =Integer.toString( seekBar.getProgress() );
            msgBuffer = message.getBytes();
            try{
                outStream.write(msgBuffer);
            } catch (IOException e) {
                Log.e (TAG, "ON RESUME : Exception during write.", e);
            }       */                
       } 
        
     
        public void onStartTrackingTouch(SeekBar seekBar) {
            String message;
            byte [] msgBuffer;
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG,"ON RESUME : Output Stream creation failed.", e);
            }
            message =Integer.toString( seekBar.getProgress() );
            msgBuffer = message.getBytes();
            try{
                outStream.write(msgBuffer);
            } catch (IOException e) {
                Log.e (TAG, "ON RESUME : Exception during write.", e);
            }         
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            String message;
            byte [] msgBuffer;
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG,"ON RESUME : Output Stream creation failed.", e);
            }
            message =Integer.toString( seekBar.getProgress() );
            msgBuffer = message.getBytes();
            try{
                outStream.write(msgBuffer);
            } catch (IOException e) {
                Log.e (TAG, "ON RESUME : Exception during write.", e);
            }         
        }
    }    
    
    
    @Override
    public void onStart() 
    {

        super.onStart();

    }


    @Override
    public void onResume() 
    {

        super.onResume();

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {

            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {

            Log.e(TAG, "ON RESUME: Socket creation failed.", e);

        }
        mBluetoothAdapter.cancelDiscovery();
        try {

            btSocket.connect();

            Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");

        } catch (IOException e) {

            try {
                btSocket.close();

            } catch (IOException e2) {

                Log .e(TAG,"ON RESUME: Unable to close socket during connection failure", e2);
            }

        }


        // Create a data stream so we can talk to server.

    /*     try {
        outStream = btSocket.getOutputStream();

        inStream = btSocket.getInputStream();
        
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
        }


        String message = "read";

        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);

        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Exception during write.", e);
        }
        int ret  = -1;

        while( ret != -1)
        {
                    try {
        
                     ret = inStream.read();
             
                     } catch (IOException e) 
                         {
                             e.printStackTrace();
                         }
        }
        
    */

    }


    @Override

    public void onPause() 
    {

        super.onPause();

        if (outStream != null)
        {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }

        }


        try {
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
        }

    }


    @Override

    public void onStop()
    {

        super.onStop();

    }


    @Override

    public void onDestroy() 
    {

        super.onDestroy();

    }

       @Override
        public boolean onKeyDown(int keyCode, KeyEvent event){
           if(keyCode == KeyEvent.KEYCODE_BACK){
               this.exitDialog();
           }
           return false;
       }
        private void exitDialog(){
           Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                   .setTitle("退出程序？")
                   .setMessage("您确定要退出本程序吗？")
                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MainActivity.this.finish();                        
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {                    
                        public void onClick(DialogInterface dialog, int whichButton) { }
                    }).create();
           dialog.show();
       }
        
}