package org.fogbowcloud.cli.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.fogbowcloud.manager.core.models.token.Token;
import org.fogbowcloud.manager.core.models.token.Token.User;
import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.TokenCreationException;
import org.fogbowcloud.manager.core.plugins.identity.exceptions.UnauthorizedException;
import org.fogbowcloud.manager.core.plugins.identity.ldap.LdapIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.identity.openstack.KeystoneV3IdentityPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestCommandToken {

	private CommandToken commandToken;

	@Before
	public void setUp() {
		commandToken = new CommandToken();
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
		assertTrue(commandToken.getIdentityPlugin() instanceof LdapIdentityPlugin);

		this.commandToken.setIdentityPluginType("keystonev3");
		assertTrue(commandToken.getIdentityPlugin() instanceof KeystoneV3IdentityPlugin);
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
}
