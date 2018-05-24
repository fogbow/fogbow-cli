package org.fogbowcloud.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.mockito.ArgumentMatcher;

public class HttpPostRequestMatcher extends ArgumentMatcher<HttpPost> {

	private HttpPost actualPostRequest;
	private HttpPost comparedPostRequest;

	public HttpPostRequestMatcher(HttpPost post) {
		this.actualPostRequest = post;
	}

	public boolean matches(Object object) {

		this.comparedPostRequest = (HttpPost) object;

		if (checkEntity() && checkURI() && checkHeaders()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkHeaders() {

		Map<String, String> actualHeaders = Arrays.asList(this.actualPostRequest.getAllHeaders()).stream()
				.collect(Collectors.toMap(Header::getName, Header::getValue));

		Map<String, String> comparedHeaders = Arrays.asList(this.comparedPostRequest.getAllHeaders()).stream()
				.collect(Collectors.toMap(Header::getName, Header::getValue));
		
		return actualHeaders.equals(comparedHeaders);

	}
	
	private boolean checkURI() {
		return this.actualPostRequest.getURI().equals(this.comparedPostRequest.getURI());
	}
	
	private boolean checkEntity() {
		try {
			String comparedEntity = EntityUtils.toString(this.comparedPostRequest.getEntity());
			String actualEntity = EntityUtils.toString(this.actualPostRequest.getEntity());

			return comparedEntity.equals(actualEntity);
			
		} catch (ParseException | IOException e) {
			return false;
		}
	}
}