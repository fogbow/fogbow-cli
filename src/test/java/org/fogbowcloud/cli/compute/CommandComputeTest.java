package org.fogbowcloud.cli.compute;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import org.fogbowcloud.cli.order.compute.CommandCompute;
import org.fogbowcloud.cli.order.compute.Compute;
import org.fogbowcloud.manager.utils.HttpRequestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.internal.util.reflection.Whitebox;

import com.google.gson.Gson;

public class CommandComputeTest {

	private CommandCompute spyCommandCompute;

	private HttpClient mockHttpClient;

	private static final String ANY_FEDERATION_TOKEN = "federation-token-value";
	private static final String ANY_URL = "http://localhost/";

	@Before
	public void setUp() throws ClientProtocolException, IOException {
		initCommandCompute();
		initHttpClient();
	}

	@Test
	public void testComputeToJson() {
		String computeJson = this.spyCommandCompute.computeToJson();
		Gson gson = new Gson();
		Compute computeFromJson = gson.fromJson(computeJson, Compute.class);
		Assert.assertThat(this.spyCommandCompute.getCompute(), new ReflectionEquals(computeFromJson, new String[0]));
	}

	@Test
	public void testCreateCompute() throws ClientProtocolException, IOException {
		ArrayList<String> commandCompute = getCreateCommandCompute(this.spyCommandCompute);

		String computeJson = this.spyCommandCompute.computeToJson();
		HttpPost post = new HttpPost(this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		Main.main(commandCompute.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongHeaders() throws ClientProtocolException, IOException {
		String wrongFederationToken = this.spyCommandCompute.getFederationToken() + "anything";

		ArrayList<String> commandCompute = getCreateCommandCompute(this.spyCommandCompute);

		String computeJson = this.spyCommandCompute.computeToJson();
		HttpPost post = new HttpPost(this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, wrongFederationToken);
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		Main.main(commandCompute.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongURI() throws ClientProtocolException, IOException {
		String wrongURI = this.spyCommandCompute.getUrl() + "anything";

		ArrayList<String> commandCompute = getCreateCommandCompute(this.spyCommandCompute);

		String computeJson = this.spyCommandCompute.computeToJson();
		HttpPost post = new HttpPost(wrongURI + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		Main.main(commandCompute.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongCommandKey() throws ClientProtocolException, IOException {
		String wrongProvidingMemberCommandKey = Compute.PROVIDING_MEMBER_COMMAND_KEY + "anything";

		ArrayList<String> commandCompute = getCreateCommandCompute(this.spyCommandCompute);

		commandCompute.set(commandCompute.indexOf(Compute.PROVIDING_MEMBER_COMMAND_KEY),
				wrongProvidingMemberCommandKey);

		String computeJson = this.spyCommandCompute.computeToJson();
		HttpPost post = new HttpPost(this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		Main.main(commandCompute.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	@Test(expected = AssertionError.class)
	public void testCreateComputeWhenWrongCommandValue() throws ClientProtocolException, IOException {
		String wrongProvidingMemberCommandValue = "anything";

		ArrayList<String> commandCompute = getCreateCommandCompute(this.spyCommandCompute);

		int providingMemberValueIndex = commandCompute.indexOf(Compute.PROVIDING_MEMBER_COMMAND_KEY) + 1;
		commandCompute.set(providingMemberValueIndex, wrongProvidingMemberCommandValue);

		String computeJson = this.spyCommandCompute.computeToJson();
		HttpPost post = new HttpPost(this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT);
		post.setEntity(new StringEntity(computeJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		post.setHeader(HttpRequestUtil.CONTENT_TYPE_KEY, HttpRequestUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedPostRequest = new HttpRequestMatcher(post);

		Main.main(commandCompute.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedPostRequest));
	}

	@Test
	public void testDelete() throws ClientProtocolException, IOException {

		String id = "any-compute-id";

		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add(CommandCompute.NAME);
		stringBuilder.add(CommandCompute.DELETE_COMMAND_KEY);
		stringBuilder.add(CommandCompute.FEDERATION_TOKEN_COMMAND_KEY);
		stringBuilder.add(this.spyCommandCompute.getFederationToken());
		stringBuilder.add(CommandCompute.URL_COMMAND_KEY);
		stringBuilder.add(this.spyCommandCompute.getUrl());
		stringBuilder.add(CommandCompute.ID_COMMAND_KEY);
		stringBuilder.add(id);

		HttpUtil.setHttpClient(this.mockHttpClient);

		String fullUrl = this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT + "/" + id;
		HttpDelete delete = new HttpDelete(fullUrl);
		delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		HttpRequestMatcher expectedDeleteRequest = new HttpRequestMatcher(delete);

		Main.main(stringBuilder.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedDeleteRequest));
	}

	@Test
	public void testGet() throws ClientProtocolException, IOException {

		String id = "any-compute-id";

		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add(CommandCompute.NAME);
		stringBuilder.add(CommandCompute.GET_COMMAND_KEY);
		stringBuilder.add(CommandCompute.FEDERATION_TOKEN_COMMAND_KEY);
		stringBuilder.add(this.spyCommandCompute.getFederationToken());
		stringBuilder.add(CommandCompute.URL_COMMAND_KEY);
		stringBuilder.add(this.spyCommandCompute.getUrl());
		stringBuilder.add(CommandCompute.ID_COMMAND_KEY);
		stringBuilder.add(id);

		HttpUtil.setHttpClient(this.mockHttpClient);

		String fullUrl = this.spyCommandCompute.getUrl() + CommandCompute.ENDPOINT + "/" + id;
		HttpGet get = new HttpGet(fullUrl);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, this.spyCommandCompute.getFederationToken());
		HttpRequestMatcher expectedGetRequest = new HttpRequestMatcher(get);

		Main.main(stringBuilder.stream().toArray(String[]::new));

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedGetRequest));
	}

	@Test
	public void testCommandComputeWithoutCommandName() throws IOException {
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add(CommandCompute.NAME);
		stringBuilder.add(CommandCompute.FEDERATION_TOKEN_COMMAND_KEY);
		stringBuilder.add(this.spyCommandCompute.getFederationToken());

		Main.main(stringBuilder.stream().toArray(String[]::new));
	}

	private void initCommandCompute() {
		this.spyCommandCompute = Mockito.spy(new CommandCompute());
		this.spyCommandCompute.setFederationToken(ANY_FEDERATION_TOKEN);
		this.spyCommandCompute.setUrl(ANY_URL);

		Compute compute = new Compute();
		for (Field field : Compute.class.getDeclaredFields()) {
			if (!Modifier.isFinal(field.getModifiers())) {
				Whitebox.setInternalState(compute, field.getName(), field.getName());
			}
		}
		spyCommandCompute.setCompute(compute);
	}

	private void initHttpClient() throws ClientProtocolException, IOException {
		this.mockHttpClient = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "Return Irrelevant"), null);
		Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		HttpUtil.setHttpClient(this.mockHttpClient);
	}

	private ArrayList<String> getCreateCommandCompute(CommandCompute commandCompute) {
		ArrayList<String> stringBuilder = new ArrayList<String>();
		stringBuilder.add(CommandCompute.NAME);
		stringBuilder.add(CommandCompute.CREATE_COMMAND_KEY);
		stringBuilder.add(CommandCompute.FEDERATION_TOKEN_COMMAND_KEY);
		stringBuilder.add(commandCompute.getFederationToken());
		stringBuilder.add(Compute.PROVIDING_MEMBER_COMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getProvidingMember());
		stringBuilder.add(Compute.PUBLIC_KEY_COMMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getPublicKey());
		stringBuilder.add(Compute.IMAGE_ID_COMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getImageId());
		stringBuilder.add(Compute.VCPU_COMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getvCPU());
		stringBuilder.add(Compute.MEMORY_COMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getMemory());
		stringBuilder.add(Compute.DISC_COMMAND_KEY);
		stringBuilder.add(commandCompute.getCompute().getDisk());
		stringBuilder.add(CommandCompute.URL_COMMAND_KEY);
		stringBuilder.add(commandCompute.getUrl());
		return stringBuilder;
	}
}
