package org.fogbowcloud.cli.image;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.manager.api.http.ImageRequestController;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class ImageCommand {
	public static final String NAME = "image";
	public static final String ENDPOINT = '/' + ImageRequestController.IMAGE_ENDPOINT;
	
	public static final String GET_COMMAND_KEY = "--get";
	@Parameter(names = { GET_COMMAND_KEY }, description = "Get a specific compute")
	protected Boolean isGetCommand = false;
	
	public static final String GET_ALL_COMMAND_KEY = "--get-all";
	@Parameter(names = { GET_ALL_COMMAND_KEY }, description = "Get all computes")
	protected Boolean isGetAllCommand = false;
	
	public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
	@Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token", required = true)
	protected String federationToken = null;
	
	public static final String URL_COMMAND_KEY =  "--url";
	@Parameter(names = { URL_COMMAND_KEY }, description = "Url")
	protected String url = null;
	
	public static final String ID_COMMAND_KEY = "--id";
	@Parameter(names = { ID_COMMAND_KEY }, description = "id")
	protected String id = null;
	
	public static final String MEMBER_ID_COMMAND_KEY = "--member-id";
	@Parameter(names = { MEMBER_ID_COMMAND_KEY }, description = "Member's id")
	private String memberId = null;
	
	private static final String MEMBER_ID_HEADER_KEY = "memberId";
	
	public String run() throws ClientProtocolException, IOException {
		if (this.isGetCommand) {
			return this.doGet();
		} else if (this.isGetAllCommand) {
			return this.doGetAll();
		}
		throw new ParameterException("command is incomplete");
	}
	
	public String doGet() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.id;
		Map<String, String> additionalHeaders = new HashMap<String, String>();
		additionalHeaders.put(MEMBER_ID_HEADER_KEY, this.memberId);
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken, additionalHeaders);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	public String doGetAll() throws ClientProtocolException, IOException {
		String fullUrl = this.url + ENDPOINT;
		Map<String, String> additionalHeaders = new HashMap<String, String>();
		additionalHeaders.put(MEMBER_ID_HEADER_KEY, this.memberId);
		HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken, additionalHeaders);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
}
