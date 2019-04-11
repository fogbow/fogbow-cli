package cloud.fogbow.cli.ras.securityrule;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.HttpClientMocker;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.network.NetworkCommand;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import com.beust.jcommander.JCommander;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.verify;

public class SecurityRuleCommandTest {

    public static final String A_VALID_TOKEN = "aValidToken";
    public static final String A_VALID_NETWORK_ID = "aValidNetworkId";
    public static final String VALID_FNS_SERVICE_URL = "https://valid-fns-service.com";
    public static final String A_VALID_RULE_ID = "aValidRuleId";

    private SecurityRule securityRule;
    private SecurityRuleCommand securityRuleCommand;
    private FogbowCliHttpUtil fogbowCliHttpUtil;

    @Before
    public void setUp() throws FogbowException {
        int port = -1;
        String cidr = "test-cidr";
        String direction = "IN";
        String etherType = "IPv4";
        String icmp = "ICMP";
        securityRule = new SecurityRule(null, cidr, port, port, direction, etherType, icmp);
        this.securityRuleCommand = new SecurityRuleCommand();

        this.fogbowCliHttpUtil = HttpClientMocker.init();
        securityRuleCommand.setFogbowCliHttpUtil(fogbowCliHttpUtil);
    }

    @Test
    public void testCreateSGRForNetwork() throws IOException, FogbowCLIException, FogbowException {
        JCommander.newBuilder()
            .addObject(securityRuleCommand)
            .build()
            .parse(
                    CliCommonParameters.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                    CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                    SecurityRuleCommand.CREATE_COMMAND_KEY,
                    SecurityRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                    SecurityRule.CIDR_COMMAND_KEY, securityRule.getCidr(),
                    SecurityRule.DIRECTION_COMMAND_KEY, securityRule.getDirection(),
                    SecurityRule.ETHERTYPE_COMMAND_KEY, securityRule.getEtherType(),
                    SecurityRule.PROTOCOL_COMMAND_KEY, securityRule.getProtocol(),
                    SecurityRule.PORT_FROM_COMMAND_KEY, new Integer(securityRule.getPortFrom()).toString(),
                    SecurityRule.PORT_TO_COMMAND_KEY, new Integer(securityRule.getPortTo()).toString()
            );

        HashMap expectedBody = securityRule.getHttpHashMap();
        String path = StringUtils.join(Arrays.asList(
                NetworkCommand.ENDPOINT, A_VALID_NETWORK_ID,
                SecurityRuleCommand.ENDPOINT),
                "/");

        securityRuleCommand.run();

        verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.POST, path, expectedBody);
    }

    @Test
    public void testDeleteSGRFromNetwork() throws IOException, FogbowCLIException, FogbowException {
        JCommander.newBuilder()
                .addObject(securityRuleCommand)
                .build()
                .parse(
                        CliCommonParameters.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                        CliCommonParameters.SYSTEM_USER_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                        SecurityRuleCommand.DELETE_COMMAND_KEY,
                        SecurityRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                        SecurityRule.INSTANCE_ID_COMMAND_KEY, A_VALID_RULE_ID
                );

        String path = StringUtils.join(Arrays.asList(
                NetworkCommand.ENDPOINT,
                A_VALID_NETWORK_ID,
                SecurityRuleCommand.ENDPOINT,
                A_VALID_RULE_ID), "/");

        securityRuleCommand.run();

        verify(this.fogbowCliHttpUtil).doGenericAuthenticatedRequest(HttpMethod.DELETE, path);
    }
}
