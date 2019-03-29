package cloud.fogbow.cli.ras.order;

import java.util.HashMap;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.ParametersDelegate;

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

	@Parameter(names = CliCommonParameters.ID_COMMAND_KEY, description = Documentation.Order.ID)
	protected String id = null;

	@Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID)
	private String memberId = null;

	@Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME)
	private String cloudName = null;

	@ParametersDelegate
	private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

	private String endpoint;
	private Object jsonObject;

	public OrderCommand(String endpoint, Object jsonObject) {
		this.endpoint = endpoint;
		this.jsonObject = jsonObject;
	}
	
	public String doCreate() throws FogbowException {
		return doCreate(this.jsonObject);
	}
	
	public String doCreate(Object json) throws FogbowException {
		String fullPath = this.endpoint;
		return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.POST, fullPath, (HashMap) json);
	}
	
	public String doDelete() throws FogbowException {
		if (this.id == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String fullPath = this.endpoint + "/" + this.id;
			return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.DELETE, fullPath);
		}
	}

	public String doGet() throws FogbowException {
		if (this.id == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String fullPath = this.endpoint + "/" + this.id;
			return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);
		}
	}

	public String doGetAll() throws FogbowException {
		String fullPath = this.endpoint + "/" + STATUS_ENDPOINT_KEY;
		return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);
	}

	public String getSystemUserToken() throws FogbowCLIException {
		// TODO: remove this method
		return "Remove me later";
//		return CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
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

	public FogbowCliHttpUtil getFogbowCliHttpUtil() {
		return fogbowCliHttpUtil;
	}

	private String jsonToString() {
	    Gson gson = new Gson();
		System.out.println(this.jsonObject);
	    String computeJson = gson.toJson(this.jsonObject);
	    return computeJson;
    }
}
