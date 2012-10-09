package bingo.android.util;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtil {
	private static boolean isProviderAvaliable(Context context, String provider) {
		LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(provider);
	}
	
	/*
	 * �豸λ�������е�GPS�Ƿ���
	 */
	public static boolean isGPSProviderAvaliable(Context context) {
		return isProviderAvaliable(context, android.location.LocationManager.GPS_PROVIDER);
	}
	
	/*
	 * �豸λ�������е��������������Ƿ���
	 */
	public static boolean isNetworkProviderAvaliable(Context context) {
		return isProviderAvaliable(context, android.location.LocationManager.NETWORK_PROVIDER);
	}
	
	public static boolean isWIFIProviderAvaliable(Context context) {
		return NetworkUtil.wifiEnabled(context);
	}
	
}
