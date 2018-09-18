package org.fogbowcloud.cli;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	private static HttpClient httpClient;
	public static final String FEDERATION_TOKEN_VALUE_HEADER_KEY = "federationTokenValue";

	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String JSON_CONTENT_TYPE_KEY = "application/json";

	private static HttpClient getHttpClient() {
		if (HttpUtil.httpClient == null) {
			return HttpUtil.httpClient = HttpClientBuilder.create().build();
		} else {
			return HttpUtil.httpClient;
		}
	}

	public static void setHttpClient(HttpClient httpClient) {
		HttpUtil.httpClient = httpClient;
	}

	public static HttpResponse post(String url, String json) throws IOException {
		return post(url, json, null);
	}

	public static HttpResponse post(String url, String json, String federatedToken) throws IOException {
		HttpPost request = new HttpPost(url);
		try {
			request.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Invalid json" + json, e);
		}
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		request.setHeader(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_KEY);
		HttpResponse response;
		try {
			response = HttpUtil.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = HttpUtil.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url, String federatedToken) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		HttpResponse response;
		try {
			response = HttpUtil.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url, String federatedToken, Map<String, String> additionalHeaders) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		for (Map.Entry<String, String> entry: additionalHeaders.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
		HttpResponse response;
		try {
			response = HttpUtil.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}

	public static HttpResponse delete(String url, String federatedToken) throws ClientProtocolException {
		HttpDelete request = new HttpDelete(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		HttpResponse response;
		try {
			response = HttpUtil.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static String getHttpEntityAsString(HttpResponse httpResponse) throws ParseException, IOException {
		String responseJson = EntityUtils.toString(httpResponse.getEntity());
		EntityUtils.consume(httpResponse.getEntity());
		return responseJson;
	}
}
