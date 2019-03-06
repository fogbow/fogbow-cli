package cloud.fogbow.cli.ras.image;

import java.io.IOException;
import java.util.Collections;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import org.apache.http.HttpResponse;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class ImageCommand {
	public static final String NAME = "image";
	public static final String ENDPOINT = '/' + "images";

	@Parameter(names = CliCommonParameters.GET_COMMAND_KEY, description = Documentation.Image.GET)
	protected Boolean isGetCommand = false;
	
	@Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Image.GET_ALL)
	protected Boolean isGetAllCommand = false;

	@Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
	private String systemUserToken = null;

	@Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
	private String systemUserTokenPath = null;

	@Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
	protected String url = null;
	
	@Parameter(names = CliCommonParameters.ID_COMMAND_KEY, description = Documentation.Image.ID)
	protected String imageID = null;
	
	@Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
	private String memberId = null;

	@Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME, required = true)
	private String cloudName = null;

	public String run() throws FogbowCLIException, IOException {
		if (this.isGetCommand) {
			return this.doGet();
		} else if (this.isGetAllCommand) {
			return this.doGetAll();
		}
		throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
	}
	
	public String doGet() throws FogbowCLIException, IOException {
		if (this.imageID == null) {
			throw new ParameterException(Messages.Exception.NO_ID_INFORMED);
		} else {
			String fullUrl = this.url + ENDPOINT + "/" + this.memberId + "/" + this.cloudName + "/" + this.imageID;
			String federationToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
			HttpResponse httpResponse = HttpUtil.get(fullUrl, federationToken, Collections.emptyMap());
			return HttpUtil.getHttpEntityAsString(httpResponse);
		}
	}
	
	public String doGetAll() throws FogbowCLIException, IOException {
		String fullUrl = this.url + ENDPOINT + "/" + this.memberId + "/" + this.cloudName;
		String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
		HttpResponse httpResponse = HttpUtil.get(fullUrl, systemUserToken, Collections.emptyMap());
		return HttpUtil.getHttpEntityAsString(httpResponse);
	}
}
