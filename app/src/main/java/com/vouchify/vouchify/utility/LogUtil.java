/*
 *
 */
package com.vouchify.vouchify.utility;

import android.util.Log;

/**
 * Customize environment logger Turn log off when release app
 */
public class LogUtil {
	public static boolean DEBUG = true;

	/**
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void e(String tag, String message) {
		if (DEBUG) {

			Log.e(tag, message);
		}
	}
}
