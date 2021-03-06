package com.example.nfc;

import java.io.IOException;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter [] mFilters;
	private String[][] mTechLists;
	private TextView infoText;
	private TextView idText;
	private Button btRead;
	private Button btWrite;
	private final String TAG="M1 Card";
	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	protected final short ByteCountPerBlock = 16;
	protected final short BlockCountPerSector = 4;
	protected final short ByteCountPerCluster = ByteCountPerBlock;
	byte[] testBytes={0x1,0x2,0x2,0x2,0x2,0x2,0x3,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0xF};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		windowsInit();
		mAdapter=NfcAdapter.getDefaultAdapter(this);
		mPendingIntent=PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		//setup an intent filter for all MIME based dispatches
		IntentFilter ndef=new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try{
			ndef.addDataType("*/*");
		}catch(MalformedMimeTypeException e){
			throw new RuntimeException("fail",e);
		}
		mFilters=new IntentFilter[]{ndef,};
		mTechLists=new String[][]{new String[]{MifareClassic.class.getName()}};
	}
	public void getCardID(Intent intent){
		String action=intent.getAction();
		String str2="NFC Card UID:";
		byte[] myNFCID=intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		idText.setText(str2+Converter.getHexString(myNFCID,myNFCID.length));
		if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
			Tag tagFromIntent=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc=MifareClassic.get(tagFromIntent);
			Tag mytagTag=mfc.getTag();
			byte[] myByte=tagFromIntent.getId();
			int cardID=tagFromIntent.describeContents();
			String str=myByte.toString();
			idText.setText("UID:"+cardID);
			Log.e(TAG,"UID="+cardID);	
		}
	}
	public void onResume(){
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}
	public void onNewIntent(Intent intent){
		String action=intent.getAction();
		if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			m1cardWrite(tag);
			m1cardRead(tag);
		}
		
		//getCardID(intent);
		//resolveIntent(intent);
		
	}
	public void onPause(){
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
	private void resolveIntent(Intent intent) {
		// TODO 自动生成的方法存根
		String action=intent.getAction();
		if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
			Tag tagFromIntent=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc=MifareClassic.get(tagFromIntent);
			MifareClassCard mifareClassCard=null;
			//Start read
			try{
				mfc.connect();
				boolean auth=false;
				int secCount=mfc.getSectorCount();
				int bCount=0,bIndex=0;
				mifareClassCard=new MifareClassCard(secCount);
				for(int j=0;j<secCount;j++){
					MifareSector mifareSector=new MifareSector();
					mifareSector.sectorIndex=j;
					auth=mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
					mifareSector.authorized=auth;
					if(auth){
						bCount=mfc.getBlockCountInSector(j);
						bCount=Math.min(bCount,MifareSector.BLOCKCOUNT);
						bIndex=mfc.sectorToBlock(j);
						for(int i=0;i<bCount;i++){
							byte[] data=mfc.readBlock(bIndex);
							if(j==12&&i==2){
								try{
									mfc.writeBlock(bIndex, testBytes);
									Log.e(TAG,"***************************Write block");
								}catch(IOException e){
									Log.e(TAG,"nfc write error");
								}finally{
									showAlert(3,"write success");
								}
							}
								MifareBlock mifareBlock=new MifareBlock(data);
								mifareBlock.blockIndex=bIndex;
								bIndex++;
								mifareSector.blocks[i]=mifareBlock;
							}
							mifareClassCard.setSector(mifareSector.sectorIndex, mifareSector);
						}else{
							
						}	
				}
			}catch (IOException e){
				Log.e(TAG,e.getLocalizedMessage());
				showAlert(3,e.toString());
				
			}finally{
				mifareClassCard.debugPrint();
				showAlert(3,"Read success");
			}
			
		}
	}
	protected short getSectorAddress(short blockAddress) {
		return (short) (blockAddress / BlockCountPerSector);
	}
	protected void CombineByteArray(byte[] data1, byte[] data2, int startIndex) {
		for (int i = 0; i < data2.length; i++) {
			data1[startIndex + i] = data2[i];
		}
	}
	private void m1cardWrite(Tag tag)
	{
		MifareClassic mc=MifareClassic.get(tag);
		try{
			mc.connect();
			boolean auth=false;
			short sectorAddress=0;
			auth=mc.authenticateSectorWithKeyA(sectorAddress, MifareClassic.KEY_DEFAULT);
			if(auth)
			{
				mc.writeBlock(2,testBytes);
				mc.close();
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try{
				mc.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	private void m1cardRead(Tag tag)
	{
		
		//tag 就是在上一篇中onNewIntent中获取的tag
		MifareClassic mc = MifareClassic.get(tag);
		        short startAddress = 0;
		        short endAddress = 5;

		        byte[] data = new byte[(endAddress - startAddress + 1 ) * ByteCountPerBlock];
		        
		        try {            
		            mc.connect();
		            int time=0;
		            for (short i = startAddress; i <= endAddress; i++ ,time++) {
		                boolean auth = false;
		                short sectorAddress = getSectorAddress(i);
		                auth = mc.authenticateSectorWithKeyA(sectorAddress, MifareClassic.KEY_DEFAULT);
		                if (auth){
		                    
		                    //the last block of the sector is used for KeyA and KeyB cannot be overwritted
		                    short readAddress = (short)(sectorAddress == 0 ? i : i + sectorAddress);
		                    
		                    byte[] response = mc.readBlock(readAddress);
							String hexString=" Block"+i+" "+Converter.getHexString(response, response.length);
							Log.i(TAG,hexString);
		                    CombineByteArray(data, response, time * ByteCountPerBlock);
		                }
		                else{
		                	Log.e(TAG,"Auth error");	
		                }
		            }

		            mc.close();
		            
		        }
		        catch (IOException e) {
		        	e.printStackTrace();
		        }
		        finally
		        {
		            try {
		                mc.close();
		            } catch (IOException e) {
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
		        }
	}
	
	private void showAlert(int alertCase, String str) {
		// TODO 自动生成的方法存根
		AlertDialog.Builder alertBox =new AlertDialog.Builder(this);
		switch(alertCase){
		case AUTH:
			alertBox.setMessage("Auth failed" );
			break;
		case EMPTY_BLOCK_0:
			alertBox.setMessage("Empty block 0");
			break;
		case EMPTY_BLOCK_1:
			alertBox.setMessage("Empty block 1");
			break;
		}
		alertBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Save the data from the UI to the database - already done
			public void onClick(DialogInterface arg0, int arg1) {
				clearFields();
			}
		});
		// display box
		alertBox.show();

	}
	
	private void clearFields() {
		

	}		
	
	private void windowsInit()
	{
		infoText=(TextView)findViewById(R.id.info_text);
		idText=(TextView)findViewById(R.id.id_text);
		btRead=(Button)findViewById(R.id.bt_read);
		btWrite=(Button)findViewById(R.id.bt_write);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
