package cloud.fogbow.cli.ras.securityrule;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import cloud.fogbow.cli.ras.order.network.NetworkCommand;
import cloud.fogbow.cli.ras.order.publicip.PublicIpCommand;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SecurityRuleCommand {
    public static final String NAME = "security-rule";
    public static final String ENDPOINT = "securityRules";
    public static final String CREATE_COMMAND_KEY = "--create";
    public static final String DELETE_COMMAND_KEY = "--delete";
    public static final String NETWORK_ID_COMMAND_KEY = "--networkId";
    public static final String PUBLIC_IP_COMMAND_KEY = "--publicIpId";

    @Parameter(names = CREATE_COMMAND_KEY)
    private Boolean isCreateCommand = false;

    @Parameter(names = DELETE_COMMAND_KEY)
    private Boolean isDeleteCommand = false;

    @Parameter(names = NETWORK_ID_COMMAND_KEY)
    private String networkId;

    @Parameter(names = PUBLIC_IP_COMMAND_KEY)
    private String publicIpId;

    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL)
    private String url;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN)
    private String systemUserToken = null;

    @Parameter(names = CliCommonParameters.SYSTEM_USER_TOKEN_PATH_COMMAND_KEY, description = Documentation.CommonParameters.SYSTEM_USER_TOKEN_PATH)
    private String systemUserTokenPath = null;

    @ParametersDelegate
    private SecurityRule securityRule = new SecurityRule();

    public String run() throws FogbowCLIException, IOException {
        String systemUserToken = CommandUtil.getSystemUserToken(this.systemUserToken, this.systemUserTokenPath);
        if ((isCreateCommand() ^ isDeleteCommand())) {
            if (isCreateCommand()) {
                return doCreate(securityRule, systemUserToken);
            } else {
                return doDelete(securityRule, systemUserToken);
            }
        } else {
            throw new ParameterException(String.format(Messages.Exception.INCONSISTENT_PARAMS, CREATE_COMMAND_KEY, DELETE_COMMAND_KEY));
        }
    }

    private String doCreate(SecurityRule securityRule, String systemUserToken) throws IOException {
        if (networkId != null ^ publicIpId != null) {
            String completeUrl;
            if (networkId != null) {
                completeUrl = StringUtils.join(Arrays.asList(url, NetworkCommand.ENDPOINT, networkId, ENDPOINT), "/");
            } else {
                completeUrl = StringUtils.join(Arrays.asList(url, PublicIpCommand.ENDPOINT, publicIpId, ENDPOINT), "/");
            }

            String jsonFormattedBody = new Gson().toJson(securityRule);
            HttpResponse httpResponse = HttpUtil.post(completeUrl, jsonFormattedBody, systemUserToken);
            return HttpUtil.getHttpEntityAsString(httpResponse);
        } else {
            String message = String.format(Messages.Exception.INCONSISTENT_PARAMS, NETWORK_ID_COMMAND_KEY, PUBLIC_IP_COMMAND_KEY);
            throw new ParameterException(message);
        }
    }

    private String doDelete(SecurityRule securityRule, String systemUserToken) throws ClientProtocolException {
        if (securityRule == null || securityRule.getId() == null) {
            throw new ParameterException(Messages.Exception.NO_RULE_ID_INFORMED);
        }

        List<String> urlParams;
        if (networkId != null ^ publicIpId != null) {
            if (networkId != null) {
                urlParams = Arrays.asList(url, NetworkCommand.ENDPOINT, networkId,
                        ENDPOINT, securityRule.getId());
            } else {
                urlParams = Arrays.asList(url, PublicIpCommand.ENDPOINT, publicIpId,
                        ENDPOINT, securityRule.getId());
            }
        } else {
            String message = String.format(Messages.Exception.INCONSISTENT_PARAMS, NETWORK_ID_COMMAND_KEY, PUBLIC_IP_COMMAND_KEY);
            throw new ParameterException(message);
        }

        String completeUrl = StringUtils.join(urlParams, "/");
        HttpResponse httpResponse = HttpUtil.delete(completeUrl, systemUserToken);
        return httpResponse.getStatusLine().toString();
    }

    public Boolean isCreateCommand() {
        return this.isCreateCommand;
    }

    public Boolean isDeleteCommand() {
        return this.isDeleteCommand;
    }
}
