package cloud.fogbow.cli.ras.securityrule;

import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class SecurityRule implements FogbowCliResource {
    public static final String INSTANCE_ID_COMMAND_KEY = "--id";
    public static final String CIDR_COMMAND_KEY = "--cidr";
    public static final String PORT_FROM_COMMAND_KEY = "--portFrom";
    public static final String PORT_TO_COMMAND_KEY = "--portTo";
    public static final String DIRECTION_COMMAND_KEY = "--direction";
    public static final String ETHERTYPE_COMMAND_KEY = "--etherType";
    public static final String PROTOCOL_COMMAND_KEY = "--protocol";

    public static final String CIDR_HTTP_FIELD = "cidr";
    public static final String DIRECTION_HTTP_FIELD = "direction";
    public static final String ETHER_TYPE_HTTP_FIELD = "etherType";
    public static final String INSTANCE_ID_HTTP_FIELD = "instanceId";
    public static final String PORT_FROM_HTTP_FIELD = "portFrom";
    public static final String PORT_TO_HTTP_FIELD = "portTo";
    public static final String PROTOCO_HTTP_FIELD = "protocol";

    @Parameter(names = INSTANCE_ID_COMMAND_KEY)
    private String id;

    @Parameter(names = CIDR_COMMAND_KEY)
    private String cidr;

    @Parameter(names = PORT_FROM_COMMAND_KEY)
    private int portFrom;

    @Parameter(names = PORT_TO_COMMAND_KEY)
    private int portTo;

    @Parameter(names = DIRECTION_COMMAND_KEY, validateWith = SecurityRuleParameterValidator.class)
    private String direction;

    @Parameter(names = ETHERTYPE_COMMAND_KEY, validateWith = SecurityRuleParameterValidator.class)
    private String etherType;

    @Parameter(names = PROTOCOL_COMMAND_KEY, validateWith = SecurityRuleParameterValidator.class)
    private String protocol;

    public SecurityRule() {

    }

    protected SecurityRule(String id, String cidr, int portFrom, int portTo, String direction, String etherType, String protocol) {
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

    @Override
    public HashMap getHttpHashMap() {
        HashMap body = new HashMap();

        body.put(CIDR_HTTP_FIELD, this.cidr);
        body.put(DIRECTION_HTTP_FIELD, this.direction);
        body.put(ETHER_TYPE_HTTP_FIELD, this.etherType);
        body.put(INSTANCE_ID_HTTP_FIELD, this.id);
        body.put(PORT_FROM_HTTP_FIELD, this.portFrom);
        body.put(PORT_TO_HTTP_FIELD, this.portTo);
        body.put(PROTOCO_HTTP_FIELD, this.protocol);

        return body;
    }
}
