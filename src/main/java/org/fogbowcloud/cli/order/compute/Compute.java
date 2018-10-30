package org.fogbowcloud.cli.order.compute;

import com.beust.jcommander.Parameter;

import java.util.Set;

public class Compute {

	public static final String PROVIDER_COMMAND_KEY =  "--provider";
	@Parameter(names = {PROVIDER_COMMAND_KEY}, description = "Provider")
	private String provider = null;

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
	private String userData = null; // TODO check this
	
	public static final String FEDERATED_NETWORK_ID_COMMAND_KEY = "--networkIds";
	@Parameter(names = { FEDERATED_NETWORK_ID_COMMAND_KEY }, description = "Federated network id", variableArity = true)
	private Set<String> networksId = null;

	public static final String NAME_COMMAND_KEY = "--name";
	@Parameter(names = { NAME_COMMAND_KEY }, description = "Name")
	private String name;

	public Compute() {
		
	}

	protected Compute(String provider, String publicKey, String imageId, String vCPU, String memory, String disk,
					  Set<String> networksId, String name) {
		this.provider = provider;
		this.publicKey = publicKey;
		this.imageId = imageId;
		this.vCPU = vCPU;
		this.memory = memory;
		this.disk = disk;
		this.networksId = networksId;
		this.name = name;
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
	
	public String getProvider() {
		return this.provider;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Set<String> getNetworksId() {
		return networksId;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getName() {
		return name;
	}

}
