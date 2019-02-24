package cloud.fogbow.cli.utils;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import java.io.IOException;

public class CommandUtil {
    public static String getFederationToken(String federationTokenValue, String federationTokenPath) throws FogbowCLIException {
        String federationToken = null;
        if (federationToken == null || federationToken.isEmpty()) {
            if (federationTokenValue != null && !federationTokenValue.isEmpty()) {
                federationToken = federationTokenValue;
            } else if (federationTokenPath != null && !federationTokenPath.isEmpty()) {
                try {
                    federationToken = FileUtils.readFileToString(federationTokenPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new FogbowCLIException(Messages.Exception.NO_FEDERATION_TOKEN);
            }
        }
        return federationToken;
    }
}
