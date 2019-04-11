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

// This class must NOT be static so we can anotate it with @ParametersDelegate
public class FogbowCliHttpUtil {

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    private String url = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    private String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    private String systemUserTokenPath = null;

    public HttpResponse doGenericRequest(HttpMethod method, String endpoint, HashMap<String, String> headers,
                                                HashMap<String, String> body) throws FogbowException {
        return HttpRequestClient.doGenericRequest(method, endpoint, headers, body);
    }

    public String doAuthenticatedGet(String fullPath)
            throws FogbowException {
        return doGenericAuthenticatedRequest(HttpMethod.GET, fullPath, new HashMap(), new HashMap());
    }

    public String doGenericAuthenticatedRequest(HttpMethod httpMethod, String fullPath) throws FogbowException {
        return doGenericAuthenticatedRequest(httpMethod, fullPath, new HashMap());
    }

    public String doGenericAuthenticatedRequest(HttpMethod httpMethod, String fullPath, HashMap body) throws FogbowException {
        return doGenericAuthenticatedRequest(httpMethod, fullPath, new HashMap(), body);
    }

    public String doGenericAuthenticatedRequest(HttpMethod httpMethod, String fullPath, HashMap customHeaders, HashMap body)
            throws FogbowException {
        String fullUrl = this.url + '/' + fullPath;

        if(customHeaders == null){
            customHeaders = new HashMap();
        }

        if(body == null){
            body = new HashMap();
        }

        HashMap headers = getHeaders();

        String systemUserTokenValue = getSystemUserToken();

        headers.put(HttpConstants.FOGBOW_USER_TOKEN_KEY, systemUserTokenValue);
        CommandUtil.extendMap(headers, customHeaders);

        HttpResponse httpResponse = HttpRequestClient.doGenericRequest(httpMethod, fullUrl, headers, body);
        return httpResponse.getContent();
    }

    public String getSystemUserToken() throws FogbowException {
        String systemUserTokenValue = null;

        try {
            systemUserTokenValue = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
        } catch (FogbowCLIException e) {
            throw new FogbowException(e.getMessage());
        }

        return systemUserTokenValue;
    }

    private HashMap getHeaders(){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HttpConstants.CONTENT_TYPE_KEY, HttpConstants.JSON_CONTENT_TYPE_KEY);
        return headers;
    }

    public String getUrl() {
        return url;
    }
}
