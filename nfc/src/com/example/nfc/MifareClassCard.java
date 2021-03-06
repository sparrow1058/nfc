package com.example.nfc;

import android.util.Log;

public class MifareClassCard {
	private int SECTORCOUNT=16;
	protected String TAG="MifareCardInfo";
	private MifareSector[] sectors;
	public MifareClassCard(int sectorSize){
		sectors=new MifareSector[sectorSize];
		SECTORCOUNT=sectorSize;
		initializeCard();
	}
	private void initializeCard() {
		// TODO 自动生成的方法存根
		for(int i=0;i<SECTORCOUNT;i++){
			sectors[i]=new MifareSector();
			sectors[i].sectorIndex=i;
			sectors[i].keyA=new MifareKey();
			sectors[i].keyB=new MifareKey();
			for(int j=0;j<4;j++){
				sectors[i].blocks[j]=new MifareBlock();
				sectors[i].blocks[j].blockIndex=i*4+j;
				
			}
		}
	}
	public MifareSector getSector(int index){
		if(index>=SECTORCOUNT){
			throw new IllegalArgumentException("Invaid index for sector"); 
		}
		return sectors[index];
	}
	public void setSector(int index,MifareSector sector){
		if(index >=SECTORCOUNT){
			throw new IllegalArgumentException("Invaild index");
		}
		sectors[index]=sector;
	}
	public int getSectorCount(){
		return SECTORCOUNT;
	}
	public void setSectorCount(int newCount){
		if(SECTORCOUNT<newCount){
			sectors=new MifareSector[newCount];
			initializeCard();
		}
		SECTORCOUNT=newCount;
	}
	public void debugPrint(){
		int blockIndex=0;
		for(int i=0;i<SECTORCOUNT;i++){
			MifareSector sector=sectors[i];
			if(sector!=null){
				for(int j=0;j<MifareSector.BLOCKCOUNT;j++){
					MifareBlock block=sector.blocks[j];
					if(block!=null){
						byte[] raw=block.getData();
						String hexString=" Block"+j+" "+Converter.getHexString(raw, raw.length);
						Log.i(TAG,hexString);
					}
				}
			}
		}
	}
}
