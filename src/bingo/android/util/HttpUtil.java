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
		//如无这个header，则有些server处理会接收不到body
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setEntity(entity);

		
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// for (String key : rawParams.keySet()) {
		// // 封装请求参数
		// params.add(new BasicNameValuePair(key, rawParams.get(key)));
		// }
		// // 设置请求参数
		// post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// // 发送POST请求
		
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
		// 如果没有返回有意义的内容或者错误信息，则视为运行错误（可能客户端传递参数有误，或者服务器端出错）
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

	// 以下为老版本

	// public static String getRequest(String ulicrl) throws Exception {
	// String result = null;
	//
	// HttpClient httpClient = new DefaultHttpClient();
	//
	// try {
	// // 创建HttpGet对象。
	// HttpGet get = new HttpGet(url);
	// // 发送GET请求
	// HttpResponse httpResponse = httpClient.execute(get);
	// // 如果服务器成功地返回响应
	// if (httpResponse.getStatusLine().getStatusCode() == 200) {
	// // 获取服务器响应字符串
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
	// * 发送请求的URL
	// * @param params
	// * 请求参数
	// * @return 服务器响应字符串
	// * @throws Exception
	// */
	// public static String postRequest(String url, Map<String, String>
	// rawParams) {
	// String result = null;
	// HttpClient httpClient = new DefaultHttpClient();
	// try {
	// // 创建HttpPost对象。
	// HttpPost post = new HttpPost(url);
	// // 如果传递参数个数比较多的话可以对传递的参数进行封装
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// for (String key : rawParams.keySet()) {
	// // 封装请求参数
	// params.add(new BasicNameValuePair(key, rawParams.get(key)));
	// }
	// // 设置请求参数
	// post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	// // 发送POST请求
	// HttpResponse httpResponse = httpClient.execute(post);
	// // 如果服务器成功地返回响应
	// if (httpResponse.getStatusLine().getStatusCode() == 200) {
	// // 获取服务器响应字符串
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
	 * 获取json内容
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
