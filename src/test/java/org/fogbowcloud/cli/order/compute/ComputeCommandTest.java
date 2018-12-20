package org.fogbowcloud.cli.order.compute;

import java.io.IOException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.cli.utils.KeyValueUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

public class ComputeCommandTest {
	
	private Compute compute;
	private ComputeCommand computeCommand = new ComputeCommand();
	private HttpClient mockHttpClient;

	private final String url = "my-url";
	private final String token = "my-token";
	private final String id = "my-id";
	private final String memberId = "my-member-id";
	private final String requirementsString = "key1=value1,key2=value2";
	private Map<String, String> requirements = new HashMap<>();

	@Before
	public void setUp() throws FogbowCLIException, IOException {
		requirements = new KeyValueUtil.KeyValueConverter().convert(requirementsString);
		this.compute = new Compute(
				"my-provider",
				"",
				"my-image-id", 
				"my-vcpu-count",
				"my-memory", 
				"my-disk",
				Arrays.asList(new String[] {"fake-user-data-1", "fake-user-data-2"}),
				Arrays.asList(new String[] {"fake-network-id-1", "fake-network-id-2"}),
				"compute-name",
				requirements
		);
		this.computeCommand = new ComputeCommand();
		initHttpClient();
	}
	
	@Test
	public void testRunCreateCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
		    .addObject(this.computeCommand)
		    .build()
		    .parse(
		    		OrderCommand.CREATE_COMMAND_KEY, 
		    		OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
		    		OrderCommand.URL_COMMAND_KEY, this.url,
		    		Compute.PROVIDER_COMMAND_KEY, this.compute.getProvider(),
		    		Compute.IMAGE_ID_COMMAND_KEY, this.compute.getImageId(),
		    		Compute.VCPU_COMMAND_KEY, this.compute.getvCPU(),
		    		Compute.MEMORY_COMMAND_KEY, this.compute.getMemory(),
		    		Compute.DISC_COMMAND_KEY, this.compute.getDisk(),
		    		Compute.NETWORK_IDS_COMMAND_KEY, separateBy(this.compute.getNetworksId(), ","),
					Compute.NAME_COMMAND_KEY, this.compute.getName(),
					Compute.USER_DATA_COMMAND_KEY, separateBy(this.compute.getUserData(), ","),
					Compute.REQUIREMENTS, requirementsString
		    );

		ComputeWrappedWithFedNet computeWrappedWithFedNet = new ComputeWrappedWithFedNet(this.compute);

		String expectedJson = new Gson().toJson(computeWrappedWithFedNet);
		HttpPost post = new HttpPost(this.url + ComputeCommand.ENDPOINT);
		post.setEntity(new StringEntity(expectedJson));
		post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		post.setHeader(HttpUtil.CONTENT_TYPE_KEY, HttpUtil.JSON_CONTENT_TYPE_KEY);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(post);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}

	private String separateBy(Collection<String> networkIds, String separator) {
		StringBuilder result = new StringBuilder();
		for (String networkId : networkIds) {
			result.append(networkId);
			result.append(separator);
		}

		if (result.length() > 0) {
			// remove ending space
			result.deleteCharAt(result.length() - 1);
		}

		return result.toString();
	}

	@Test
	public void testRunDeleteCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					OrderCommand.DELETE_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpDelete delete = new HttpDelete(this.url + ComputeCommand.ENDPOINT + '/' + this.id);
		delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(delete);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					OrderCommand.GET_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + ComputeCommand.ENDPOINT + '/' + this.id);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetAllCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					OrderCommand.GET_ALL_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + ComputeCommand.ENDPOINT );
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetAllStatusCommand() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					OrderCommand.GET_ALL_STATUS_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					OrderCommand.ID_COMMAND_KEY, this.id);

		HttpGet get = new HttpGet(this.url + ComputeCommand.ENDPOINT + "/" +  OrderCommand.STATUS_ENDPOINT_KEY);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	@Test
	public void testRunGetQuota() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					ComputeCommand.GET_QUOTA_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					ComputeCommand.MEMBER_ID_COMMAND_KEY, this.memberId);

		HttpGet get = new HttpGet(this.url + ComputeCommand.ENDPOINT + ComputeCommand.QUOTA_ENDPOINT_KEY + this.memberId);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}
	
	
	@Test
	public void testRunGetAllocation() throws FogbowCLIException, IOException {
		JCommander.newBuilder()
				.addObject(this.computeCommand)
				.build()
				.parse(
					ComputeCommand.GET_ALLOCATION_COMMAND_KEY,
					OrderCommand.FEDERATION_TOKEN_COMMAND_KEY, this.token,
					OrderCommand.URL_COMMAND_KEY, this.url,
					ComputeCommand.MEMBER_ID_COMMAND_KEY, this.memberId);

		HttpGet get = new HttpGet(this.url + ComputeCommand.ENDPOINT + ComputeCommand.ALLOCATION_ENDPOINT_KEY + this.memberId);
		get.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, token);
		HttpRequestMatcher expectedRequest = new HttpRequestMatcher(get);

		this.computeCommand.run();

		Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
	}

	private void initHttpClient() throws FogbowCLIException, IOException {
		this.mockHttpClient = Mockito.mock(HttpClient.class);
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		HttpResponse response = factory.newHttpResponse(
				new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "Return Irrelevant"), null);
		response.setEntity(new StringEntity("{}"));
		Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
		HttpUtil.setHttpClient(this.mockHttpClient);
	}

}
