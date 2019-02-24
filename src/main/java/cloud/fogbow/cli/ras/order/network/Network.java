package cloud.fogbow.cli.ras.order.network;

import com.beust.jcommander.Parameter;

public class Network {
	
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
}
