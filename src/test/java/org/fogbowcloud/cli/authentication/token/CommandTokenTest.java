package org.fogbowcloud.cli.authentication.token;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.fogbowcloud.cli.HttpUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

public class CommandTokenTest {

	private CommandToken commandToken;
    private HttpClient mockHttpClient;
    private final String URL = "my-url";
    private final String MOCKED_TOKEN = "fake-token";

	@Before
	public void setUp() throws IOException {
		this.commandToken = new CommandToken();
		MockitoAnnotations.initMocks(this);
        initHttpClient();
	}

	@Test
	public void testCreateToken() throws IOException {
        CommandToken spyCommand = getCommandTokenWithMockedDynamicParams();
	    JCommander.newBuilder()
                .addObject(spyCommand)
                .build()
                .parse(
                        CommandToken.CREATE_COMMAND_KEY,
                        CommandToken.URL_COMMAND_KEY, URL);

        Whitebox.setInternalState(spyCommand, "isCreate", true);
        String token = spyCommand.run();
        assertEquals(MOCKED_TOKEN, token);
	}

	public CommandToken getCommandTokenWithMockedDynamicParams() {
        Map<String, String> credentials = getMockedCredentials();
		CommandToken spyCommandToken = Mockito.spy(new CommandToken());
		Whitebox.setInternalState(spyCommandToken, "credentials", credentials);
		return spyCommandToken;
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
        credentials.put("privateKey", privateKey);

        String publicKey = "/home/ordan/public_key.pem";
        credentials.put("publicKey", publicKey);

        return credentials;
    }

    private void initHttpClient() throws IOException {
        this.mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        HttpResponse response = factory.newHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_CREATED, "Return Irrelevant"), null);
        response.setEntity(new StringEntity(MOCKED_TOKEN));
        Mockito.when(this.mockHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(response);
        HttpUtil.setHttpClient(this.mockHttpClient);
    }
}
