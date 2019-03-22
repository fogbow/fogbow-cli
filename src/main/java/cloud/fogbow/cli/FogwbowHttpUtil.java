package cloud.fogbow.cli;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class AuthenticatedGETResource extends BaseCommand {

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    private String url = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    private String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    private String systemUserTokenPath = null;

    public String doAuthenticatedRequest(String endpoint) throws FogbowCLIException {
        String fullUrl = this.url + '/' + endpoint;

        HashMap headers = new HashMap<>();
        HashMap body = new HashMap<>();

        String systemUserTokenValue = null;
        try {
            systemUserTokenValue = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
        } catch (FogbowCLIException e) {
            e.printStackTrace();
        }

        headers.put(HttpConstants.FOGBOW_USER_TOKEN_KEY, systemUserTokenValue);

        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpRequestClient.doGenericRequest(HttpMethod.GET, fullUrl, headers, body);
        } catch (Exception e) {
            throw new FogbowCLIException(e.getMessage());
        }
        return httpResponse.getContent();
    }
}
