package org.fogbowcloud.cli.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fogbowcloud.manager.core.models.token.Token;
import org.fogbowcloud.manager.core.models.token.Token.User;
import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;
import org.fogbowcloud.manager.core.plugins.identity.ldap.LdapIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.identity.openstack.KeystoneV3IdentityPlugin;
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
	public void testCreateToken() throws ReflectiveOperationException, TokenCreationException, UnauthorizedException {

		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);
		String accessId = "accessId";
		Token token = new Token(accessId, new User("", ""), new Date(), null);
		Mockito.when(identityPlugin.createToken(Mockito.any())).thenReturn(token);

		CommandToken spyCommandToken = Mockito.spy(new CommandToken());
		Mockito.doReturn(identityPlugin).when(spyCommandToken).getIdentityPlugin();
		spyCommandToken.setIsCreation(true);
		spyCommandToken.addCredential("Dkey", "value");
		
		assertEquals(token.getAccessId(), spyCommandToken.run());
	}

	@Test
	public void testIdentityPluginType() throws ReflectiveOperationException, TokenCreationException {

		this.commandToken.setIdentityPluginType("ldap");
		assertTrue(this.commandToken.getIdentityPlugin() instanceof LdapIdentityPlugin);

		this.commandToken.setIdentityPluginType("keystonev3");
		assertTrue(this.commandToken.getIdentityPlugin() instanceof KeystoneV3IdentityPlugin);
	}

	@Test(expected = TokenCreationException.class)
	public void testWrongIdentityPluginType() throws ReflectiveOperationException, TokenCreationException {

		CommandToken commandToken = new CommandToken();

		commandToken.setIdentityPluginType("ldab");
		commandToken.getIdentityPlugin();
	}

	@Test
	public void testNoCommandDefined()
			throws ReflectiveOperationException, UnauthorizedException, TokenCreationException {
		assertEquals(null, this.commandToken.run());
	}
	
	@Test
	public void testCredentials() throws UnauthorizedException, TokenCreationException, ReflectiveOperationException {
		
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
		
		IdentityPlugin identityPlugin = Mockito.mock(IdentityPlugin.class);
		String accessId = "accessId";
		Token token = new Token(accessId, new User("", ""), new Date(), null);
		Mockito.when(identityPlugin.createToken(Mockito.any())).thenReturn(token);
		
		CommandToken spyCommandToken = Mockito.spy(new CommandToken());
		
		Whitebox.setInternalState(spyCommandToken, "credentials", credentials);
		Whitebox.setInternalState(spyCommandToken, "isCreate", true);
		
		Mockito.doReturn(identityPlugin).when(spyCommandToken).getIdentityPlugin();
		
		spyCommandToken.run();
		
		Mockito.verify(identityPlugin).createToken(this.mapCaptor.capture());
		
		assertTrue(this.mapCaptor.getValue().equals(credentials));
	}
}
