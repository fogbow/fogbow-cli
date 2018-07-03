package org.fogbowcloud.cli.authentication.user;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.fogbowcloud.manager.core.exceptions.FogbowManagerException;
import org.fogbowcloud.manager.core.exceptions.UnexpectedException;
import org.fogbowcloud.manager.core.models.tokens.FederationUser;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

public class CommandUserTest {

    private CommandUser commandUser;

    @Before
    public void setUp() {
        this.commandUser = new CommandUser();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFederationUser() throws FogbowManagerException, ReflectiveOperationException, UnexpectedException {

        FederationIdentityPlugin federationIdentityPlugin = Mockito.mock(FederationIdentityPlugin.class);

        String fakeId = "fake-id";
        FederationUser federationUser = new FederationUser(fakeId, new HashMap<>());
        Mockito.when(federationIdentityPlugin.getFederationUser(Mockito.anyString())).thenReturn(federationUser);

        CommandUser spyCommandUser = Mockito.spy(new CommandUser());
        Mockito.doReturn(federationIdentityPlugin).when(spyCommandUser).getFederationIdentityPlugin();

        Whitebox.setInternalState(spyCommandUser, "isGetUser", true);
        Whitebox.setInternalState(spyCommandUser, "identityPluginName", "ldap");
        Whitebox.setInternalState(spyCommandUser, "federationTokenValue", "token");

        String result = spyCommandUser.userToJson(federationUser);

        assertEquals(result, spyCommandUser.run());
    }

    @Test
    public void testNoCommandDefined() throws ReflectiveOperationException, FogbowManagerException, UnexpectedException {
        assertEquals(null, this.commandUser.run());
    }
}
