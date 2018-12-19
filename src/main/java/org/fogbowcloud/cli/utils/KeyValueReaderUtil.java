package org.fogbowcloud.cli.utils;

import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.fogbowcloud.cli.order.OrderCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyValueReaderUtil {

    public static final String KEY_VALUE_SEPARATOR = "=";
    private static final String KEY_VALUE_EXCEPTION_MESSAGE = "Key-values should be in the form: %s key1=value1" +
            " %s key2=value2";

    public static Map<String, String> convertKeyValueListToMap(List<String> requirementList) throws FogbowCLIException {
        Map<String, String> requirements = new HashMap<>();
        if (!requirementList.isEmpty()) {
            for (String requirement : requirementList) {
                String [] req = requirement.split(KEY_VALUE_SEPARATOR);
                if (req.length != 2 || req[0].isEmpty() || req[1].isEmpty()) {
                    throw new FogbowCLIException(String.format(KEY_VALUE_EXCEPTION_MESSAGE, OrderCommand.REQUIREMENTS,
                            OrderCommand.REQUIREMENTS));
                }
                requirements.put(req[0], req[1]);
            }
        }
        return requirements;
    }
}
