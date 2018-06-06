package org.fogbowcloud.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.mockito.ArgumentMatcher;

public class HttpRequestMatcher extends ArgumentMatcher<HttpRequestBase> {

	private HttpRequestBase actualRequest;
	private HttpRequestBase comparedRequest;

	public HttpRequestMatcher(HttpRequestBase request) {
		this.actualRequest = request;
	}

	public boolean matches(Object object) {

		this.comparedRequest = (HttpRequestBase) object;

		if (checkEntity() && checkURI() && checkHeaders() && checkType()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkHeaders() {

		Map<String, String> actualHeaders = Arrays.asList(this.actualRequest.getAllHeaders()).stream()
				.collect(Collectors.toMap(Header::getName, Header::getValue));

		Map<String, String> comparedHeaders = Arrays.asList(this.comparedRequest.getAllHeaders()).stream()
				.collect(Collectors.toMap(Header::getName, Header::getValue));

		return actualHeaders.equals(comparedHeaders);

	}

	private boolean checkURI() {
		return this.actualRequest.getURI().equals(this.comparedRequest.getURI());
	}

	private boolean checkEntity() {

		if (this.actualRequest.getMethod().equals(HttpPost.METHOD_NAME)) {
			HttpPost actualPostRequest = (HttpPost) this.actualRequest;
			HttpPost comparedPostRequest = (HttpPost) this.comparedRequest;
			try {
				String actualEntity = EntityUtils.toString(actualPostRequest.getEntity());
				String comparedEntity = EntityUtils.toString(comparedPostRequest.getEntity());
				return comparedEntity.equals(actualEntity);
			} catch (ParseException | IOException e) {
				return false;
			}

		} else {
			return true;
		}
	}

	private boolean checkType() {
		return this.actualRequest.getMethod().equals(this.comparedRequest.getMethod());
	}
}