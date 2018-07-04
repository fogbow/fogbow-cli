package org.fogbowcloud.cli.order.fednet;

import java.util.List;

import com.beust.jcommander.Parameter;

public class FederatedNetwork {
	
	public static final String CIDR_NOTATION_COMMAND_KEY =  "--cidr";
	@Parameter(names = { CIDR_NOTATION_COMMAND_KEY }, description = "CIDR Notation")
	private String cidrNotation = null;
	
	public static final String LABEL_COMMAND_KEY =  "--label";
	@Parameter(names = { LABEL_COMMAND_KEY }, description = "Label")
	private String label = null;
	
	public static final String ALLOWED_MEMBERS_COMMAND_KEY =  "--allowed-member";
	@Parameter(names = { ALLOWED_MEMBERS_COMMAND_KEY }, description = "Allowed members list")
	private List<String> allowedMembers = null;
	
	public FederatedNetwork() {
		
	}
	
	public FederatedNetwork(String cidrNotation, String label, List<String> allowedMembers) {
		super();
		this.cidrNotation = cidrNotation;
		this.label = label;
		this.allowedMembers = allowedMembers;
	}	
	
	public String getCidrNotation() {
		return cidrNotation;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getAllowedMembers() {
		return allowedMembers;
	}
	
}
