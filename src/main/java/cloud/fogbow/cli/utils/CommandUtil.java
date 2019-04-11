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

    public static String getFileContent(String providedResourcePath, String errorMessage) throws FogbowCLIException {
        return getFileContent(null, providedResourcePath, errorMessage);
    }

    public static String getFileContent(String providedRawValue, String providedResourcePath, String errorMessage) throws FogbowCLIException {
        providedResourcePath = parsePath(providedResourcePath);

        String actualvalue = null;
        if (actualvalue == null || actualvalue.isEmpty()) {
            if (providedRawValue != null && !providedRawValue.isEmpty()) {
                actualvalue = providedRawValue;
            } else if (providedResourcePath != null && !providedResourcePath.isEmpty()) {
                try {
                    actualvalue = FileUtils.readFileToString(providedResourcePath);
                } catch (IOException e) {
                    throw new FogbowCLIException(errorMessage);
                }
            } else {
                throw new FogbowCLIException(errorMessage);
            }
        }
        return actualvalue;
    }

    public static void extendMap(HashMap destination, HashMap source){
        for (Object obj: source.entrySet()){

            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            Object value = entry.getValue();

            destination.put(key, value);
        }
    }


    public static HashMap removeNullEntries(HashMap<String, Object> entries) {
        HashMap clearedHashMap = new HashMap();

        for(Map.Entry<String, Object> entry : entries.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value != null){
                clearedHashMap.put(key, value);
            }
        }

        return clearedHashMap;
    }

    private static String parsePath(String path){
        if(path == null){
            path = "";
        }
        String homePath = System.getProperty("user.home");
        return path.replace("~", homePath);
    }
}
