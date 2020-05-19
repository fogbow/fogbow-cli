package cloud.fogbow.cli.ras.quota;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;


public class QuotaCommand {

    public static final String NAME = "quota";
    public static final String ENDPOINT = '/' + "quota";

    @ParametersDelegate
    private FogbowCliHttpUtil fogbowCliHttpUtil = new FogbowCliHttpUtil();

    @Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
    private String memberId = null;

    @Parameter(names = CliCommonParameters.CLOUD_NAME_COMMAND_KEY, description = Documentation.CommonParameters.CLOUD_NAME, required = true)
    private String cloudName = null;

    public String run() throws FogbowException {
        String fullUrl = ENDPOINT + '/' + memberId + '/' + cloudName;
        System.out.println(fullUrl);
        String response = fogbowCliHttpUtil.doAuthenticatedGet(fullUrl);
        return response;
    }
}
