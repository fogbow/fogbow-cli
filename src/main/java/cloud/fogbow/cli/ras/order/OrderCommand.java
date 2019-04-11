package cloud.fogbow.cli.ras.order;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.ras.FogbowCliResource;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

import java.util.HashMap;

public class OrderCommand {
	public static final String CREATE_COMMAND_KEY = "--create";
	public static final String DELETE_COMMAND_KEY = "--delete";
	public static final String STATUS_ENDPOINT_KEY = "status";

	public static final String CLOUD_NAME_KEY = "cloudName";
	public static final String MEMBER_ID_KEY = "memberId";
	public static final String ID_KEY = "id";

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
	private FogbowCliResource fogbowCliResource;

	public OrderCommand(String endpoint, FogbowCliResource fogbowCliResource) {
		this.endpoint = endpoint;
		this.fogbowCliResource = fogbowCliResource;
	}

	public String doCreate() throws FogbowException {
		return doCreate(this.fogbowCliResource);
	}
	
	public String doCreate(FogbowCliResource fogbowCliResource) throws FogbowException {
		String fullPath = this.endpoint;

		HashMap body = getCommonParameters();
		HashMap resourceSpecificParams = fogbowCliResource.getHttpHashMap();
		CommandUtil.extendMap(body, resourceSpecificParams);

		body = CommandUtil.removeNullEntries(body);

		return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.POST, fullPath, body);
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

	public String getSystemUserToken() throws FogbowException {
		return fogbowCliHttpUtil.getSystemUserToken();
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

	public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
		this.fogbowCliHttpUtil = fogbowCliHttpUtil;
	}

	private HashMap getCommonParameters(){
		HashMap body = new HashMap();

		body.put(CLOUD_NAME_KEY, this.cloudName);
		body.put(MEMBER_ID_KEY, this.memberId);
		body.put(ID_KEY, this.id);

		return body;
	}

}
