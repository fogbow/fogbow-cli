package org.fogbowcloud.cli.order.volume;

import com.beust.jcommander.Parameter;

public class Volume {
	
	private static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	private static final String VOLUME_SIZE_COMMAND_KEY =  "--volume-size";
	@Parameter(names = { VOLUME_SIZE_COMMAND_KEY }, description = "Volume size")
	private Integer volumeSize = null;
	
}
