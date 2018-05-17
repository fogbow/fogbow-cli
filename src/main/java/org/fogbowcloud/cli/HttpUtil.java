package org.fogbowcloud.cli;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtil {

	private static HttpClient httpClient = HttpClientBuilder.create().build();
	private static final String FEDERATED_TOKEN_HEADER = "accessid";
	
	public static HttpResponse post(String url, String json, String federatedToken)
			throws ClientProtocolException, IOException {	
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(json));
		post.setHeader(FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		return response;
	}
}
