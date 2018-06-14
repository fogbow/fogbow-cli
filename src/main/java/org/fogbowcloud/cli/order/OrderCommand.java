package org.fogbowcloud.cli.order;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.beust.jcommander.Parameter;

public abstract class OrderCommand {
	
	public static final String CREATE_COMMAND_KEY = "--create";
	@Parameter(names = { CREATE_COMMAND_KEY })
	protected Boolean isCreateCommand = false;
	
	public static final String DELETE_COMMAND_KEY = "--delete";
	@Parameter(names = { DELETE_COMMAND_KEY }, description = "Delete a compute")
	protected Boolean isDeleteCommand = false;
	
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
	
	protected abstract String doDelete() throws ClientProtocolException, IOException;
	protected abstract String doCreate() throws ClientProtocolException, IOException;
	protected abstract String doGet() throws ClientProtocolException, IOException;
	
}
