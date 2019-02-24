package cloud.fogbow.cli.utils;

import cloud.fogbow.cli.constants.Messages;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.util.HashMap;
import java.util.Map;

public class KeyValueUtil {

    public static final String KEY_VALUES_SEPARATOR = ",";
    public static final String KEY_VALUE_SEPARATOR = "=";

    public static class KeyValueConverter implements IStringConverter<Map<String, String>> {

        @Override
        public Map<String, String> convert(String s) {
            String[] separatedKeyValues = s.split(KEY_VALUES_SEPARATOR);
            Map<String, String> keyValues = new HashMap<>();
            for (String keyValue : separatedKeyValues) {
                String [] req = keyValue.split(KEY_VALUE_SEPARATOR);
                if (req.length != 2 || req[0].isEmpty() || req[1].isEmpty()) {
                    throw new ParameterException(Messages.Exception.INVALID_KEY_VALUE_FORMAT);
                } else {
                    keyValues.put(req[0], req[1]);
                }
            }
            return keyValues;
        }

    }
}
