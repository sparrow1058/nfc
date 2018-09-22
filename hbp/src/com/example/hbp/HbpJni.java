package com.example.hbp;

public class HbpJni {
	///javah -classpath  . -jni   com.example.rfid.Jni
	static{
		System.loadLibrary("hbp");
	}
	public native boolean 	HBPOpen(String str);	//dev/ttyUSB0
	public native void 		SendCmd(String str);
	public native String	GetData();
	public native int KillRFThread();

}
