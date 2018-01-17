package com.vouchify.vouchify;

import android.util.Log;

public class MemUtils {
	
	public static final float BYTES_IN_MB = 1024.0f * 1024.0f;
	
	public static float megabytesFree() {
		final Runtime rt = Runtime.getRuntime();
		final float bytesUsed = rt.totalMemory();
		final float mbUsed = bytesUsed / BYTES_IN_MB;
		final float mbFree = megabytesAvailable() - mbUsed;
		Log.d("ScaleBeforeLoad", "Free Memory: " + megabytesAvailable()+" , "+mbFree);
		return mbFree ;
	}

	public static float megabytesAvailable() {
		final Runtime rt = Runtime.getRuntime();
		final float bytesAvailable = rt.maxMemory() ;
		return bytesAvailable / BYTES_IN_MB;
	}
}
