package cloud.fogbow.cli.as.token;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TokenCommandTest {
    private TokenCommand tokenCommand;

    private final int HTTP_OK_CODE = 200;
    private final HashMap FAKE_HEADERS = new HashMap<>();
    private final String FAKE_TOKEN_RESPONSE = "{\"token\": \"fake-token\"}";

    private final String MOCKED_PUBLIC_KEY = "fake-public-key";
    private final String URL = "my-url";
    private final String MOCKED_TOKEN = "fake-token";
    private FogbowCliHttpUtil fogbowCliHttpUtil;

    @Before
    public void setUp() throws FogbowException {
        this.tokenCommand = new TokenCommand();
        MockitoAnnotations.initMocks(this);
        initHttpClient(FAKE_TOKEN_RESPONSE, HTTP_OK_CODE, FAKE_HEADERS);
    }

    @Test
    public void testCreateToken() throws IOException, FogbowCLIException, FogbowException {
        TokenCommand spyCommand = getCommandTokenWithMockedDynamicParams();
        JCommander.newBuilder()
                .addObject(spyCommand)
                .build()
                .parse(
                        TokenCommand.CREATE_COMMAND_KEY,
                        CliCommonParameters.URL_COMMAND_KEY, URL);

        Whitebox.setInternalState(spyCommand, "isCreate", true);
        String token = spyCommand.run();
        System.out.println(token);
        assertEquals(MOCKED_TOKEN, token);
    }

    public TokenCommand getCommandTokenWithMockedDynamicParams() {
        Map<String, String> credentials = getMockedCredentials();
        TokenCommand spyTokenCommand = Mockito.spy(new TokenCommand());
        Whitebox.setInternalState(spyTokenCommand, "credentials", credentials);
        Whitebox.setInternalState(spyTokenCommand, "publicKey", MOCKED_PUBLIC_KEY);
        Whitebox.setInternalState(spyTokenCommand, "fogbowCliHttpUtil", this.fogbowCliHttpUtil);
        return spyTokenCommand;
    }

    private Map<String, String> getMockedCredentials() {
        Map<String, String> credentials = new HashMap<String, String>();

        String password = "12345678";
        credentials.put("password", password);

        String username = "fogbow";
        credentials.put("username", username);

        String authUrl = "ldap://ldap.lsd.ufcg.edu.br:389";
        credentials.put("authUrl", authUrl);

        String base = "dc=lsd,dc=ufcg,dc=edu,dc=br";
        credentials.put("base", base);

        String privateKey = "/home/ordan/private_key.pem";
        credentials.put("private-key", privateKey);

        String publicKey = "/home/ordan/public_key.pem";
        credentials.put("public-key", publicKey);

        return credentials;
    }

    private void initHttpClient() throws FogbowException {
        initHttpClient("some text", 200, new HashMap<>());
    }

    private void initHttpClient(String httpReturnContent, int httpStatusCode, Map<String, List<String>> headers) throws FogbowException {
        FogbowCliHttpUtil mockedCliFogbowHttpUtil = Mockito.mock(FogbowCliHttpUtil.class);

        HttpResponse httpResponse = new HttpResponse(httpReturnContent, httpStatusCode, headers);

        Mockito.when(mockedCliFogbowHttpUtil.doGenericRequest(
                Mockito.any(HttpMethod.class), Mockito.anyString(), Mockito.any(HashMap.class), Mockito.any(HashMap.class)))
                .thenReturn(httpResponse);
        this.fogbowCliHttpUtil = mockedCliFogbowHttpUtil;
    }
}
