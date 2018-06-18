package org.fogbowcloud.cli.order.attachment;

import com.beust.jcommander.Parameter;

public class Attachment {
	
	private static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	private static final String SOURCE_COMMAND_KEY =  "--source";
	@Parameter(names = { SOURCE_COMMAND_KEY }, description = "Source")
	private String source = null;
	
	private static final String TARGET_COMMAND_KEY =  "--target";
	@Parameter(names = { TARGET_COMMAND_KEY }, description = "Target")
	private String target = null;
	
	private static final String DEVICE_COMMAND_KEY =  "--device";
	@Parameter(names = { DEVICE_COMMAND_KEY }, description = "Source")
	private String device = null;
	
}
