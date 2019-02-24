package cloud.fogbow.cli.ras.securityrule;

import cloud.fogbow.cli.HttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
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
import cloud.fogbow.cli.HttpRequestMatcher;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.order.network.NetworkCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

public class SecurityRuleCommandTest {

    public static final String A_VALID_TOKEN = "aValidToken";
    public static final String A_VALID_NETWORK_ID = "aValidNetworkId";
    public static final String VALID_FNS_SERVICE_URL = "https://valid-fns-service.com";
    public static final String A_VALID_RULE_ID = "aValidRuleId";
    private HttpClient mockHttpClient;

    private SecurityRule securityRule;

    @Before
    public void setUp() throws IOException, FogbowCLIException {
        int port = -1;
        String cidr = "test-cidr";
        String direction = "IN";
        String etherType = "IPv4";
        String icmp = "ICMP";
        securityRule = new SecurityRule(null, cidr, port, port, direction, etherType, icmp);

        initHttpClient();
    }

    @Test
    public void testCreateSGRForNetwork() throws IOException, FogbowCLIException {
        SecurityRuleCommand securityRuleCommand = new SecurityRuleCommand();

        JCommander.newBuilder()
            .addObject(securityRuleCommand)
            .build()
            .parse(
                    CliCommonParameters.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                    CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                    SecurityRuleCommand.CREATE_COMMAND_KEY,
                    SecurityRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                    SecurityRule.CIDR_COMMAND_KEY, securityRule.getCidr(),
                    SecurityRule.DIRECTION_COMMAND_KEY, securityRule.getDirection(),
                    SecurityRule.ETHERTYPE_COMMAND_KEY, securityRule.getEtherType(),
                    SecurityRule.PROTOCOL_COMMAND_KEY, securityRule.getProtocol(),
                    SecurityRule.PORT_FROM_COMMAND_KEY, new Integer(securityRule.getPortFrom()).toString(),
                    SecurityRule.PORT_TO_COMMAND_KEY, new Integer(securityRule.getPortTo()).toString()
            );

        String expectedJson = new Gson().toJson(securityRule);
        String expectedUri = StringUtils.join(Arrays.asList(
                VALID_FNS_SERVICE_URL,
                NetworkCommand.ENDPOINT, A_VALID_NETWORK_ID,
                SecurityRuleCommand.ENDPOINT),
                "/");

        HttpPost post = new HttpPost(expectedUri);
        post.setEntity(new StringEntity(expectedJson));
        post.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, A_VALID_TOKEN);
        post.setHeader(HttpUtil.CONTENT_TYPE_KEY, HttpUtil.JSON_CONTENT_TYPE_KEY);
        HttpRequestMatcher expectedRequest = new HttpRequestMatcher(post);

        securityRuleCommand.run();

        Mockito.verify(this.mockHttpClient).execute(Mockito.argThat(expectedRequest));
    }

    @Test
    public void testDeleteSGRFromNetwork() throws IOException, FogbowCLIException {
        SecurityRuleCommand securityRuleCommand = new SecurityRuleCommand();

        JCommander.newBuilder()
                .addObject(securityRuleCommand)
                .build()
                .parse(
                        CliCommonParameters.URL_COMMAND_KEY, VALID_FNS_SERVICE_URL,
                        CliCommonParameters.FEDERATION_TOKEN_COMMAND_KEY, A_VALID_TOKEN,
                        SecurityRuleCommand.DELETE_COMMAND_KEY,
                        SecurityRuleCommand.NETWORK_ID_COMMAND_KEY, A_VALID_NETWORK_ID,
                        SecurityRule.INSTANCE_ID_COMMAND_KEY, A_VALID_RULE_ID
                );

        String deleteRuleUrl = StringUtils.join(Arrays.asList(
                VALID_FNS_SERVICE_URL,
                NetworkCommand.ENDPOINT,
                A_VALID_NETWORK_ID,
                SecurityRuleCommand.ENDPOINT,
                A_VALID_RULE_ID), "/");

        HttpDelete delete = new HttpDelete(deleteRuleUrl);
        delete.setHeader(HttpUtil.FEDERATION_TOKEN_VALUE_HEADER_KEY, A_VALID_TOKEN);
        HttpRequestMatcher expectedRequest = new HttpRequestMatcher(delete);

        securityRuleCommand.run();

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
