package bingo.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	/*
	 * WIFI�Ƿ����
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
	 * �����Ƿ����(NOTE:���µ�����ͨ2G��������ʾδ����)
	 */
	public static boolean networkEnabled(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean enabled = false;
		if(connectivityManager != null)
		{
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			enabled = info != null && info.isAvailable();
		}
		return enabled;
	}
}
