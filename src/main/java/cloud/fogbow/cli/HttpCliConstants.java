package cloud.fogbow.cli;

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

public class HttpCliConstants {

	private static HttpClient httpClient;

	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String JSON_CONTENT_TYPE_KEY = "application/json";

	private static HttpClient getHttpClient() {
		if (HttpCliConstants.httpClient == null) {
			return HttpCliConstants.httpClient = HttpClientBuilder.create().build();
		} else {
			return HttpCliConstants.httpClient;
		}
	}

	public static void setHttpClient(HttpClient httpClient) {
		HttpCliConstants.httpClient = httpClient;
	}

	public static HttpResponse post(String url, String json) throws IOException {
		return post(url, json, null);
	}

	public static HttpResponse post(String url, String json, String systemUserToken) throws IOException {
		HttpPost request = new HttpPost(url);
		try {
			request.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Invalid json" + json, e);
		}
		HttpResponse response;
		try {
			response = HttpCliConstants.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = HttpCliConstants.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url, String systemUserToken) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = HttpCliConstants.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}
	
	public static HttpResponse get(String url, String systemUserToken, Map<String, String> additionalHeaders) throws ClientProtocolException {
		HttpGet request = new HttpGet(url);
		for (Map.Entry<String, String> entry: additionalHeaders.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
		HttpResponse response;
		try {
			response = HttpCliConstants.getHttpClient().execute(request);
		} catch (IOException e) {
			throw new ClientProtocolException("Unable to connect to " + url, e);
		}
		return response;
	}

	public static HttpResponse delete(String url, String systemUserToken) throws ClientProtocolException {
		HttpDelete request = new HttpDelete(url);
		HttpResponse response;
		try {
			response = HttpCliConstants.getHttpClient().execute(request);
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
