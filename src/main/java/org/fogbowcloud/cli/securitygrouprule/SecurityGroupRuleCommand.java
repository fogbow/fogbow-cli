package org.fogbowcloud.cli.securitygrouprule;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import com.sun.deploy.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.order.network.NetworkCommand;
import org.fogbowcloud.cli.order.publicip.PublicIpCommand;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SecurityGroupRuleCommand {

    public static final String URL_COMMAND_KEY = "--url";
    public static final String CREATE_COMMAND_KEY = "--create";
    public static final String DELETE_COMMAND_KEY = "--delete";
    public static final String NETWORK_ID_COMMAND_KEY = "--networkId";
    public static final String PUBLIC_IP_COMMAND_KEY = "--publicIpId";
    public static final String FEDERATION_TOKEN_COMMAND_KEY =  "--federation-token-value";
    public static final String FEDERATION_TOKEN_PATH_COMMAND_KEY =  "--federation-token-path";

    public static final String NAME = "security-group-rules";
    public static final String ENDPOINT = "securityGroupRules";

    @Parameter(names = { CREATE_COMMAND_KEY })
    private Boolean isCreateCommand = false;

    @Parameter(names = { DELETE_COMMAND_KEY })
    private Boolean isDeleteCommand = false;

    @Parameter(names = { NETWORK_ID_COMMAND_KEY })
    private String networkId;

    @Parameter(names = { PUBLIC_IP_COMMAND_KEY })
    private String publicIpId;

    @Parameter(names = { URL_COMMAND_KEY })
    private String url;

    @Parameter(names = { FEDERATION_TOKEN_COMMAND_KEY }, description = "User's Token")
    private String federationTokenValue;

    @Parameter(names = { FEDERATION_TOKEN_PATH_COMMAND_KEY }, description = "Path to user's Token")
    private String federationTokenPath;

    @ParametersDelegate
    private SecurityGroupRule securityGroupRule = new SecurityGroupRule();

    public String run() throws IOException {
        if ((isCreateCommand() ^ isDeleteCommand())) {
            if (isCreateCommand()) {
                return doCreate(securityGroupRule, getFederationToken());
            } else {
                return doDelete(securityGroupRule, getFederationToken());
            }
        } else {
            throw new ParameterException(String.format("you should specify either %s or %s", CREATE_COMMAND_KEY, DELETE_COMMAND_KEY));
        }
    }

    private String doCreate(SecurityGroupRule securityGroupRule, String federationTokenValue) throws IOException {
        if (networkId != null ^ publicIpId != null) {
            String completeUrl;
            if (networkId != null) {
                completeUrl = StringUtils.join(Arrays.asList(url, NetworkCommand.ENDPOINT, networkId, ENDPOINT), "/");
            } else {
                completeUrl = StringUtils.join(Arrays.asList(url, PublicIpCommand.ENDPOINT, publicIpId, ENDPOINT), "/");
            }

            String jsonFormattedBody = new Gson().toJson(securityGroupRule);
            HttpResponse httpResponse = HttpUtil.post(completeUrl, jsonFormattedBody, federationTokenValue);
            return HttpUtil.getHttpEntityAsString(httpResponse);
        } else {
            String message = String.format("You should specify either %s or %s", NETWORK_ID_COMMAND_KEY, PUBLIC_IP_COMMAND_KEY);
            throw new ParameterException(message);
        }
    }

    private String getFederationToken() throws IOException {
        if (federationTokenPath == null && federationTokenValue == null ||
            federationTokenPath != null && federationTokenValue != null) {
            String message = "You should specify either %s or %s";
            message = String.format(message, FEDERATION_TOKEN_COMMAND_KEY, FEDERATION_TOKEN_PATH_COMMAND_KEY);
            throw new ParameterException(message);
        }

        return federationTokenValue != null ? federationTokenValue : readFile(federationTokenPath);
    }

    private String doDelete(SecurityGroupRule securityGroupRule, String federationTokenValue) throws ClientProtocolException {
        if (securityGroupRule == null || securityGroupRule.getId() == null) {
            throw new ParameterException("You should specify the id of the rule you are trying to delete");
        }

        List<String> urlParams;
        if (networkId != null ^ publicIpId != null) {
            if (networkId != null) {
                urlParams = Arrays.asList(url, NetworkCommand.ENDPOINT, networkId,
                        ENDPOINT, securityGroupRule.getId());
            } else {
                urlParams = Arrays.asList(url, PublicIpCommand.ENDPOINT, publicIpId,
                        ENDPOINT, securityGroupRule.getId());
            }
        } else {
            String message = String.format("You should specify either %s or %s", NETWORK_ID_COMMAND_KEY, PUBLIC_IP_COMMAND_KEY);
            throw new ParameterException(message);
        }

        String completeUrl = StringUtils.join(urlParams, "/");
        HttpResponse httpResponse = HttpUtil.delete(completeUrl, federationTokenValue);
        return httpResponse.getStatusLine().toString();
    }

    public Boolean isCreateCommand() {
        return this.isCreateCommand;
    }

    public Boolean isDeleteCommand() {
        return this.isDeleteCommand;
    }

    private String readFile(String path) throws IOException {
        if (path == null) return "";
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
