package org.fogbowcloud.cli.authentication;

import com.beust.jcommander.Parameter;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.util.Set;

public class CommandAuthentication {

    private static final String ENV_VAR_NAME = "FOGBOW_CONF_PATH";
    private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.manager.plugins.identity";

    @Parameter(names = { "--conf-path", "-p" }, description = "Configuration file path")
    private String confPath = null;

    @Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
    private String identityPluginName = null;

    public FederationIdentityPlugin getFederationIdentityPlugin()
            throws ReflectiveOperationException, TokenValueCreationException {
        Class<? extends FederationIdentityPlugin> federationIdentityPluginClass = getFederationIdentityPluginClass(
                this.identityPluginName);
        Constructor<?> constructor = federationIdentityPluginClass.getConstructor();

        FederationIdentityPlugin identityPlugin = (FederationIdentityPlugin) constructor.newInstance();
        return identityPlugin;
    }

    private Class<? extends FederationIdentityPlugin> getFederationIdentityPluginClass(String typeName)
            throws TokenValueCreationException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());
        Set<Class<? extends FederationIdentityPlugin>> allFederationIdentityPluginClasses = reflections
                .getSubTypesOf(FederationIdentityPlugin.class);

        for (Class<? extends FederationIdentityPlugin> eachClass : allFederationIdentityPluginClasses) {
            String federationIdentityPluginName = getFederationIdentityPluginName(eachClass);
            if (federationIdentityPluginName.equals(typeName)) {
                return eachClass;
            }
        }

        throw new TokenValueCreationException("Token type [" + typeName + "] is not valid.");
    }

    private String getFederationIdentityPluginName(Class<? extends FederationIdentityPlugin> classObject) {
        String packageName = classObject.getName();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        String identityPluginSuffix = "IdentityPlugin";
        String identityPluginName = className.replace(identityPluginSuffix, "");
        identityPluginName = identityPluginName.toLowerCase();
        return identityPluginName;
    }

    protected String getConfPath() {
        return (this.confPath == null) ? System.getenv(ENV_VAR_NAME) : this.confPath;
    }
}
