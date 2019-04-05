package cloud.fogbow.cli.utils;

import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandUtil {

    public static String getSystemUserToken(String systemUserToken, String systemUserTokenPath) throws FogbowCLIException {
        return getFileContent(systemUserToken, systemUserTokenPath, Messages.Exception.NO_SYSTEM_USER_TOKEN).replace("\n", "");
    }

    public static String getApplicationPublicKey(String publicKeyValue, String publicKeyPath) throws FogbowCLIException {
        return getFileContent(publicKeyValue, publicKeyPath, Messages.Exception.NO_PUBLIC_KEY_PROVIDED).replace("\n", "");
    }

    public static String getFileContent(String providedResourcePath, String NoResourceFoundException) throws FogbowCLIException {
        return getFileContent(null, providedResourcePath, NoResourceFoundException);
    }

    public static String getFileContent(String providedRawValue, String providedResourcePath, String NoResourceFoundException) throws FogbowCLIException {
        providedResourcePath = parsePath(providedResourcePath);

        String actualvalue = null;
        if (actualvalue == null || actualvalue.isEmpty()) {
            if (providedRawValue != null && !providedRawValue.isEmpty()) {
                actualvalue = providedRawValue;
            } else if (providedResourcePath != null && !providedResourcePath.isEmpty()) {
                try {
                    actualvalue = FileUtils.readFileToString(providedResourcePath);
                } catch (IOException e) {
                    throw new FogbowCLIException(NoResourceFoundException);
                }
            } else {
                throw new FogbowCLIException(NoResourceFoundException);
            }
        }
        return actualvalue;
    }

    public static void extendMap(HashMap destiantion, HashMap source){
        for (Object obj: source.entrySet()){

            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            Object value = entry.getValue();

            destiantion.put(key, value);
        }
    }

    private static String parsePath(String path){
        if(path == null){
            path = "";
        }
        String homePath = System.getProperty("user.home");
        return path.replace("~", homePath);
    }
}
