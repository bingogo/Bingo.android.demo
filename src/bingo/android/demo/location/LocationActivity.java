package bingo.android.demo.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bingo.android.demo.R;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/*
 * 此demo演示如何使用locationManager来获取位置信息
 * 对比4中获取位置信息的方式
 */
public class LocationActivity extends Activity implements LocationListener {
	private TextView location;
	private static final String TAG = "LocationActivity";

	LocationManager locationManager = null;
	private Location loc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		location = (TextView) this.findViewById(R.id.location);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		List<String> enabledProviders = locationManager.getProviders(criteria,
				true);

		if (enabledProviders.isEmpty()) {
			Log.v(TAG, "enabledProviders isEmpty");
		} else {
			for (String enabledProvider : enabledProviders) {
				locationManager.requestLocationUpdates(enabledProvider, 1000,
						0, this);
				Log.v(TAG, "enabledProviders " + enabledProvider);
			}

			checkUpLocation(0);
		}
	}

	private static final int CHECK_LOCATION_TIMES = 5;
	private static final long CHECK_LOCATION_DELAYED = 15000;
	private static final int CHECK_LOCATION = 1;
	private Handler locationHanlder = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == CHECK_LOCATION) {
				Log.v(TAG, "check location");
				if (loc != null)
					return;

				if (msg.arg1 <= msg.arg2) {
					Log.v(TAG, "check location times:" + msg.arg1);
					checkUpLocation(msg.arg1);
				} else {
					locationManager.removeUpdates(LocationActivity.this);
					LocationActivity.this.getLastLocation();
				}
			}
		};
	};

	private synchronized void checkUpLocation(int times) {
		Log.v(TAG, "checkUpLocation");
		Message msg = locationHanlder.obtainMessage(CHECK_LOCATION, ++times,
				CHECK_LOCATION_TIMES);
		locationHanlder.sendMessageDelayed(msg, CHECK_LOCATION_DELAYED);
	}

	protected void getLastLocation() {
		Log.v(TAG, "getLastLocation");
		loc = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc == null) {
			Log.v(TAG, "NETWORK_PROVIDER is null");
			loc = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (loc != null)
			this.showLocationInfo(loc);
		else
			Log.v(TAG, "location is null");

	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	public void onRequestLocation(View view) {
		switch (view.getId()) {
		case R.id.gpsBtn:
			Log.d(TAG, "GPS button is clicked");
			requestGPSLocation();
			Location locGps = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			showLocationInfo(locGps);
			break;
		case R.id.netBtn:
			Log.d(TAG, "Network button is clicked");
			requestNetworkLocation();
			Location locNet = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			showLocationInfo(locNet);
			break;
		case R.id.telBtn:
			Log.d(TAG, "CellID button is clicked");
			requestTelLocation();
			break;
		case R.id.wifiBtn:
			Log.d(TAG, "WI-FI button is clicked");
			requestWIFILocation();
			break;
		case R.id.allBtn:
			getLocation();
			break;
		}
	}

	private void getLocation() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		StringBuffer sb = null;
		BufferedReader br = null;
		try {

			GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
			if (null == gcl) {
				return ;
			}
			int cid = gcl.getCid();
			int lac = gcl.getLac();
			int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
			int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3, 5));
			JSONObject holder = new JSONObject();
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("request_address", true);

			JSONArray array = new JSONArray();
			JSONObject data = new JSONObject();

			data.put("cell_id", cid);
			data.put("location_area_code", lac);
			data.put("mobile_country_code", mcc);
			data.put("mobile_network_code", mnc);
			array.put(data);
			holder.put("cell_towers", array);
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
				sb = new StringBuffer();
				String result = br.readLine();
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}

				JSONObject data_ = new JSONObject(sb.toString());
				data_ = (JSONObject) data_.get("location");
				Location loc = new Location(LocationManager.NETWORK_PROVIDER);
				loc.setLatitude((Double) data_.get("latitude"));
				loc.setLongitude((Double) data_.get("longitude"));
				
				android.util.Log.i(TAG, "latitude : " + loc.getLatitude()
						+ "  longitude : " + loc.getLongitude());
				
			}

		} catch (JSONException e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr JSONException error",
							e);
		} catch (ClientProtocolException e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr ClientProtocolException error",
							e);
		} catch (IOException e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr IOException error",
							e);
		} catch (Exception e) {
			android.util.Log
					.e(TAG,
							"network get the latitude and longitude ocurr Exception error",
							e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					android.util.Log
							.e(TAG,
									"network get the latitude and longitude when closed BufferedReader ocurr IOException error",
									e);
				}
			}
		}
	}

	private void showLocationInfo(Location loc) {
		if (loc == null)
			return;
		Log.v(TAG,
				String.format("lat:[%1$s],lng:[%2$s]", loc.getLatitude(),
						loc.getLongitude()));
		location.setText(String.format("lat:[%1$s],lng:[%2$s]",
				loc.getLatitude(), loc.getLongitude()));

	}

	private void requestGPSLocation() {
		LocationManager mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60,
				100, mLocLis);
	}

	private void requestNetworkLocation() {
		LocationManager mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				mLocLis);
	}

	private LocationListener mLocLis = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "onStatusChanged, provider = " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, "onProviderEnabled, provider = " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "onProviderDisabled, provider = " + provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Log.d(TAG, "latitude: " + latitude + ", longitude: " + longitude);
		}
	};

	private void requestTelLocation() {
		TelephonyManager mTelMan = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// MCC+MNC. Unreliable on CDMA networks
		String operator = mTelMan.getNetworkOperator();
		String mcc = operator.substring(0, 3);
		String mnc = operator.substring(3, 5);

		GsmCellLocation location = (GsmCellLocation) mTelMan.getCellLocation();
		int cid = location.getCid();
		int lac = location.getLac();

		JSONObject tower = new JSONObject();
		try {
			tower.put("cell_id", cid);
			tower.put("location_area_code", lac);
			tower.put("mobile_country_code", mcc);
			tower.put("mobile_network_code", mnc);
		} catch (JSONException e) {
			Log.e(TAG, "call JSONObject's put failed", e);
		}

		JSONArray array = new JSONArray();
		array.put(tower);

		List<NeighboringCellInfo> list = mTelMan.getNeighboringCellInfo();
		Iterator<NeighboringCellInfo> iter = list.iterator();
		NeighboringCellInfo cellInfo;
		JSONObject tempTower;
		while (iter.hasNext()) {
			cellInfo = iter.next();
			tempTower = new JSONObject();
			try {
				tempTower.put("cell_id", cellInfo.getCid());
				tempTower.put("location_area_code", cellInfo.getLac());
				tempTower.put("mobile_country_code", mcc);
				tempTower.put("mobile_network_code", mnc);
			} catch (JSONException e) {
				Log.e(TAG, "call JSONObject's put failed", e);
			}
			array.put(tempTower);
		}

		JSONObject object = createJSONObject("cell_towers", array);
		requestLocation(object);
	}

	private void requestWIFILocation() {
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		String mac = info.getMacAddress();
		String ssid = info.getSSID();

		JSONObject wifi = new JSONObject();
		try {
			wifi.put("mac_address", mac);
			wifi.put("ssid", ssid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONArray array = new JSONArray();
		array.put(wifi);

		JSONObject object = createJSONObject("wifi_towers", array);
		requestLocation(object);
	}

	private void requestLocation(JSONObject object) {
		Log.d(TAG, "requestLocation: " + object.toString());
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		try {
			StringEntity entity = new StringEntity(object.toString());
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			StringBuffer buffer = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				buffer.append(result);
				result = br.readLine();
			}

			Log.d(TAG, buffer.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSONObject createJSONObject(String arrayName, JSONArray array) {
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

	@Override
	public void onLocationChanged(Location location) {
		Log.v(TAG, "onLocationChanged");
		loc = location;
		this.showLocationInfo(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
