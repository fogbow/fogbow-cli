package org.fogbowcloud.cli;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtil {

	private static HttpClient httpClient;
	public static final String FEDERATED_TOKEN_HEADER = "accessId";
	
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
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(json));
		post.setHeader(FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader("Content-type", "application/json");
		HttpResponse response = HttpUtil.getHttpClient().execute(post);
		return response;
	}
}
