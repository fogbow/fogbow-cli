package org.fogbowcloud.cli.order.attachment;

import com.beust.jcommander.Parameter;

public class Attachment {
	
	public static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	public static final String SOURCE_COMMAND_KEY =  "--source";
	@Parameter(names = { SOURCE_COMMAND_KEY }, description = "Source")
	private String source = null;
	
	public static final String TARGET_COMMAND_KEY =  "--target";
	@Parameter(names = { TARGET_COMMAND_KEY }, description = "Target")
	private String target = null;
	
	public static final String DEVICE_COMMAND_KEY =  "--device";
	@Parameter(names = { DEVICE_COMMAND_KEY }, description = "Source")
	private String device = null;
	
	public Attachment(){
		
	}
	
	public Attachment(String providingMember, String source, String target, String device) {
		super();
		this.providingMember = providingMember;
		this.source = source;
		this.target = target;
		this.device = device;
	}
	
	public String getProvidingMember() {
		return providingMember;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getDevice() {
		return device;
	}
}
