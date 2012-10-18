package bingo.android.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import bingo.android.exception.AppException;

public class HttpUtil {
	public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;

	public static String get(String url) throws AppException {
		String result = null;

		try {
			HttpClient httpClient = getHttpClient();
			HttpGet httpGet = getHttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int stateCode = httpResponse.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK)
				throw AppException.http(stateCode);

			HttpEntity entity = httpResponse.getEntity();
			result = EntityUtils.toString(entity, HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw AppException.http(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw AppException.network(e);
		}

		return result;
	}

	public static String post(String url, HttpEntity entity) throws AppException {
		String result = null;
		HttpPost httpPost = getHttpPOST(url);
		//�������header������Щserver�������ղ���body
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setEntity(entity);

		
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// for (String key : rawParams.keySet()) {
		// // ��װ�������
		// params.add(new BasicNameValuePair(key, rawParams.get(key)));
		// }
		// // �����������
		// post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// // ����POST����
		
		try {
			HttpResponse httpResponse = getHttpClient().execute(httpPost);
			int stateCode = httpResponse.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK)
				throw AppException.http(stateCode);

			HttpEntity returnEntity = httpResponse.getEntity();
			result = EntityUtils.toString(returnEntity, HTTP.UTF_8);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static JSONObject getJSON(String url) throws AppException {
		String result = HttpUtil.get(url);
		// ���û�з�������������ݻ��ߴ�����Ϣ������Ϊ���д��󣨿��ܿͻ��˴��ݲ������󣬻��߷������˳���
		JSONObject json;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.data(e);
		}
		return json;
	}

	private static HttpPost getHttpPOST(String url) {
		return new HttpPost(url);
	}

	private static HttpGet getHttpGet(String url) {
		return new HttpGet(url);
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
		final HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params,
				HTTP_REQUEST_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
		ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
		return httpClient;
	}

	// ����Ϊ�ϰ汾

	// public static String getRequest(String ulicrl) throws Exception {
	// String result = null;
	//
	// HttpClient httpClient = new DefaultHttpClient();
	//
	// try {
	// // ����HttpGet����
	// HttpGet get = new HttpGet(url);
	// // ����GET����
	// HttpResponse httpResponse = httpClient.execute(get);
	// // ����������ɹ��ط�����Ӧ
	// if (httpResponse.getStatusLine().getStatusCode() == 200) {
	// // ��ȡ��������Ӧ�ַ���
	// HttpEntity entity = httpResponse.getEntity();
	// Log.i("entity is null?", Boolean.toString(entity == null));
	// result = EntityUtils.toString(entity, HTTP.UTF_8);
	// Log.i("entity tostring is null?",
	// Boolean.toString(result == null));
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// httpClient.getConnectionManager().shutdown();
	// }
	//
	// return result;
	// }
	//
	// /**
	// *
	// * @param url
	// * ���������URL
	// * @param params
	// * �������
	// * @return ��������Ӧ�ַ���
	// * @throws Exception
	// */
	// public static String postRequest(String url, Map<String, String>
	// rawParams) {
	// String result = null;
	// HttpClient httpClient = new DefaultHttpClient();
	// try {
	// // ����HttpPost����
	// HttpPost post = new HttpPost(url);
	// // ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// for (String key : rawParams.keySet()) {
	// // ��װ�������
	// params.add(new BasicNameValuePair(key, rawParams.get(key)));
	// }
	// // �����������
	// post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	// // ����POST����
	// HttpResponse httpResponse = httpClient.execute(post);
	// // ����������ɹ��ط�����Ӧ
	// if (httpResponse.getStatusLine().getStatusCode() == 200) {
	// // ��ȡ��������Ӧ�ַ���
	// result = EntityUtils.toString(httpResponse.getEntity());
	// return result;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// httpClient.getConnectionManager().shutdown();
	// }
	// return result;
	// }

	/**
	 * ��ȡjson����
	 * 
	 * @param url
	 * @return JSONArray
	 * @throws JSONException
	 * @throws ConnectionException
	 */

	// public static String encode(String text) {
	// return URLEncoder.encode(text);
	// }
}
