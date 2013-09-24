package com.oag.migo.util;

import android.content.Context;
import android.widget.Toast;

public class NotificationUtil {

	public static void sendToastLong(Context context, String msg) {
		sendToast(context, msg, Toast.LENGTH_LONG);
	}
	public static void sendToastShort(Context context, String msg) {
		sendToast(context, msg, Toast.LENGTH_SHORT);
	}
	public static void sendToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}
}
