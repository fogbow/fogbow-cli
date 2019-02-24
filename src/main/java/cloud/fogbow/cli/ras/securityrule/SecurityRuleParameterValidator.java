package cloud.fogbow.cli.ras.securityrule;

import cloud.fogbow.cli.constants.Messages;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityRuleParameterValidator implements IParameterValidator {

        private HashMap<String, List<String>> getValidValues() {
            HashMap<String, List<String>> validValues = new HashMap<>();
            validValues.put(SecurityRule.DIRECTION_COMMAND_KEY, Arrays.asList(new String[] { "IN", "OUT" }));
            validValues.put(SecurityRule.ETHERTYPE_COMMAND_KEY, Arrays.asList(new String[] { "IPv4", "IPv6" }));
            validValues.put(SecurityRule.PROTOCOL_COMMAND_KEY, Arrays.asList(new String[] { "TCP", "UDP", "ICMP" }));
            return validValues;
        }

        @Override
        public void validate(String name, String value) throws ParameterException {
            Map<String, List<String>> validValues = getValidValues();
            List<String> validValuesForParameter = validValues.get(name);
            if (validValuesForParameter == null) {
                String message = Messages.Exception.UNABLE_TO_VALIDATE_PARAMETER_S_S;
                String.format(message, name, getClass().toString());
                throw new IllegalArgumentException(message);
            } else {
                if (!validValuesForParameter.contains(value)) {
                    String message = Messages.Exception.INVALID_PARAMETER_S_S;
                    message = String.format(message, name, validValuesForParameter.toString());
                    throw new ParameterException(message);
                }
            }
        }
    }