package bingo.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	/*
	 * WIFI是否可用
	 */
	public static boolean wifiEnabled(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/*
	 * 网络是否可用
	 */
	public static boolean networkEnabled(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean enabled = false;
		if(connectivityManager != null)
		{
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			enabled = info != null;
		}
		return enabled;
	}
}
