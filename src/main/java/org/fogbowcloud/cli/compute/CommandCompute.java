package org.fogbowcloud.cli.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
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
	private Boolean isCreateCommand = false;
	
	public static final String DELETE_COMMAND_KEY = "--delete";
	@Parameter(names = { DELETE_COMMAND_KEY }, description = "Delete a compute")
	private Boolean isDeleteCommand = false;
	
	public static final String GET_COMMAND_KEY = "--get";
	@Parameter(names = { GET_COMMAND_KEY }, description = "Get a specific compute")
	private Boolean isGetCommand = false;
	
	public static final String GET_ALL_COMMAND_KEY = "--get-all";
	@Parameter(names = { GET_ALL_COMMAND_KEY }, description = "Get all computes")
	private Boolean isGetAllCommand = false;
	
	public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
	@Parameter(names = { GET_QUOTA_COMMAND_KEY }, description = "Get quota")
	private Boolean isGetQuotaCommand = false;
	
	public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
	@Parameter(names = { GET_ALLOCATION_COMMAND_KEY }, description = "Get alocation")
	private Boolean isGetAllocationCommand = false;
	
	public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
	@Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token", required = true)
	private String federationToken = null;
	
	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url")
	private String url = null;
	
	public static final String ID_COMMAND_KEY = "--id";
	@Parameter(names = { ID_COMMAND_KEY }, description = "Compute id")
	private String id = null;
	
	public static final String MEMBER_ID_COMMAND_KEY = "--member-id";
	@Parameter(names = { MEMBER_ID_COMMAND_KEY }, description = "Member's id")
	private String memberId = null;
	
	@ParametersDelegate
	private Compute compute = new Compute();

	public static final String ENDPOINT = '/' + ComputeOrdersController.COMPUTE_ENDPOINT;

	public String run() throws ClientProtocolException, IOException {
		if (this.isCreateCommand) {
			return doCreateCompute();
		} else if (this.isDeleteCommand) {
			return doDeleteCompute();
		} else if (this.isGetCommand) {
			return doGetCompute();
		} else if (this.isGetAllCommand) {
			return doGetAllCompute();
		} else if (this.isGetQuotaCommand) {
			return doGetQuota();
		} else if (this.isGetAllocationCommand) {
			return doGetAllocation();
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
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	private String doGetAllCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	private String doGetAllocation() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/allocation/" + this.memberId;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}

	private String doGetQuota() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/quota/" + this.memberId;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
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
