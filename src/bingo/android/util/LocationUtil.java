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
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class LocationUtil {
	private static final String TAG = "LocationUtil";

	/*
	 * �豸λ�������е�GPS�Ƿ���
	 */
	public static boolean isGPSProviderAvaliable(Context context) {
		return isProviderAvaliable(context,
				android.location.LocationManager.GPS_PROVIDER);
	}

	/*
	 * �豸λ�������е��������������Ƿ���
	 */
	public static boolean isNetworkProviderAvaliable(Context context) {
		return isProviderAvaliable(context,
				android.location.LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isWIFIProviderAvaliable(Context context) {
		return NetworkUtil.wifiEnabled(context);
	}

	/*
	 * ����wifi��ȡλ����Ϣ
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
	 * ���ݻ�վ��ȡλ����Ϣ NOTE: MCC+MNC. Unreliable on CDMA networks(������)
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
	 * ����Location��ȡ��ַ��Ϣ ע�⣺��������google��λ�÷�����ĳЩ�豸�ϲ�û�а�װ�˷���
	 */
	public static String getCityByLocation(Context context, Location location) {
		Geocoder geo = new Geocoder(context);
		String city = null;
		try {
			List<Address> address = geo.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
			if (address != null && address.size() > 0) {
				Address add = address.get(0);
				Log.v(TAG,
						String.format(
								"getAdminArea:[%1$s],getCountryName:[%2$s],getLocality:[%3$s]",
								add.getAdminArea(), add.getCountryName(),
								add.getLocality()));
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
	 * ������ַ�������Ļ�������
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
	 * ����google��ַ����֧���ƶ����ѻ�վ��wifi��ַ����
	 */
	private static Location requestLocationService(JSONObject holder) {
		BufferedReader br = null;
		Location location = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			//NOTE:ĳЩ�豸��dns�����������⣬�����������www.google.com����Ļ������޷���������˻���ip��ַ�����õķ�ʽ�ǽ���������ʻ�ȡ
			HttpPost post = new HttpPost("http://74.125.128.104/loc/json");
			StringEntity se = new StringEntity(holder.toString());
			
			//����ʹ�ô���һ���Խ������
			HttpUtil.post("http://10.0.6.33:9000/location/loc", se);
			

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

				Log.v(TAG, "location:" + sb.toString());
				JSONObject data = new JSONObject(sb.toString());
				data = (JSONObject) data.get("location");
				
				
				location = new Location(LocationManager.NETWORK_PROVIDER);

				Log.v(TAG, "location:" + data);

				location.setLatitude((Double) data.get("latitude"));
				location.setLongitude((Double) data.get("longitude"));

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
	
	
	/** ��վ��Ϣ */
    private static class SCell
    {
     public int CID; // cellId��վ��ţ��Ǹ�16λ�����ݣ���Χ��0��65535��
        public int MCC; // mobileCountryCode�ƶ����Ҵ��루�й���Ϊ460��
        public int MNC; // mobileNetworkCode�ƶ�������루�й��ƶ�Ϊ00���й���ͨΪ01��
        public int LAC; // locationAreaCodeλ��������        
        public String radioType; //��ͨ�ƶ�gsm������cdma       

    }
 

	public static Location getLocationByCell(Context context, boolean isAll) {
		TelephonyManager mTelNet = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = mTelNet.getNetworkType();		
		
		SCell cell = new SCell();
		// �й�����ΪCTC: NETWORK_TYPE_EVDO_A���й�����3G��getNetworkType;
		  // NETWORK_TYPE_CDMA����2G��CDMA
		  if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type == TelephonyManager.NETWORK_TYPE_1xRTT)
		  {
			  Log.v(TAG, "NETWORK_TYPE_CDMA����2G��CDMA");
		CdmaCellLocation location = (CdmaCellLocation) mTelNet.getCellLocation();

		   StringBuilder nsb = new StringBuilder();
		   nsb.append(location.getSystemId());

		   cell.CID = location.getBaseStationId();  
		   cell.LAC = location.getNetworkId();
		   cell.MNC = location.getSystemId();
		   cell.MCC = Integer.valueOf(mTelNet.getNetworkOperator().substring(0, 3));
		   cell.radioType = "cdma";

		  }
		  // �ƶ�2G�� + CMCC + 2 type = NETWORK_TYPE_EDGE
		  else if (type == TelephonyManager.NETWORK_TYPE_EDGE)
		  {
			  Log.v(TAG, "�ƶ�2G�� + CMCC + 2 type");
		   GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		   String operator = mTelNet.getNetworkOperator();
		   
		   cell.CID = location.getCid();
		   cell.MCC = Integer.parseInt(operator.substring(0, 3));
		   cell.MNC = Integer.parseInt(operator.substring(3));
		   cell.LAC = location.getLac();   
		   cell.radioType = "gsm";
		  }
		  // ��ͨ��2G�������� China Unicom 1 NETWORK_TYPE_GPRS
		  // �������ԣ���ȡ��ͨ���ݵ�ʱ���޷���ȡ���Ҵ����������룬��������ΪJSON Parsing Error
		  else if (type == TelephonyManager.NETWORK_TYPE_GPRS)
		  {
			  Log.v(TAG, "��ͨ��2G�������� China Unicom 1 NETWORK_TYPE_GPRS");
		   GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		   cell.CID = location.getCid();
		   cell.LAC = location.getLac();
		   cell.radioType = "gsm";
		  }
		  else
		  {
			  Log.v(TAG, "null");			  
			  return getLocationByCell(context);
		  }

		
		try {
	            /** ����POST��JSON���� */
	            JSONObject holder = new JSONObject();
	            holder.put("version", "1.1.0");
	            holder.put("host", "maps.google.com");            
	            holder.put("request_address", true);
	            holder.put("radio_type", cell.radioType);
	            //holder.put("carrier", "HTC");
	            holder.put("home_mobile_country_code", cell.MCC);
	   holder.put("home_mobile_network_code", cell.MNC);    
	   holder.put("address_language", "zh_CN");
	   
	            
	           

	            JSONObject tower = new JSONObject();
	            tower.put("mobile_country_code", cell.MCC);
	            tower.put("mobile_network_code", cell.MNC);
	            tower.put("cell_id", cell.CID);
	            tower.put("location_area_code", cell.LAC);  
	            tower.put("age", 0);           
	           

	            JSONArray array = new JSONArray();
	            array.put(tower);
	            
	            holder.put("cell_towers", array);
	            Log.e("Location send", holder.toString());
			
	            holder.put("cell_towers", array);
			return requestLocationService(holder);

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
		 
		return null;
	}

}
