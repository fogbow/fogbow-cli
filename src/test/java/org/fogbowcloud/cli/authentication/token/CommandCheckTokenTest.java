package org.fogbowcloud.cli.authentication.token;

import static org.junit.Assert.assertEquals;

import org.fogbowcloud.manager.core.exceptions.TokenValueCreationException;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CommandCheckTokenTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValidToken()
            throws ReflectiveOperationException, TokenValueCreationException {

        FederationIdentityPlugin federationIdentityPlugin = Mockito.mock(FederationIdentityPlugin.class);
        Mockito.when(federationIdentityPlugin.isValid(Mockito.any())).thenReturn(true);

        CommandCheckToken spyCommandCheckToken = Mockito.spy(new CommandCheckToken());
        Mockito.doReturn(federationIdentityPlugin).when(spyCommandCheckToken).getFederationIdentityPlugin();

        assertEquals("Token Valid", spyCommandCheckToken.run());
    }

    @Test
    public void testIsNotValidToken()
            throws ReflectiveOperationException, TokenValueCreationException {

        FederationIdentityPlugin federationIdentityPlugin = Mockito.mock(FederationIdentityPlugin.class);
        Mockito.when(federationIdentityPlugin.isValid(Mockito.any())).thenReturn(false);

        CommandCheckToken spyCommandCheckToken = Mockito.spy(new CommandCheckToken());
        Mockito.doReturn(federationIdentityPlugin).when(spyCommandCheckToken).getFederationIdentityPlugin();

        assertEquals("Token Unauthorized", spyCommandCheckToken.run());
    }

    @Test(expected = TokenValueCreationException.class)
    public void testNoCommandDefined() throws TokenValueCreationException, ReflectiveOperationException {
        CommandCheckToken commandCheckToken = new CommandCheckToken();

        assertEquals(null, commandCheckToken.run());
    }
}
