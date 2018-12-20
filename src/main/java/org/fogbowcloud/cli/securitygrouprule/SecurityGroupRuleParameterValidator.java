package org.fogbowcloud.cli.securitygrouprule;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityGroupRuleParameterValidator implements IParameterValidator {

        private HashMap<String, List<String>> getValidValues() {
            HashMap<String, List<String>> validValues = new HashMap<>();
            validValues.put(SecurityGroupRule.DIRECTION_COMMAND_KEY, Arrays.asList(new String[] { "IN", "OUT" }));
            validValues.put(SecurityGroupRule.ETHERTYPE_COMMAND_KEY, Arrays.asList(new String[] { "IPv4", "IPv6" }));
            validValues.put(SecurityGroupRule.PROTOCOL_COMMAND_KEY, Arrays.asList(new String[] { "TCP", "UDP", "ICMP" }));
            return validValues;
        }

        @Override
        public void validate(String name, String value) throws ParameterException {
            Map<String, List<String>> validValues = getValidValues();
            List<String> validValuesForParameter = validValues.get(name);
            if (validValuesForParameter == null) {
                String message = "You shouldn't be validating <%s> using <%s>";
                String.format(message, name, getClass().toString());
                throw new IllegalArgumentException(message);
            } else {
                if (!validValuesForParameter.contains(value)) {
                    String message = "Parameter <%s> should be one of %s";
                    message = String.format(message, name, validValuesForParameter.toString());
                    throw new ParameterException(message);
                }
            }
        }
    }