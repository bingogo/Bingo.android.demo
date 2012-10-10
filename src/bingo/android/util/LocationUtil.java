package bingo.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class LocationUtil {
	private static final String TAG = "LocationUtil";

	/*
	 * 设备位置设置中的GPS是否开启
	 */
	public static boolean isGPSProviderAvaliable(Context context) {
		return isProviderAvaliable(context,
				android.location.LocationManager.GPS_PROVIDER);
	}

	/*
	 * 设备位置设置中的无线网络连接是否开启
	 */
	public static boolean isNetworkProviderAvaliable(Context context) {
		return isProviderAvaliable(context,
				android.location.LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isWIFIProviderAvaliable(Context context) {
		return NetworkUtil.wifiEnabled(context);
	}

	/*
	 * 根据wifi获取位置信息
	 */
	public static Location getLocationByWIFI(Context context) {
		Location location = null;

		WifiManager wifiMan = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		String mac = info.getMacAddress();
		String ssid = info.getSSID();

		JSONObject wifi = new JSONObject();
		try {
			wifi.put("mac_address", mac);
			wifi.put("ssid", ssid);
			JSONArray array = new JSONArray();
			array.put(wifi);

			JSONObject object = createLocationServiceJSONObject("wifi_towers",
					array);
			location = requestLocationService(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return location;
	}

	/*
	 * 根据基站获取位置信息 NOTE: MCC+MNC. Unreliable on CDMA networks(待测试)
	 */
	public static Location getLocationByCell(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		Location location = null;
		try {
			GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
			if (null == gcl) {
				return null;
			}

			int cid = gcl.getCid();
			int lac = gcl.getLac();
			int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
			int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3, 5));
			JSONObject holder = new JSONObject();
			holder.put("request_address", true);

			JSONArray array = new JSONArray();
			JSONObject data = new JSONObject();

			data.put("cell_id", cid);
			data.put("location_area_code", lac);
			data.put("mobile_country_code", mcc);
			data.put("mobile_network_code", mnc);
			array.put(data);

			JSONObject object = createLocationServiceJSONObject("cell_towers",
					array);
			location = requestLocationService(object);

		} catch (JSONException e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr JSONException error",
							e);
		} catch (Exception e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr Exception error",
							e);
		}
		return location;
	}
	
	/*
	 * 根据Location获取地址信息
	 * 注意：这里依赖google的位置服务，在某些设备上并没有安装此服务
	 */
	public static String getCityByLocation(Context context, Location location) {
		Geocoder geo = new Geocoder(context);
		String city = null;
		try {
			List<Address> address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if(address!= null && address.size()>0)
			{
				Address add = address.get(0);
				Log.v(TAG, String.format("getAdminArea:[%1$s],getCountryName:[%2$s],getLocality:[%3$s]",
						add.getAdminArea(), add.getCountryName(), add.getLocality()));
				city = add.getLocality();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return city;
	}

	private static boolean isProviderAvaliable(Context context, String provider) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(provider);
	}

	/*
	 * 创建地址服务对象的基础属性
	 */
	private static JSONObject createLocationServiceJSONObject(String arrayName,
			JSONArray array) {
		JSONObject object = new JSONObject();
		try {
			object.put("version", "1.1.0");
			object.put("host", "maps.google.com");
			object.put(arrayName, array);
		} catch (JSONException e) {
			Log.e(TAG, "call JSONObject's put failed", e);
		}
		return object;
	}

	/*
	 * 请求google地址服务，支持移动蜂窝基站和wifi地址两种
	 */
	private static Location requestLocationService(JSONObject holder) {		 
		Log.v(TAG, "requestLocation: " + holder.toString());
		BufferedReader br = null;
		Location location = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://www.google.com/loc/json");
			StringEntity se = new StringEntity(holder.toString());

			Log.v(TAG, "post:" + holder.toString());

			post.setEntity(se);
			HttpResponse resp = client.execute(post);

			if (resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = resp.getEntity();
				br = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}

				JSONObject data_ = new JSONObject(sb.toString());
				data_ = (JSONObject) data_.get("location");
				location = new Location(LocationManager.NETWORK_PROVIDER);

				Log.v(TAG, "location:" + data_);

				location.setLatitude((Double) data_.get("latitude"));
				location.setLongitude((Double) data_.get("longitude"));

				Log.i(TAG, "latitude : " + location.getLatitude()
						+ "  longitude : " + location.getLongitude());
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"network get the latitude and longitude ocurr JSONException error",
					e);
		} catch (ClientProtocolException e) {
			Log.e(TAG,
					"network get the latitude and longitude ocurr ClientProtocolException error",
					e);
		} catch (IOException e) {
			Log.e(TAG,
					"network get the latitude and longitude ocurr IOException error",
					e);
		} catch (Exception e) {
			Log.e(TAG,
					"network get the latitude and longitude ocurr Exception error",
					e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(TAG,
							"network get the latitude and longitude when closed BufferedReader ocurr IOException error",
							e);
				}
			}
		}
		return location;
	}

}
