package org.fogbowcloud.cli.authentication.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.fogbowcloud.manager.core.exceptions.FogbowManagerException;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

public class CommandTokenTest {

	private CommandToken commandToken;

	@Captor
	private ArgumentCaptor<Map<String, String>> mapCaptor;

	@Before
	public void setUp() {
		this.commandToken = new CommandToken();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateToken() throws FogbowManagerException, ReflectiveOperationException {

		FederationIdentityPlugin federationIdentityPlugin = Mockito.mock(FederationIdentityPlugin.class);
		String accessId = "accessId";
		Mockito.when(federationIdentityPlugin.createFederationTokenValue(Mockito.any())).thenReturn(accessId);

		CommandToken spyCommandToken = Mockito.spy(new CommandToken());
		Mockito.doReturn(federationIdentityPlugin).when(spyCommandToken).getFederationIdentityPlugin();

		Whitebox.setInternalState(spyCommandToken, "isCreate", true);

		Map<String, String> credentials = new HashMap<>();
		credentials.put("Dkey", "value");

		Whitebox.setInternalState(spyCommandToken, "credentials", credentials);

		assertEquals(accessId, spyCommandToken.run());
	}

	@Test
	public void testNoCommandDefined()
			throws ReflectiveOperationException, FogbowManagerException {
		assertEquals(null, this.commandToken.run());
	}

	@Test
	public void testCredentials() throws FogbowManagerException, ReflectiveOperationException {

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

		FederationIdentityPlugin federationIdentityPlugin = Mockito.mock(FederationIdentityPlugin.class);
		String accessId = "accessId";
		Mockito.when(federationIdentityPlugin.createFederationTokenValue(Mockito.any())).thenReturn(accessId);

		CommandToken spyCommandToken = Mockito.spy(new CommandToken());

		Whitebox.setInternalState(spyCommandToken, "credentials", credentials);
		Whitebox.setInternalState(spyCommandToken, "isCreate", true);

		Mockito.doReturn(federationIdentityPlugin).when(spyCommandToken).getFederationIdentityPlugin();

		spyCommandToken.run();

		Mockito.verify(federationIdentityPlugin).createFederationTokenValue(this.mapCaptor.capture());

		assertTrue(this.mapCaptor.getValue().equals(credentials));
	}
}
