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
	@Parameter(names = { IMAGE_ID_COMMAND_KEY }, description = "Image id")
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

	public static final String USER_DATA_COMMAND_KEY = "--user-data";
	@Parameter(names = { USER_DATA_COMMAND_KEY }, description = "User Data")
	private String userData = null;
	
	public static final String FEDERATED_NETWORK_ID_COMMAND_KEY = "--fednet-id";
	@Parameter(names = { FEDERATED_NETWORK_ID_COMMAND_KEY }, description = "Federated network id")
	private String networksId = null;
	
	public Compute() {
		
	}

	protected Compute(String providingMember, String publicKey, String imageId, String vCPU, String memory, String disk,
					  String networksId) {
		super();
		this.providingMember = providingMember;
		this.publicKey = publicKey;
		this.imageId = imageId;
		this.vCPU = vCPU;
		this.memory = memory;
		this.disk = disk;
		this.networksId = networksId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getImageId() {
		return imageId;
	}

	public String getvCPU() {
		return vCPU;
	}

	public String getMemory() {
		return memory;
	}

	public String getDisk() {
		return disk;
	}
	
	public String getProvidingMember() {
		return this.providingMember;
	}
	
	public String getNetworksId() {
		return this.networksId;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}
}
