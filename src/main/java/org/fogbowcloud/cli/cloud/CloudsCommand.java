package org.fogbowcloud.cli.cloud;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;

import java.io.IOException;

public class CloudsCommand {

    public static final String NAME = "clouds";

    public static final String MEMBER_ID_COMMAND_KEY = "--member-id";
    public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
    public static final String FEDERATION_TOKEN_PATH_COMMAND_KEY =  "--federation-token-path";
    public static final String URL_COMMAND_KEY = "--url";

    public static final String ENDPOINT = "clouds";

    @Parameter(names = URL_COMMAND_KEY, required = true)
    private String url;

    @Parameter(names = { MEMBER_ID_COMMAND_KEY }, description = "Member's id", required = true)
    private String memberId = null;

    @Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token")
    private String federationTokenValue = null;

    @Parameter(names = { FEDERATION_TOKEN_PATH_COMMAND_KEY }, description = "Path to user's Token")
    private String federationTokenPath = null;

    private String federationToken = null;

    public String run() throws IOException {
        String fullUrl = this.url + "/" + ENDPOINT + "/" + this.memberId;
        HttpResponse httpResponse = HttpUtil.get(fullUrl, this.federationToken);
        return HttpUtil.getHttpEntityAsString(httpResponse);
    }
}
