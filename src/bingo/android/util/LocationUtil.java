package bingo.android.util;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtil {
	private static boolean isProviderAvaliable(Context context, String provider) {
		LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(provider);
	}
	
	/*
	 * 设备位置设置中的GPS是否开启
	 */
	public static boolean isGPSProviderAvaliable(Context context) {
		return isProviderAvaliable(context, android.location.LocationManager.GPS_PROVIDER);
	}
	
	/*
	 * 设备位置设置中的无线网络连接是否开启
	 */
	public static boolean isNetworkProviderAvaliable(Context context) {
		return isProviderAvaliable(context, android.location.LocationManager.NETWORK_PROVIDER);
	}
	
	public static boolean isWIFIProviderAvaliable(Context context) {
		return NetworkUtil.wifiEnabled(context);
	}
	
}
