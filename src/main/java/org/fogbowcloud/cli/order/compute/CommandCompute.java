package org.fogbowcloud.cli.order.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.order.OrderCommand;
import org.fogbowcloud.manager.api.http.ComputeOrdersController;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class CommandCompute extends OrderCommand {

	public static final String NAME = "compute";
	
	public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
	@Parameter(names = { GET_QUOTA_COMMAND_KEY }, description = "Get quota")
	private Boolean isGetQuotaCommand = false;
	
	public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
	@Parameter(names = { GET_ALLOCATION_COMMAND_KEY }, description = "Get alocation")
	private Boolean isGetAllocationCommand = false;
	
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
		return HttpUtil.getHttpEntityAsString(httpResponse);
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
