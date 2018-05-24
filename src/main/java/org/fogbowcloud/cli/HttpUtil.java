package org.fogbowcloud.cli;

import java.io.IOException;

import javax.mail.Header;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtil {

	private static HttpClient httpClient;
	public static final String FEDERATED_TOKEN_HEADER = "accessId";
	public static final Header JSONHeader = new Header("Content-Type", "application/json");
			
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
		request.setHeader(FEDERATED_TOKEN_HEADER, federatedToken);
		request.setHeader(JSONHeader.getName(), JSONHeader.getValue());
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static HttpResponse get(String url, String federatedToken) 
			throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.setHeader(FEDERATED_TOKEN_HEADER, federatedToken);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
	
	public static HttpResponse delete(String url, String federatedToken) 
			throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete(url);
		request.setHeader(FEDERATED_TOKEN_HEADER, federatedToken);
		HttpResponse response = HttpUtil.getHttpClient().execute(request);
		return response;
	}
}
