package org.fogbowcloud.cli.order.compute;

import com.beust.jcommander.Parameter;

public class Compute {
	
	public static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	public static final String PUBLIC_KEY_COMMMAND_KEY = "--public-key";
	@Parameter(names = { PUBLIC_KEY_COMMMAND_KEY}, description = "Public key")
	private String publicKey = null;
	
	public static final String IMAGE_ID_COMMAND_KEY = "--image-id";
	@Parameter(names = { IMAGE_ID_COMMAND_KEY }, description = "Image name")
	private String imageId = null;
	
	public static final String VCPU_COMMAND_KEY = "--vcpu";
	@Parameter(names = { VCPU_COMMAND_KEY }, description = "Vcpu")
	private String vCPU = null;
	
	public static final String MEMORY_COMMAND_KEY = "--memory";
	@Parameter(names = { MEMORY_COMMAND_KEY }, description = "Memory")
	private String memory = null;
	
	public static final String DISC_COMMAND_KEY = "--disk";
	@Parameter(names = { DISC_COMMAND_KEY }, description = "Disk")
	private String disk = null;
	
	protected String getProvidingMember() {
		return providingMember;
	}

	protected String getPublicKey() {
		return publicKey;
	}

	protected String getImageId() {
		return imageId;
	}

	protected String getvCPU() {
		return vCPU;
	}

	protected String getMemory() {
		return memory;
	}

	protected String getDisk() {
		return disk;
	}

}
