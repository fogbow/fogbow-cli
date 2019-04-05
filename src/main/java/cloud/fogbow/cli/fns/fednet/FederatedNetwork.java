package cloud.fogbow.cli.fns.fednet;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

public class FederatedNetwork implements FogbowCliResource {

	private static final String PROVIDING_MEMBERS_JSON_KEY = "providingMembers";
	private static final String NAME_JSON_KEY = "name";
	private static final String CIDR_JSON_KEY = "cidr";

	public static final String CIDR_NOTATION_COMMAND_KEY =  "--cidr";
	@Parameter(names = { CIDR_NOTATION_COMMAND_KEY }, description = "CIDR Notation")
	private String cidrNotation = null;
	
	public static final String NAME_COMMAND_KEY =  "--name";
	@Parameter(names = { NAME_COMMAND_KEY }, description = "Name")
	private String name = null;
	
	public static final String ALLOWED_MEMBERS_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { ALLOWED_MEMBERS_COMMAND_KEY }, description = "Allowed members list")
	private List<String> providingMembers = null;

	public FederatedNetwork() {
		
	}

	public FederatedNetwork(String cidrNotation, String name, List<String> providingMembers) {
		this.cidrNotation = cidrNotation;
		this.name = name;
		this.providingMembers = providingMembers;
	}

	public FederatedNetwork(String cidrNotation, String testeFedNet, Set<String> allowedMembers) {
	}

	public String getCidrNotation() {
		return cidrNotation;
	}

	public String getName() {
		return name;
	}

	@Override
	public HashMap getHTTPHashMap() {
		HashMap body = new HashMap();

		body.put(CIDR_JSON_KEY, this.cidrNotation);
		body.put(NAME_JSON_KEY, this.name);
		body.put(PROVIDING_MEMBERS_JSON_KEY, this.providingMembers);

		return body;
	}
}
