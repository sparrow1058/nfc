package com.example.rfid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context ctx, Intent arg1) {
		ctx.startService(new Intent(ctx, RFIDService.class));
	}
}
