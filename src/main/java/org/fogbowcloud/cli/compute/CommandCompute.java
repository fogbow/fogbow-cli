package org.fogbowcloud.cli.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.manager.api.http.ComputeOrdersController;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class CommandCompute {

	public static final String NAME = "compute";
	
	public static final String CREATE_COMMAND_KEY = "--create";
	@Parameter(names = { CREATE_COMMAND_KEY }, description = "Create a new compute")
	private Boolean isCreate = false;
	
	public static final String DELETE_COMMAND_KEY = "--delete";
	@Parameter(names = { DELETE_COMMAND_KEY }, description = "Delete a compute")
	private Boolean isDelete = false;
	
	public static final String GET_COMMAND_KEY = "--get";
	@Parameter(names = { GET_COMMAND_KEY }, description = "Get a specific compute")
	private Boolean isGet = false;
	
	public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token";
	@Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token", required = true)
	private String federationToken = null;
	
	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url")
	private String url = null;
	
	public static final String ID_COMMAND_KEY = "--id";
	@Parameter(names = { ID_COMMAND_KEY }, description = "Compute id")
	private String id = null;

	@ParametersDelegate
	private Compute compute = new Compute();

	public static final String ENDPOINT = ComputeOrdersController.COMPUTE_ENDPOINT;

	public String run() throws ClientProtocolException, IOException {
		if (this.isCreate) {
			return doCreateCompute();
		} else if (this.isDelete) {
			return doDeleteCompute();
		} else if (this.isGet) {
			return doGetCompute();
		}
		throw new ParameterException("command is incomplete");
	}

	private String doCreateCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.post(fullUrl, computeToJson(), this.federationToken);
		return httpResponse.getStatusLine().toString();
	}

	private String doDeleteCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.delete(fullUrl, this.federationToken);
		return httpResponse.getStatusLine().toString();
	}

	private String doGetCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return httpResponse.getStatusLine().toString();
	}

	protected String computeToJson() {
		Gson gson = new Gson();
		String computeJson = gson.toJson(this.compute);
		return computeJson;
	}
	
	protected Compute getCompute() {
		return this.compute;
	}
	
	protected void setCompute(Compute compute) {
		this.compute = compute;
	}
	
	protected void setFederationToken(String federationToken) {
		this.federationToken = federationToken;
	}
	
	protected String getFederationToken() {
		return this.federationToken;
	}
	
	protected String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}
}
