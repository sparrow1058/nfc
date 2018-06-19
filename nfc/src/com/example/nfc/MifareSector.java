package com.example.nfc;

public class MifareSector {
	//Default block size 
	public final static int BLOCKCOUNT=4;
	public int sectorIndex;	
	public MifareBlock[] blocks=new MifareBlock[BLOCKCOUNT];
	public MifareKey keyA;
	public MifareKey keyB;
	public boolean authorized;
}
