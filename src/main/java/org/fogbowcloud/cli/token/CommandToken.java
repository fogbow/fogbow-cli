package org.fogbowcloud.cli.token;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.fogbowcloud.manager.core.exceptions.UnauthenticatedException;
import org.fogbowcloud.manager.core.plugins.behavior.federationidentity.FederationIdentityPlugin;
import org.fogbowcloud.manager.core.plugins.exceptions.TokenValueCreationException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken {

	public static final String NAME = "token";

	private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.manager.plugins.identity";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token", required = true)
	private Boolean isCreate = false;

	@Parameter(names = { "--type", "-t" }, description = "Identity plugin type", required = true)
	private String identityPluginName = null;

	@DynamicParameter(names = "-D", description = "Dynamic parameters")
	private Map<String, String> credentials = new HashMap<String, String>();

	public String run() throws ReflectiveOperationException, UnauthenticatedException, TokenValueCreationException {
		if (this.isCreate) {
			return createToken();
		}
		return null;
	}

	private String createToken()
			throws ReflectiveOperationException, UnauthenticatedException, TokenValueCreationException {

		FederationIdentityPlugin identityPlugin = getFederationIdentityPlugin();
		Map<String, String> userCredentials = new HashMap<String, String>();

		for (String key : this.credentials.keySet()) {
			userCredentials.put(key, this.credentials.get(key));
		}

		String accessId = identityPlugin.createFederationTokenValue(userCredentials);
		return accessId;
	}

	protected FederationIdentityPlugin getFederationIdentityPlugin()
			throws ReflectiveOperationException, TokenValueCreationException {
		Class<? extends FederationIdentityPlugin> federationIdentityPluginClass = getFederationIdentityPluginClass(
				this.identityPluginName);
		Constructor<?> constructor = federationIdentityPluginClass.getConstructor(Properties.class);
		FederationIdentityPlugin identityPlugin = (FederationIdentityPlugin) constructor.newInstance(new Properties());
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
}
