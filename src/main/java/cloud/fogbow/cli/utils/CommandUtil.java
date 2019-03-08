package cloud.fogbow.cli.utils;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import java.io.IOException;

public class CommandUtil {
    public static String getSystemUserToken(String systemUserToken, String systemUserTokenPath) throws FogbowCLIException {
        String actualSystemUserToken = null;
        if (actualSystemUserToken == null || actualSystemUserToken.isEmpty()) {
            if (systemUserToken != null && !systemUserToken.isEmpty()) {
                actualSystemUserToken = systemUserToken;
            } else if (systemUserTokenPath != null && !systemUserTokenPath.isEmpty()) {
                try {
                    actualSystemUserToken = FileUtils.readFileToString(systemUserTokenPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new FogbowCLIException(Messages.Exception.NO_SYSTEM_USER_TOKEN);
            }
        }
        return actualSystemUserToken;
    }
}
