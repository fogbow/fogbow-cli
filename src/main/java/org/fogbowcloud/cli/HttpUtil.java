package org.fogbowcloud.cli;

import java.io.IOException;
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
import org.fogbowcloud.manager.utils.HttpRequestUtil;

public class HttpUtil {

	private static HttpClient httpClient;
	public static final String FEDERATION_TOKEN_VALUE_HEADER_KEY = "federationTokenValue";

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

	public static HttpResponse post(String url, String json, String federatedToken)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);
		request.setEntity(new StringEntity(json));
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		request.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static HttpResponse get(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static HttpResponse get(String url, String federatedToken) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static HttpResponse get(String url, String federatedToken, Map<String, String> additionalHeaders)
			throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		for (Map.Entry<String, String> entry: additionalHeaders.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}

	public static HttpResponse delete(String url, String federatedToken) throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete(url);
		request.setHeader(FEDERATION_TOKEN_VALUE_HEADER_KEY, federatedToken);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static String getHttpEntityAsString(HttpResponse httpResponse) throws ParseException, IOException {
		String responseJson = EntityUtils.toString(httpResponse.getEntity());
		EntityUtils.consume(httpResponse.getEntity());
		return responseJson;
	}
}
