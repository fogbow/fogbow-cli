package org.fogbowcloud.cli.compute;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpRequestMatcher;
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

	private CommandCompute mockCommandCompute;
	
	private HttpClient mockHttpClient;
	
	@Before
	public void setUp() throws ClientProtocolException, IOException {
		this.mockCommandCompute = Mockito.spy(new CommandCompute());
		
		this.mockHttpClient = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "Return Irrelevant"), null);
		Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		
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

		Whitebox.setInternalState(this.mockCommandCompute, "compute", compute);

		String computeJson = this.mockCommandCompute.computeToJson();

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
		
		Whitebox.setInternalState(this.compute, "providingMember", "providingmember");
		Whitebox.setInternalState(this.compute, "publicKey", "publickey");
		Whitebox.setInternalState(this.compute, "imageName", "imagename");
		Whitebox.setInternalState(this.compute, "vCPU", "vcpu");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		Whitebox.setInternalState(this.mockCommandCompute, "compute", compute);
		
		String computeJson = this.mockCommandCompute.computeToJson();

		HttpUtil.setHttpClient(this.mockHttpClient);
		
		HttpPost post = new HttpPost(url + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader(HttpUtil.JSONHeader.getName(), HttpUtil.JSONHeader.getValue());

		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongHeaders() throws ClientProtocolException, IOException {
		String url = "http://localhost/";
		String correctFederatedToken = "federatedToken";
		String wrongFederatedToken = "federatedTokenWrong";
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--create");
		stringBuilder.add("--federated-token");
		stringBuilder.add(correctFederatedToken);
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
		
		Whitebox.setInternalState(this.compute, "providingMember", "providingmember");
		Whitebox.setInternalState(this.compute, "publicKey", "publickey");
		Whitebox.setInternalState(this.compute, "imageName", "imagename");
		Whitebox.setInternalState(this.compute, "vCPU", "vcpu");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		Whitebox.setInternalState(this.mockCommandCompute, "compute", compute);
		
		String computeJson = this.mockCommandCompute.computeToJson();
		
		HttpUtil.setHttpClient(this.mockHttpClient);
		
		HttpPost post = new HttpPost(url + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, wrongFederatedToken);
		post.setHeader(HttpUtil.JSONHeader.getName(), HttpUtil.JSONHeader.getValue());
		
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongURI() throws ClientProtocolException, IOException {
		
		String correctUrl = "http://localhost/";
		String wrongUrl = "http://localhost_wrong/" ;
		
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
		stringBuilder.add(correctUrl);
		
		Whitebox.setInternalState(this.compute, "providingMember", "providingmember");
		Whitebox.setInternalState(this.compute, "publicKey", "publickey");
		Whitebox.setInternalState(this.compute, "imageName", "imagename");
		Whitebox.setInternalState(this.compute, "vCPU", "vcpu");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		Whitebox.setInternalState(this.mockCommandCompute, "compute", compute);
		
		String computeJson = this.mockCommandCompute.computeToJson();
		
		HttpUtil.setHttpClient(this.mockHttpClient);
		
		HttpPost post = new HttpPost(wrongUrl + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader(HttpUtil.JSONHeader.getName(), HttpUtil.JSONHeader.getValue());
		
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongEntityJson() throws ClientProtocolException, IOException {
		String url = "http://localhost/";
		String federatedToken = "federatedToken";
		
		String wrongProvidingMember = "providingmemberwrong";
		String correctProvidingMember = "providingmember";
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--create");
		stringBuilder.add("--federated-token");
		stringBuilder.add(federatedToken);
		stringBuilder.add("--providing-member");
		stringBuilder.add(correctProvidingMember);
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
		
		Whitebox.setInternalState(this.compute, "providingMember", wrongProvidingMember);
		Whitebox.setInternalState(this.compute, "publicKey", "publickey");
		Whitebox.setInternalState(this.compute, "imageName", "imagename");
		Whitebox.setInternalState(this.compute, "vCPU", "vcpu");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		Whitebox.setInternalState(this.mockCommandCompute, "compute", compute);
		
		String computeJson = this.mockCommandCompute.computeToJson();
		
		HttpUtil.setHttpClient(this.mockHttpClient);
		
		HttpPost post = new HttpPost(url + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		post.setHeader(HttpUtil.JSONHeader.getName(), HttpUtil.JSONHeader.getValue());

		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}
	
	@Test
	public void testDelete() throws ClientProtocolException, IOException {
		
		String federatedToken = "federatedToken";
		String id = "any-compute-id";
		String url = "http://localhost";
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--delete");
		stringBuilder.add("--federated-token");
		stringBuilder.add(federatedToken);
		stringBuilder.add("--url");
		stringBuilder.add(url);
		stringBuilder.add("--id");
		stringBuilder.add(id);
		
		HttpUtil.setHttpClient(this.mockHttpClient);
		
		String fullUrl = url + CommandCompute.ENDPOINT + "/" + id;
		HttpDelete delete = new HttpDelete(fullUrl);
		delete.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		HttpRequestMatcher expectedDeleteRequest = new HttpRequestMatcher(delete);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedDeleteRequest));
	}
	
	@Test
	public void testGet() throws ClientProtocolException, IOException {
		
		String federatedToken = "federatedToken";
		String id = "any-compute-id";
		String url = "http://localhost";
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--get");
		stringBuilder.add("--federated-token");
		stringBuilder.add(federatedToken);
		stringBuilder.add("--url");
		stringBuilder.add(url);
		stringBuilder.add("--id");
		stringBuilder.add(id);
		
		HttpUtil.setHttpClient(this.mockHttpClient);
		
		String fullUrl = url + CommandCompute.ENDPOINT + "/" + id;
		HttpGet get = new HttpGet(fullUrl);
		get.setHeader(HttpUtil.FEDERATED_TOKEN_HEADER, federatedToken);
		HttpRequestMatcher expectedGetRequest = new HttpRequestMatcher(get);
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
		
		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedGetRequest));
	}
	
	@Test
	public void testCommandComputeWithoutCommandName() {
		
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add("compute");
		stringBuilder.add("--federated-token");
		stringBuilder.add("token");
		
		Main.main(stringBuilder.stream().toArray(String[]::new));
	}
}
