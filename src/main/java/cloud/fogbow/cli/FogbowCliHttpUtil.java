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

// This class must NOT be static so we can anotate it with @ParametersDelegate
public class FogbowCliHttpUtil {

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    private String url = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    private String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    private String systemUserTokenPath = null;

    public String doAuthenticatedGET(String fullPath) throws FogbowCLIException {
        String fullUrl = this.url + '/' + fullPath;

        return doGenericAuthenticatedRequest(HttpMethod.GET, fullUrl, new HashMap(), new HashMap());
    }

    public String doGenericAuthenticatedRequest(HttpMethod httpMethod, String fullUrl, HashMap customHeaders, HashMap body) throws FogbowCLIException {
        HashMap headers = new HashMap();

        String systemUserTokenValue = getSystemUserTokenValue();

        headers.put(HttpConstants.FOGBOW_USER_TOKEN_KEY, systemUserTokenValue);
        headers.putAll(customHeaders);

        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpRequestClient.doGenericRequest(httpMethod, fullUrl, headers, body);
        } catch (Exception e) {
            throw new FogbowCLIException(e.getMessage());
        }
        return httpResponse.getContent();
    }

    private String getSystemUserTokenValue(){
        String systemUserTokenValue = null;

        try {
            systemUserTokenValue = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
        } catch (FogbowCLIException e) {
            e.printStackTrace();
        }

        return systemUserTokenValue;
    }
}
