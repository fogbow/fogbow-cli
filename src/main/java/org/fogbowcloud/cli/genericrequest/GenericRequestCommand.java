package org.fogbowcloud.cli.genericrequest;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.utils.FileUtils;

import java.io.IOException;

public class GenericRequestCommand {

    public static final String NAME = "genericRequest";
    public static final String MEMBER_ID_COMMAND_KEY = "--member-id";
    public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
    public static final String FEDERATION_TOKEN_PATH_COMMAND_KEY =  "--federation-token-path";
    public static final String URL_COMMAND_KEY = "--url";

    public static final String MEMBER_ID_HEADER_KEY = "memberId";
    private static final String ENDPOINT = "/genericRequest";

    @Parameter(names = URL_COMMAND_KEY)
    private String url;

    @Parameter(names = { MEMBER_ID_COMMAND_KEY }, description = "Member's id")
    private String memberId = null;

    @Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token")
    private String federationTokenValue = null;

    @Parameter(names = { FEDERATION_TOKEN_PATH_COMMAND_KEY }, description = "Path to user's Token")
    private String federationTokenPath = null;

    private String federationToken = null;

    @ParametersDelegate
    private GenericRequest genericRequest = new GenericRequest();

    public String getFederationToken() throws FogbowCLIException {
        if (this.federationToken == null || this.federationToken.isEmpty()) {
            if (this.federationTokenValue != null && !this.federationTokenValue.isEmpty()) {
                this.federationToken = this.federationTokenValue;
            } else if (this.federationTokenPath != null && !this.federationTokenPath.isEmpty()) {
                try {
                    this.federationToken = FileUtils.readFileToString(this.federationTokenPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new FogbowCLIException("Its required to pass --federation-token-value or --federation-token-path as params.");
            }
        }
        return federationToken;
    }

    public String run() throws FogbowCLIException, IOException {
        String fullUrl = this.url + ENDPOINT + "/" + this.memberId;
        HttpResponse httpResponse = HttpUtil.post(fullUrl, new Gson().toJson(this.genericRequest), getFederationToken());
        return HttpUtil.getHttpEntityAsString(httpResponse);
    }

}
