package cloud.fogbow.cli.ras.order;

import java.io.IOException;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.utils.CommandUtil;
import org.apache.http.HttpResponse;
import cloud.fogbow.cli.HttpUtil;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

public class OrderCommand {
	public static final String CREATE_COMMAND_KEY = "--create";
	public static final String DELETE_COMMAND_KEY = "--delete";
	public static final String STATUS_ENDPOINT_KEY = "status";

	@Parameter(names = CREATE_COMMAND_KEY, description = Documentation.Order.CREATE)
	private Boolean isCreateCommand = false;

	@Parameter(names = DELETE_COMMAND_KEY, description = Documentation.Order.DELETE)
	private Boolean isDeleteCommand = false;
	
	@Parameter(names = CliCommonParameters.GET_COMMAND_KEY, description = Documentation.Order.GET)
	private Boolean isGetCommand = false;

	@Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Order.GET_ALL)
	private Boolean isGetAllCommand = false;

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL)
	private String url;

	@Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
	private String systemUserToken = null;

	@Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
	private String systemUserTokenPath = null;

	@Parameter(names = CliCommonParameters.ID_COMMAND_KEY, description = Documentation.Order.ID)
	protected String id = null;

	@Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID)
	private String memberId = null;

	@Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME)
	private String cloudName = null;

	private String endpoint;
	private Object jsonObject;

	public OrderCommand(String endpoint, Object jsonObject) {
		this.endpoint = endpoint;
		this.jsonObject = jsonObject;
	}
	
	public String doCreate() throws IOException, FogbowCLIException {
		return doCreate(jsonToString());
	}
	
	public String doCreate(String json) throws IOException, FogbowCLIException {
		String fullUrl = this.url + this.endpoint;
		String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
		HttpResponse httpResponse = HttpUtil.post(fullUrl, json, systemUserToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
	
	public String doDelete() throws IOException, FogbowCLIException {
		if (this.id == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String fullUrl = this.url + this.endpoint + "/" + this.id;
			String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
			HttpResponse httpResponse = HttpUtil.delete(fullUrl, systemUserToken);
			return httpResponse.getStatusLine().toString();
		}
	}

	public String doGet() throws IOException, FogbowCLIException {
		if (this.id == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String fullUrl = this.url + this.endpoint + "/" + this.id;
			String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
			HttpResponse httpResponse = HttpUtil.get(fullUrl, systemUserToken);
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}

	public String doGetAll() throws IOException, FogbowCLIException {
		String fullUrl = this.url + this.endpoint + "/" + STATUS_ENDPOINT_KEY;
		String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
		HttpResponse httpResponse = HttpUtil.get(fullUrl, systemUserToken);
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}

	public String getSystemUserToken() throws FogbowCLIException {
		return CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
	}

	public String getMemberId() {
		return this.memberId;
	}

	public String getCloudName() {
		return this.cloudName;
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
