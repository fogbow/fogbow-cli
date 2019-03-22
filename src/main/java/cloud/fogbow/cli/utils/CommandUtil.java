package cloud.fogbow.cli.utils;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import java.io.IOException;

public class CommandUtil {

    public static String getSystemUserToken(String systemUserToken, String systemUserTokenPath) throws FogbowCLIException {
        return getGenericValue(systemUserToken, systemUserTokenPath, Messages.Exception.NO_SYSTEM_USER_TOKEN);
    }

    public static String getApplicationPublicKey(String publicKeyValue, String publicKeyPath) throws FogbowCLIException {
        return getGenericValue(publicKeyValue, publicKeyPath, Messages.Exception.NO_PUBLIC_KEY_PROVIDED);
    }

    private static String getGenericValue(String providedRawValue, String providedResourcePath, String NoResourceFoundException) throws FogbowCLIException {
        String actualvalue = null;
        if (actualvalue == null || actualvalue.isEmpty()) {
            if (providedRawValue != null && !providedRawValue.isEmpty()) {
                actualvalue = providedRawValue;
            } else if (providedResourcePath != null && !providedResourcePath.isEmpty()) {
                try {
                    actualvalue = FileUtils.readFileToString(providedResourcePath);
                    actualvalue = actualvalue.replace("\n", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new FogbowCLIException(NoResourceFoundException);
            }
        }
        return actualvalue;
    }
}
