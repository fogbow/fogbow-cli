package org.fogbowcloud.cli.authentication.user;

import com.beust.jcommander.ParameterException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.authentication.CommandAuthentication;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.fogbowcloud.cli.constants.Messages;

import java.io.IOException;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandUser {

    public static final String NAME = "user";
    public static final String ENDPOINT = "tokens/member";

    @Parameter(names = { "--get-user", "-g" }, description = "Get user information", required = true)
    private Boolean isGetUser = false;

    @Parameter(names = { "--federation-token-value", "-f" }, description = "Federation token value", required = true)
    private String federationTokenValue = null;

    public static final String URL_COMMAND_KEY =  "--url";
    @Parameter(names = { URL_COMMAND_KEY }, description = "Fogbow RAS URL", required = true)
    protected String url = null;

    public String run() throws IOException {
        if (this.url == null || this.url.isEmpty()) {
            throw new ParameterException(Messages.Exception.NO_MEMBERSHIP_SERVICE_URL);
        }

        if (this.isGetUser) {
            return getFederationUser(this.federationTokenValue);
        }

        return null;
    }

    // TODO: Currently this endpoints is not implemented in Fogbow RAS
    private String getFederationUser(String federationTokenValue) throws IOException {
        if (federationTokenValue == null || federationTokenValue.isEmpty()) {
            throw new ParameterException(Messages.Exception.NO_FEDERATION_TOKEN_PARAM);
        }

        String fullUrl = this.url + ENDPOINT;
        HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationTokenValue);
        return HttpUtil.getHttpEntityAsString(httpResponse);
    }
}
