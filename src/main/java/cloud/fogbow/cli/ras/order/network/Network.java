package cloud.fogbow.cli.ras.order.network;

import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class Network implements FogbowCliResource {

	public static final String ALLOCATION_MODE_KEY = "allocationMode";
	public static final String CIDR_KEY = "cidr";
	public static final String GATEWAY_KEY = "gateway";
	public static final String NAME_KEY = "name";
	public static final String PROVIDER_KEY = "provider";
	
	public static final String PROVIDER_COMMAND_KEY =  "--provider";
	@Parameter(names = {PROVIDER_COMMAND_KEY}, description = "Provider")
	private String provider = null;

	public static final String GATEWAY_COMMAND_KEY =  "--gateway";
	@Parameter(names = { GATEWAY_COMMAND_KEY }, description = "Gateway")
	private String gateway = null;

	public static final String ADDRESS_COMMAND_KEY =  "--cidr";
	@Parameter(names = { ADDRESS_COMMAND_KEY }, description = "CIDR of the network")
	private String cidr = null;

	public static final String ALLOCATION_COMMAND_KEY =  "--allocationMode";
	@Parameter(names = { ALLOCATION_COMMAND_KEY }, description = "Allocation")
	private String allocation = null;

	public static final String NAME_COMMAND_KEY = "--name";
	@Parameter(names = { NAME_COMMAND_KEY }, description = "Name")
	private String name;

	public Network() {
		
	}
	
	public Network(String provider, String gateway, String cidr, String allocation, String name) {
		super();
		this.provider = provider;
		this.gateway = gateway;
		this.cidr = cidr;
		this.allocation = allocation;
		this.name = name;
	}
	
	public String getProvider() {
		return provider;
	}
	public String getGateway() {
		return gateway;
	}
	public String getCidr() {
		return cidr;
	}
	public String getAllocation() {
		return allocation;
	}

	public String getName() {
		return name;
	}

	@Override
	public HashMap getHTTPHashMap() {
		HashMap body = new HashMap();

		body.put(ALLOCATION_MODE_KEY, this.allocation);
		body.put(CIDR_KEY, this.cidr);
		body.put(GATEWAY_KEY, this.gateway);
		body.put(NAME_KEY, this.name);
		body.put(PROVIDER_KEY, this.provider);

		return body;
	}
}
