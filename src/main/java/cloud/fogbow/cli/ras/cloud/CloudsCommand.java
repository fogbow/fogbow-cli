package cloud.fogbow.cli.ras.cloud;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import com.beust.jcommander.Parameter;
import org.apache.http.HttpResponse;
import cloud.fogbow.cli.HttpUtil;

import java.io.IOException;

public class CloudsCommand {
    public static final String NAME = "clouds";
    public static final String ENDPOINT = "clouds";

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    private String url = null;

    @Parameter(names = CliCommonParameters.MEMBER_ID_COMMAND_KEY, description = Documentation.CommonParameters.MEMBER_ID, required = true)
    private String memberId = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    private String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    private String systemUserTokenPath = null;

    public String run() throws FogbowCLIException, IOException {
        String fullUrl = this.url + "/" + ENDPOINT + "/" + this.memberId;
        String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
        HttpResponse httpResponse = HttpUtil.get(fullUrl, systemUserToken);
        return HttpUtil.getHttpEntityAsString(httpResponse);
    }
}