package com.oag.migo.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class DataLookup {

	private static final String USER_ID = "MIGO1";
	private static final String BASE_URL = "http://www.migo.aero/migo-service/services/json/";
	private static final String EXP_DATE_FORMAT = "yyyy-mm-dd HH:mm:ss.sss";

	private static String authToken = "";
	private static Date tokenExpiration = null;

	public static String[] lookupCarrierCd(String code)
			throws MalformedURLException, IOException, JSONException {
		String urlStr = BASE_URL + "lookup/airlineByCode?searchValue=" + code
				+ "&authToken=" + authToken;
		StringBuffer data = performHttpGet(urlStr);

		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		String name = null;
		if (jObj.has("status")) {
			if ("OK".equals(jObj.getString("status"))) {
				name = jObj.getJSONObject("result").getString("name");
			}
		}
		return name == null ? new String[0] : new String[] { name };
	}

	public static String[] lookupCarrierName(String searchValue)
			throws MalformedURLException, IOException, JSONException {
		String urlStr = BASE_URL + "lookup/airlineByName?searchValue="
				+ searchValue + "&authToken=" + authToken;
		StringBuffer data = performHttpGet(urlStr);

		String[] names = null;
		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		if (jObj.has("status")) {
			if ("OK".equals(jObj.getString("status"))) {
				JSONArray results = jObj.getJSONArray("result");
				names = new String[results.length()];
				for (int i = 0; i < results.length(); i++) {
					names[i] = results.getJSONObject(i).getString("name");
				}
			}
		}
		return names == null ? new String[0] : names;

	}

	public static JSONArray lookupAirportName(String searchValue)
			throws MalformedURLException, IOException, JSONException {
		String urlStr = BASE_URL + "lookup/portsByName?searchValue="
				+ searchValue + "&authToken=" + authToken;
		StringBuffer data = performHttpGet(urlStr);

		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		JSONArray results = null;
		if (jObj.has("status")) {
			if ("OK".equals(jObj.getString("status"))) {
				Object value = jObj.get("result");
				if (value instanceof JSONArray) {
					return (JSONArray) value;
				} else {
					results = new JSONArray();
					results.put(value);
				}

			}
		}
		return results;
	}

	public static JSONObject lookupAirportCd(String searchValue)
			throws MalformedURLException, IOException, JSONException {
		String urlStr = BASE_URL + "lookup/portsByIATA?searchValue="
				+ searchValue + "&authToken=" + authToken;
		StringBuffer data = performHttpGet(urlStr);

		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		JSONObject result = null;
		if (jObj.has("status")) {
			if ("OK".equals(jObj.getString("status"))) {
				result = jObj.getJSONObject("result");
			}
		}
		return result;
	}

	public static void auth() throws IOException, JSONException, ParseException {
		if (authToken != "" && !expired())
			return;
		String urlStr = BASE_URL + "auth?userId=" + USER_ID + "&password="
				+ USER_ID;
		StringBuffer data = performHttpGet(urlStr);
		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		authToken = jObj.getString("authToken");
		String expiration = jObj.getString("expiration");
		tokenExpiration = new SimpleDateFormat(EXP_DATE_FORMAT)
				.parse(expiration.replace("T", " ").replace("Z", ""));

	}

	public static boolean expired() {
		Calendar expiration = Calendar.getInstance();
		expiration.setTime(tokenExpiration);
		return Calendar.getInstance().before(expiration);
	}

	private static StringBuffer performHttpGet(String urlStr)
			throws MalformedURLException, IOException {
		URL url = new URL(urlStr);
		URLConnection con = url.openConnection();
		con.connect();
		InputStream is = con.getInputStream();
		byte[] b = new byte[128];

		int i = 0;
		StringBuffer data = new StringBuffer(512);
		while ((i = is.read(b)) > -1) {
			data.append(new String(b, 0, i));
		}
		Log.d("MIGO", "Migo http result: " + data);
		return data;
	}

	public static JSONArray lookupPortsInRadius(double latitude,
			double longitude, int radius) throws MalformedURLException,
			IOException, JSONException {
		String urlStr = BASE_URL + "status/portsInRadius?lat=" + latitude
				+ "&long=" + longitude + "&measure=1&distance=" + radius
				+ "&authToken=" + authToken;
		return lookupPortsInRadius(urlStr);
	}

	public static JSONArray lookupPortsInRadius(String urlStr)
			throws MalformedURLException, IOException, JSONException {
		StringBuffer data = performHttpGet(urlStr);

		JSONArray results = null;
		JSONObject jObj = (JSONObject) new JSONTokener(data.toString())
				.nextValue();
		if (jObj.has("status")) {
			if ("OK".equals(jObj.getString("status"))) {
				results = jObj.getJSONArray("result");
			}
		}
		return results == null ? new JSONArray() : results;

	}

	public static JSONArray lookupPortsInRadius(String portCd, int radius)
			throws MalformedURLException, IOException, JSONException {
		String urlStr = BASE_URL + "status/portsInRadius?airportCd=" + portCd
				+ "&measure=1&distance=" + radius + "&authToken=" + authToken;
		return lookupPortsInRadius(urlStr);
	}

	public static JSONObject alerts(int batchSize) {
		String url = BASE_URL + "alerts?batchSize=" + batchSize + "&authToken="
				+ authToken;
		StringBuffer data = null;
		JSONObject jData = null;
		try {
			data = performHttpGet(url);
			jData = new JSONObject(data.toString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jData;
	}

	public static JSONArray airportStats() {
		String url=BASE_URL+"status/airportStatus?authToken="+authToken;
		StringBuffer data=null;
		JSONObject jData = null;
		try {
			data = performHttpGet(url);
			jData = new JSONObject(data.toString());
			if (jData.has("result")) {
				return jData.getJSONArray("result");
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONArray();
	}

	public static JSONArray airlineStats() {
		String url=BASE_URL+"status/airlineStatus?authToken="+authToken;
		StringBuffer data=null;
		JSONObject jData = null;
		try {
			data = performHttpGet(url);
			jData = new JSONObject(data.toString());
			if (jData.has("result")) {
				return jData.getJSONArray("result");
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONArray();
	}

	public static String getAuthToken() {
		return authToken;
	}

	public static void setAuthToken(String authTokenIn) {
		authToken = authTokenIn;
	}

	public static Date getAuthExpiration() {
		return tokenExpiration;
	}

	public static void setAuthExpiration(Date d) {
		tokenExpiration = d;
	}

	public static void main(String[] args) throws IOException, JSONException,
			ParseException {
		DataLookup.auth();
		DataLookup.alerts(10);

	}
}
