package org.fogbowcloud.cli.compute;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;

@Parameters(separators = "=", commandDescription = "Compute manipulation")
public class CommandCompute {
	
	public static final String NAME = "compute";
	
	@Parameter(names = { "--create", "-c" }, description = "Creates a new compute")
	private Boolean isCreation = false;
	
	@Parameter(names = { "--federated-token", "-ft" }, description = "User's Token", required = true)
	private String federatedToken = null;
	
	@Parameter(names = { "--local-token", "-lt" }, description = "Local token")
	private String localToken = null;
	
	@Parameter(names = { "--url", "-url" }, description = "Url")
	private String url = null;
	
	@ParametersDelegate
	private Compute compute = new Compute(); 
	
	public String run() throws ClientProtocolException, IOException {
		if (this.isCreation) {
			return createCompute();
		}
		return null;
	}

	private String createCompute() throws ClientProtocolException, IOException {
		HttpResponse httpRespose = HttpUtil.post(this.url, computeToJson(), this.federatedToken);
		return httpRespose.getStatusLine().toString();
	}
	
	protected String computeToJson() {
		Gson gson = new Gson();
		String computeJson = gson.toJson(compute);
		return computeJson;
	}
}
