package org.fogbowcloud.cli.authentication.token;

import com.beust.jcommander.ParameterException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.authentication.CommandAuthentication;

import com.beust.jcommander.Parameter;
import org.fogbowcloud.cli.constants.Messages;

import java.io.IOException;

public class CommandCheckToken extends CommandAuthentication {

    public static final String NAME = "check-token";
    public static final String ENDPOINT = "tokens/validate";

    private static final String TOKEN_VALID = "Token Valid";
    private static final String TOKEN_INVALID = "Token Unauthorized";

    @Parameter(names = { "--federation-token-value", "-f" }, description = "Federation token value", required = true)
    private String federationTokenValue = null;

    public static final String URL_COMMAND_KEY =  "--url";
    @Parameter(names = { URL_COMMAND_KEY }, description = "Fogbow RAS URL", required = true)
    protected String url = null;

    public String run() throws IOException {
        if (this.url == null || this.url.isEmpty()) {
            throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL);
        }

        if (this.federationTokenValue == null || this.federationTokenValue.isEmpty()) {
            throw new ParameterException(Messages.Exception.NO_FEDERATION_TOKEN_PARAM);
        }

        return checkToken();
    }

    // TODO: Currently this endpoints is not implemented in Fogbow RAS
    private String checkToken() throws IOException {

        String fullUrl = this.url + ENDPOINT;
        HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationTokenValue);
        String isValidString = HttpUtil.getHttpEntityAsString(httpResponse);
        boolean isValid = Boolean.valueOf(isValidString);

        if (isValid) {
            return TOKEN_VALID;
        }

        return TOKEN_INVALID;
    }
}
