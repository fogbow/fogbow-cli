package org.fogbowcloud.cli.authentication;

import org.fogbowcloud.manager.core.HomeDir;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.ldap.LdapIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertTrue;

public class CommandAuthenticationTest {

    private static final String CONF_PATH = "src/test/resource";

    private CommandAuthentication commandAuthentication;

    @Before
    public void setUp() {
        this.commandAuthentication = new CommandAuthentication();
    }

    @Test
    public void testIdentityPluginType() throws ReflectiveOperationException, TokenValueCreationException {
        HomeDir.getInstance().setPath(CONF_PATH);

        Whitebox.setInternalState(this.commandAuthentication, "identityPluginName", "ldap");

        assertTrue(this.commandAuthentication.getFederationIdentityPlugin() instanceof LdapIdentityPlugin);
    }

    @Test(expected = TokenValueCreationException.class)
    public void testWrongIdentityPluginType() throws ReflectiveOperationException, TokenValueCreationException {
        Whitebox.setInternalState(this.commandAuthentication, "identityPluginName", "ldab");
        this.commandAuthentication.getFederationIdentityPlugin();
    }
}
