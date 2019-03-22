package cloud.fogbow.cli.ras.cloud;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

public class CloudsCommand {
    public static final String NAME = "clouds";
    public static final String ENDPOINT = "clouds";

    @Parameter(names = CliCommonParameters.GET_COMMAND_KEY, description = Documentation.Order.GET)
    private Boolean isGetCommand = false;

    @Parameter(names = CliCommonParameters.GET_ALL_COMMAND_KEY, description = Documentation.Order.GET_ALL)
    private Boolean isGetAllCommand = false;

    @Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID)
    private String memberId = null;

    @ParametersDelegate
    private FogbowCliHttpUtil authenticatedRequest = new FogbowCliHttpUtil();

    public String run() throws FogbowCLIException {
        if(this.getIsGetCommand()){
            return this.doGet();
        } else if(this.getIsGetAllCommand()){
            return this.doGetAll();
        }
        throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
    }

    public String doGetAll() throws FogbowCLIException {
        String fullPath = ENDPOINT;
        return authenticatedRequest.doAuthenticatedGET(fullPath);
    }

    public String doGet() throws FogbowCLIException {
        if(this.memberId == null || this.memberId.isEmpty()){
            throw new FogbowCLIException(Messages.Exception.INCOMPLETE_COMMAND);
        }
        String fullPath = ENDPOINT + "/" + this.memberId;
        return authenticatedRequest.doAuthenticatedGET(fullPath);
    }

    public Boolean getIsGetCommand() {
        return isGetCommand;
    }

    public Boolean getIsGetAllCommand() {
        return isGetAllCommand;
    }
}
