package org.fogbowcloud.cli.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class CommandCompute {
	
	public static final String NAME = "compute";
	
	@Parameter(names = {"--create"}, description = "Create a new compute")
	private Boolean isCreate = false;
	
	@Parameter(names = {"--delete"}, description = "Delete a compute")
	private Boolean isDelete = false;
	
	@Parameter(names = {"--get"}, description = "Get a specific compute")
	private Boolean isGet = false;
	
	@Parameter(names = { "--federated-token", "-ft" }, description = "User's Token", required = true)
	private String federatedToken = null;
	
	@Parameter(names = { "--url" }, description = "Url")
	private String url = null;
	
	@Parameter(names = { "--id" }, description = "Compute id")
	private String id = null;
	
	@ParametersDelegate
	private Compute compute = new Compute(); 
	
	public static final String ENDPOINT = "/compute";
	
	public String run() throws ClientProtocolException, IOException {
		if (this.isCreate) {
			return createCompute();
		} else if (this.isDelete) {
			return deleteCompute();
		} else if (this.isGet) {
			return getCompute();
		}
		throw new ParameterException("command is incomplete");
	}

	private String createCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		HttpResponse httpResponse = HttpUtil.post(fullUrl, computeToJson(), this.federatedToken);
		return httpResponse.getStatusLine().toString();
	}
	
	private String deleteCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.delete(fullUrl, this.federatedToken);
		return httpResponse.getStatusLine().toString();
	}
	
	private String getCompute() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federatedToken);
		return httpResponse.getStatusLine().toString();
	}
	
	protected String computeToJson() {
		Gson gson = new Gson();
		String computeJson = gson.toJson(this.compute);
		return computeJson;
	}
}
