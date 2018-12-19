package org.fogbowcloud.cli.securitygrouprule;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.sun.deploy.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpRequestMatcher;
import org.fogbowcloud.cli.HttpUtil;
import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.network.NetworkCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

public class SecurityGroupRuleCommandTest {

    public static final String A_VALID_TOKEN = "aValidToken";
    public static final String A_VALID_NETWORK_ID = "aValidNetworkId";
    public static final String VALID_FNS_SERVICE_URL = "https://valid-fns-service.com";
    public static final String A_VALID_RULE_ID = "aValidRuleId";
    private HttpClient mockHttpClient;

    private SecurityGroupRule securityGroupRule;

    @Before
    public void setUp() throws IOException, FogbowCLIException {
        int port = -1;
        String cidr = "test-cidr";
        String direction = "IN";
        String etherType = "IPv4";
        String icmp = "ICMP";
        securityGroupRule = new SecurityGroupRule(null, cidr, port, port, direction, etherType, icmp);

        initHttpClient();
    }

    @Test
    public void testCreateSGRForNetwork() throws IOException {
        SecurityGroupRuleCommand securityGroupRuleCommand = new SecurityGroupRuleCommand();

        JCommander.newBuilder()
            .addObject(securityGroupRuleCommand)
            .build()
            .parse(
                    SecurityGroupRuleCommand.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                    SecurityGroupRuleCommand.FEDERATION_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                    SecurityGroupRuleCommand.CREATE_COMMAND_KEY,
                    SecurityGroupRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                    SecurityGroupRule.CIDR_COMMAND_KEY, securityGroupRule.getCidr(),
                    SecurityGroupRule.DIRECTION_COMMAND_KEY, securityGroupRule.getDirection(),
                    SecurityGroupRule.ETHERTYPE_COMMAND_KEY, securityGroupRule.getEtherType(),
                    SecurityGroupRule.PROTOCOL_COMMAND_KEY, securityGroupRule.getProtocol(),
                    SecurityGroupRule.PORT_FROM_COMMAND_KEY, new Integer(securityGroupRule.getPortFrom()).toString(),
                    SecurityGroupRule.PORT_TO_COMMAND_KEY, new Integer(securityGroupRule.getPortTo()).toString()
            );

        String expectedJson = new Gson().toJson(securityGroupRule);
        String expectedUri = StringUtils.join(Arrays.asList(
                VALID_FNS_SERVICE_URL,
                NetworkCommand.ENDPOINT, A_VALID_NETWORK_ID,
                SecurityGroupRuleCommand.ENDPOINT),
                "/");

        HttpPost post = new HttpPost(expectedUri);
        post.setEntity(new StringEntity(expectedJson));
        post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, A_VALID_TOKEN);
        post.setHeader(HttpUtil.CONTENT_TYPE_KEY, HttpUtil.JSON_CONTENT_TYPE_KEY);
        HttpRequestMatcher expectedRequest = new HttpRequestMatcher(post);

        securityGroupRuleCommand.run();

        Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
    }

    @Test
    public void testDeleteSGRFromNetwork() throws IOException {
        SecurityGroupRuleCommand securityGroupRuleCommand = new SecurityGroupRuleCommand();

        JCommander.newBuilder()
                .addObject(securityGroupRuleCommand)
                .build()
                .parse(
                        SecurityGroupRuleCommand.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                        SecurityGroupRuleCommand.FEDERATION_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                        SecurityGroupRuleCommand.DELETE_COMMAND_KEY,
                        SecurityGroupRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                        SecurityGroupRule.INSTANCE_ID_COMMAND_KEY, A_VALID_RULE_ID
                );

        String deleteRuleUrl = StringUtils.join(Arrays.asList(
                VALID_FNS_SERVICE_URL,
                NetworkCommand.ENDPOINT,
                A_VALID_NETWORK_ID,
                SecurityGroupRuleCommand.ENDPOINT,
                A_VALID_RULE_ID), "/");

        HttpDelete delete = new HttpDelete(deleteRuleUrl);
        delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, A_VALID_TOKEN);
        HttpRequestMatcher expectedRequest = new HttpRequestMatcher(delete);

        securityGroupRuleCommand.run();

        Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
    }

    private void initHttpClient() throws FogbowCLIException, IOException {
        mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        HttpResponse response = factory.newHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "Return Irrelevant"), null);
        response.setEntity(new StringEntity("{}"));
        Mockito.when(mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
        HttpUtil.setHttpClient(mockHttpClient);
    }

}
