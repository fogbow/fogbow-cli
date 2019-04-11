package cloud.fogbow.cli.ras.image;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class ImageCommand  {
	public static final String NAME = "image";
	public static final String ENDPOINT = '/' + "images";

	@Parameter(names = CliCommonParameters.GET_COMMAND_KEY, description = Documentation.Image.GET)
	protected Boolean isGetCommand = false;
	
	@Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Image.GET_ALL)
	protected Boolean isGetAllCommand = false;

	@Parameter(names = CliCommonParameters.ID_COMMAND_KEY, description = Documentation.Image.ID)
	protected String imageID = null;
	
	@Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
	private String memberId = null;

	@Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME, required = true)
	private String cloudName = null;

	@ParametersDelegate
	private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

	public String run() throws FogbowCLIException{
		try {
			if (this.isGetCommand) {
				return this.doGet();
			} else if (this.isGetAllCommand) {
				return this.doGetAll();
			}
		} catch (FogbowException e){
			throw new FogbowCLIException(e.getMessage());
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}

	public String doGet() throws FogbowCLIException, FogbowException {
		if (this.imageID == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String path = ENDPOINT + "/" + this.memberId + "/" + this.cloudName + "/" + this.imageID;
			String imageData = fogbowCliHttpUtil.doAuthenticatedGet(path);
			return imageData;
		}
	}

	public String doGetAll() throws FogbowCLIException, FogbowException {
		String path = ENDPOINT + "/" + this.memberId + "/" + this.cloudName;

		String allImages = fogbowCliHttpUtil.doAuthenticatedGet(path);

		return allImages;
	}

	public void setFogbowCliHttpUtil(FogbowCliHttpUtil fogbowCliHttpUtil) {
		this.fogbowCliHttpUtil = fogbowCliHttpUtil;
	}
}
