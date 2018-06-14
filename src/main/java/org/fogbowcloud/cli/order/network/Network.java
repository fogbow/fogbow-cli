package org.fogbowcloud.cli.order.network;

import com.beust.jcommander.Parameter;

public class Network {
	
	public static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	public static final String GATEWAY_COMMAND_KEY =  "--gateway";
	@Parameter(names = { GATEWAY_COMMAND_KEY }, description = "Gateway")
	private String gateway = null;
	
	public static final String ADDRESS_COMMAND_KEY =  "--address";
	@Parameter(names = { ADDRESS_COMMAND_KEY }, description = "Address")
	private String address = null;
	
	public static final String ALLOCATION_COMMAND_KEY =  "--allocation";
	@Parameter(names = { ALLOCATION_COMMAND_KEY }, description = "Allocation")
	private String allocation = null;
}
