package cloud.fogbow.cli.ras.order.compute;

import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;
import cloud.fogbow.cli.utils.KeyValueUtil.KeyValueConverter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.converters.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compute implements FogbowCliResource {

	public static final String PROVIDER_COMMAND_KEY =  "--provider";
	@Parameter(names = { PROVIDER_COMMAND_KEY }, description = "Provider")
	private String provider = null;

	public static final String PUBLIC_KEY_COMMMAND_KEY = "--public-key-path";
	@Parameter(names = { PUBLIC_KEY_COMMMAND_KEY}, description = "Public key")
	private String publicKeyPath = null;
	
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

	// naming this command key as "--user-data" results in a unaccountable crash of JCommander
	public static final String USER_DATA_COMMAND_KEY = "--extra-data";
	@Parameter(names = { USER_DATA_COMMAND_KEY }, description = "User Data")
	private List<String> userData = null;

	public static final String NETWORK_IDS_COMMAND_KEY = "--networks-id";
	@Parameter(names = {NETWORK_IDS_COMMAND_KEY}, description = "Network Ids", splitter = CommaParameterSplitter.class)
	private List<String> networksId = null;

	public static final String NAME_COMMAND_KEY = "--name";

	@Parameter(names = { NAME_COMMAND_KEY }, description = "Name")
	private String name;

	public static final String REQUIREMENTS_COMMAND_KEY = "--requirements";
	@Parameter(names = { REQUIREMENTS_COMMAND_KEY }, converter = KeyValueConverter.class)
	private Map<String, String> requirements;

	public Compute() {
		
	}

	public Compute(String provider, String publicKeyPath, String imageId, String vCPU, String memory, String disk,
				   List<String> userData, List<String> networksId, String name, Map<String, String> requirements) {
		this.provider = provider;
		this.publicKeyPath = publicKeyPath;
		this.imageId = imageId;
		this.vCPU = vCPU;
		this.memory = memory;
		this.disk = disk;
		this.userData = userData;
		this.networksId = networksId;
		this.name = name;
		this.requirements = requirements;
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
	
	public List<String> getNetworkIds() {
		return networksId;
	}

	public List<String> getUserData() {
		return userData;
	}

	public void setUserData(List<String> userData) {
		this.userData = userData;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getRequirements() {
		return requirements;
	}


	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}

	@Override
	public HashMap getHTTPHashMap() {
		return null;
	}
}
