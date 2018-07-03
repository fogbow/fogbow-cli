package org.fogbowcloud.cli.order;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;

public class OrderCommand {
	
	public static final String CREATE_COMMAND_KEY = "--create";
	@Parameter(names = { CREATE_COMMAND_KEY })
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
	
	public static final String GET_ALL_STATUS_COMMAND_KEY = "--get-all-status";
	@Parameter(names = { GET_ALL_STATUS_COMMAND_KEY }, description = "Get all computes")
	private Boolean isGetAllStatusCommand = false;

	public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
	@Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token", required = true)
	private String federationToken = null;
	
	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url", required = true)
	private String url = null;
	
	public static final String ID_COMMAND_KEY = "--id";
	@Parameter(names = { ID_COMMAND_KEY }, description = "id")
	private String id = null;
	
	private String endpoint;
	private Object jsonObject;
	public static final String STATUS_ENDPOINT_KEY = "status";
	
	public OrderCommand(String endpoint, Object jsonObject) {
		this.endpoint = endpoint;
		this.jsonObject = jsonObject;
	}
	
	public String doCreate() throws ClientProtocolException, IOException {
		String fullUrl = this.url + this.endpoint;
		HttpResponse httpResponse = HttpUtil.post(fullUrl, jsonToString(), this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	public String doDelete() throws ClientProtocolException, IOException {
		if (this.id == null) {
			throw new ParameterException("No id passed as parameter");
		} else {
			String fullUrl = this.url + this.endpoint + "/" + this.id;
			HttpResponse httpResponse = HttpUtil.delete(fullUrl, this.federationToken);
			return httpResponse.getStatusLine().toString();
		}
	}

	public String doGet() throws ClientProtocolException, IOException {
		if (this.id == null) {
			throw new ParameterException("No id passed as parameter");
		} else {
			String fullUrl = this.url + this.endpoint + "/" + this.id;
			HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}
	
	public String doGetAll() throws ClientProtocolException, IOException {
		String fullUrl = this.url + this.endpoint;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	public String doGetAllStatus() throws IOException, IOException {
		String fullUrl = this.url + this.endpoint + "/" + STATUS_ENDPOINT_KEY;
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	public Boolean getIsCreateCommand() {
		return isCreateCommand;
	}

	public Boolean getIsDeleteCommand() {
		return isDeleteCommand;
	}

	public Boolean getIsGetCommand() {
		return isGetCommand;
	}

	public Boolean getIsGetAllCommand() {
		return isGetAllCommand;
	}
	
	public Boolean getIsGetAllStatusCommand() {
		return isGetAllStatusCommand;
	}

	public String getFederationToken() {
		return federationToken;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}
	
	private String jsonToString() {
		Gson gson = new Gson();
		String computeJson = gson.toJson(this.jsonObject);
		return computeJson;
	}
	
}
