package cloud.fogbow.cli;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpConstants;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public abstract class TokenBasedCommand extends BaseCommand {
    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    protected String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    protected String systemUserTokenPath = null;

    protected String doAuthenticatedRequest(HttpMethod method, String endpoint) throws FogbowCLIException, FogbowException {
        return doAuthenticatedRequest(method, endpoint, new HashMap<>());
    }

    protected String doAuthenticatedRequest(HttpMethod method, String endpoint, HashMap<String, String> headers) throws FogbowCLIException, FogbowException {
        return doAuthenticatedRequest(method, endpoint, headers, new HashMap<>());
    }

    protected String doAuthenticatedRequest(HttpMethod method, String endpoint, HashMap<String, String> headers, HashMap<String, String> body) throws FogbowCLIException, FogbowException {
        if (headers == null) {
            headers = new HashMap<>();
        }

        if (body == null) {
            body = new HashMap<>();
        }

        String systemUserTokenValue = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);

        HashMap requestHeaders = new HashMap();
        // This oder matters cause the Child command may want to overide this header
        requestHeaders.put(HttpConstants.FOGBOW_USER_TOKEN_KEY, systemUserTokenValue);
        requestHeaders.putAll(headers);

        HttpResponse httpResponse = HttpRequestClient.doGenericRequest(method, endpoint, requestHeaders, body);
        return httpResponse.getContent();
    }
}
