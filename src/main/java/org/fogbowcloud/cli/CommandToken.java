package org.fogbowcloud.cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Identity;
import java.util.Properties;
import java.util.Set;

import org.fogbowcloud.manager.core.plugins.IdentityPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Token manipulation")
public class CommandToken {

	private static final String PLUGIN_PACKAGE = "org.fogbowcloud.manager.core.plugins.identity";

	@Parameter(names = { "--create", "-c" }, description = "Create a new token")
	private Boolean isCreation = false;

	@Parameter(names = { "--type", "-t" }, description = "Identity plugin type")
	private String identityPluginType;

	public void handle() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		if (isCreation) {
			IdentityPlugin identityPlugin = getIdentityPluginByTypeName("ldap");
			System.out.println(identityPlugin);
		}

	}

	private IdentityPlugin getIdentityPluginByTypeName(String name)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {

		Reflections reflections = new Reflections(ClasspathHelper.forPackage(PLUGIN_PACKAGE), new SubTypesScanner());
		Set<Class<? extends IdentityPlugin>> allClasses = reflections.getSubTypesOf(IdentityPlugin.class);

		IdentityPlugin identityPlugin = null;

		for (Class<? extends IdentityPlugin> currentClass : allClasses) {
			if (currentClass.getName().contains(name)) {
				System.out.println(currentClass);
				Constructor<?> cons = currentClass.getConstructor(Properties.class);
				identityPlugin = (IdentityPlugin) cons.newInstance(new Properties());
				
			}
		}

		return identityPlugin;
	}
}
