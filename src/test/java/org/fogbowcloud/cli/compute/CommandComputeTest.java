package org.fogbowcloud.cli.compute;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpPostRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.internal.util.reflection.Whitebox;

import com.google.gson.Gson;

public class CommandComputeTest {

	private Compute compute;

	private CommandCompute commandCompute;

	@Before
	public void setUp() {
		this.commandCompute = Mockito.spy(new CommandCompute());
		this.compute = new Compute();
	}

	@Test
	public void testComputeToJson() {

		Whitebox.setInternalState(this.compute, "providingMember", "providingMember");
		Whitebox.setInternalState(this.compute, "publicKey", "publicKey");
		Whitebox.setInternalState(this.compute, "imageName", "imageName");
		Whitebox.setInternalState(this.compute, "vCPU", "vCPU");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");

		Whitebox.setInternalState(commandCompute, "compute", compute);

		String computeJson = commandCompute.computeToJson();

		Gson gson = new Gson();
		Compute computeFromJson = gson.fromJson(computeJson, Compute.class);

		Assert.assertThat(compute, new ReflectionEquals(computeFromJson, new String[0]));
	}

	@Test
	public void testCreateCompute() throws ClientProtocolException, IOException {
		
		String url = "http://localhost/";
		String federatedToken = "federatedToken";
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--create");
		stringBuilder.add("--federated-token");
		stringBuilder.add(federatedToken);
		stringBuilder.add("--providing-member");
		stringBuilder.add("providingmember");
		stringBuilder.add("--public-key");
		stringBuilder.add("publickey");
		stringBuilder.add("--image-name");
		stringBuilder.add("imagename");
		stringBuilder.add("--vcpu");
		stringBuilder.add("vcpu");
		stringBuilder.add("--memory");
		stringBuilder.add("memory");
		stringBuilder.add("--disk");
		stringBuilder.add("disk");
		stringBuilder.add("--url");
		stringBuilder.add(url);
		
		Whitebox.setInternalState(this.compute, "providingMember", "providingMember");
		Whitebox.setInternalState(this.compute, "publicKey", "publicKey");
		Whitebox.setInternalState(this.compute, "imageName", "imageName");
		Whitebox.setInternalState(this.compute, "vCPU", "vCPU");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		Whitebox.setInternalState(commandCompute, "compute", compute);
		
		String computeJson = commandCompute.computeToJson();
		
		HttpClient httpClient = Mockito.mock(HttpClient.class);
		
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "Return Irrelevant"), null);
		Mockito.when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		
		HttpUtil.setHttpClient(httpClient);
		
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader("Content-type", "\"application/json\"");
		
		HttpPostRequestMatcher expectedPostRequest = new HttpPostRequestMatcher(post);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(httpClient).execute(Mockito.argThat(expectedPostRequest));
		
	}

}
