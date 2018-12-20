package org.fogbowcloud.cli.securitygrouprule;

import com.beust.jcommander.Parameter;

public class SecurityGroupRule {

    public static final String INSTANCE_ID_COMMAND_KEY = "--id";
    public static final String CIDR_COMMAND_KEY = "--cidr";
    public static final String PORT_FROM_COMMAND_KEY = "--portFrom";
    public static final String PORT_TO_COMMAND_KEY = "--portTo";
    public static final String DIRECTION_COMMAND_KEY = "--direction";
    public static final String ETHERTYPE_COMMAND_KEY = "--etherType";
    public static final String PROTOCOL_COMMAND_KEY = "--protocol";

    @Parameter(names = INSTANCE_ID_COMMAND_KEY)
    private String id;

    @Parameter(names = CIDR_COMMAND_KEY)
    private String cidr;

    @Parameter(names = PORT_FROM_COMMAND_KEY)
    private int portFrom;

    @Parameter(names = PORT_TO_COMMAND_KEY)
    private int portTo;

    @Parameter(names = DIRECTION_COMMAND_KEY, validateWith = SecurityGroupRuleParameterValidator.class)
    private String direction;

    @Parameter(names = ETHERTYPE_COMMAND_KEY, validateWith = SecurityGroupRuleParameterValidator.class)
    private String etherType;

    @Parameter(names = PROTOCOL_COMMAND_KEY, validateWith = SecurityGroupRuleParameterValidator.class)
    private String protocol;

    public SecurityGroupRule() {

    }

    protected SecurityGroupRule(String id, String cidr, int portFrom, int portTo, String direction, String etherType, String protocol) {
        this.id = id;
        this.cidr = cidr;
        this.portFrom = portFrom;
        this.portTo = portTo;
        this.direction = direction;
        this.etherType = etherType;
        this.protocol = protocol;
    }

    public String getId() {
        return id;
    }

    public String getCidr() {
        return cidr;
    }

    public int getPortFrom() {
        return portFrom;
    }

    public int getPortTo() {
        return portTo;
    }

    public String getDirection() {
        return direction;
    }

    public String getEtherType() {
        return etherType;
    }

    public String getProtocol() {
        return protocol;
    }

}
