package com.example.nfc;

public class MifareBlock {
	public int blockIndex;
	public boolean needRead=true;
	public boolean needWrite=false;
	private final byte[] data=new byte[16];
	public MifareBlock(byte[] dataValue){
		if(dataValue==null||dataValue.length!=16){
			throw new IllegalArgumentException("Invaild data");
		}
		System.arraycopy(dataValue, 0, data, 0, dataValue.length);
	}
	public MifareBlock(){
		
	}
	public byte[] getData(){
		return data;
	}
	public void setData(byte[] dataValue){
		if(dataValue==null||dataValue.length!=16){
			throw new IllegalArgumentException("Invaild data");
		}
		System.arraycopy(dataValue, 0, data,0,dataValue.length);
		
	}
}
