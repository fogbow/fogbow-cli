package org.fogbowcloud.cli;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.mockito.ArgumentMatcher;

public class HttpPostRequestMatcher extends ArgumentMatcher<HttpPost> {

	private HttpPost postRequest;

	public HttpPostRequestMatcher(HttpPost post) {
		this.postRequest = post;
	}

	public boolean matches(Object object) {

		HttpPost comparedPostRequest = (HttpPost) object;
		if (!this.postRequest.getURI().equals(comparedPostRequest.getURI())) {
			return false;
		}
		if (!checkHeaders(comparedPostRequest.getAllHeaders())) {
			return false;
		}
		if (!this.postRequest.getEntity().equals(comparedPostRequest.getEntity())) {
			return false;
		}
		return true;
	}

	public boolean checkHeaders(Header[] comparedHeaders) {
		for (Header header: this.postRequest.getAllHeaders()) {
			boolean found = false;
			for (Header comparedHeader: comparedHeaders) {
				if (header.getName().equals(comparedHeader.getName())) {
					if (header.getValue().equals(comparedHeader.getValue())) {
						found = true;
					} else {
						return false;
					}
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
}