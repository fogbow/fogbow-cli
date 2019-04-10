package cloud.fogbow.cli.ras.order.compute;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.ras.FogbowCliResource;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.cli.utils.KeyValueUtil.KeyValueConverter;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.util.CloudInitUserDataBuilder;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.google.common.base.Preconditions;

import java.util.*;

public class Compute implements FogbowCliResource {

	public static final String DEFAULT_PUBLIC_KEY_PATH_FILE = "~/.ssh/id_rsa.pub";
	public static final String DISK_KEY = "disk";
	public static final String IMAGE_ID_KEY = "imageId";
	public static final String MEMORY_KEY = "memory";
	public static final String NAME_KEY = "name";
	public static final String NETWORK_IDS_KEY = "networkIds";
	public static final String PROVIDER_KEY = "provider";
	public static final String PUBLIC_KEY_KEY = "publicKey";
	public static final String REQUIREMENTS_KEY = "requirements";
	public static final String USER_DATA_KEY = "userData";
	public static final String USER_DATA_SEPARATOR = ":";
	public static final String USER_DATAFILE_CONTENT_KEY = "extraUserDataFileContent";
	public static final String USER_DATAfILE_FILE_TYPE_KEY = "extraUserDataFileType";
	public static final String USER_DATAfILE_TAG_KEY = "tag";
	public static final String VCPU_KEY = "vCPU";

	@Parameter(names = {CliCommonParameters.PROVIDER_COMMAND_KEY},
			description = Documentation.CommonParameters.PROVIDER)
	protected String provider = null;

	public static final String PUBLIC_KEY_COMMMAND_KEY = "--public-key-path";
	@Parameter(names = { PUBLIC_KEY_COMMMAND_KEY}, description = "Public key")
	protected String publicKeyPath = null;
	
	public static final String IMAGE_ID_COMMAND_KEY = "--image-id";
	@Parameter(names = { IMAGE_ID_COMMAND_KEY }, description = "Image id")
	protected String imageId = null;
	
	public static final String VCPU_COMMAND_KEY = "--vcpu";
	@Parameter(names = { VCPU_COMMAND_KEY }, description = "Vcpu")
	protected String vCPU = null;
	
	public static final String MEMORY_COMMAND_KEY = "--memory";
	@Parameter(names = { MEMORY_COMMAND_KEY }, description = "Memory")
	protected String memory = null;
	
	public static final String DISC_COMMAND_KEY = "--disk";
	@Parameter(names = { DISC_COMMAND_KEY }, description = "Disk")
	protected String disk = null;

	// naming this command key as "--user-data" results in a unaccountable crash of JCommander
	public static final String USER_DATA_COMMAND_KEY = "--extra-data";
	@Parameter(names = { USER_DATA_COMMAND_KEY }, description = "User Data")
	protected List<String> userData = null;

	public static final String NETWORK_IDS_COMMAND_KEY = "--networks-id";
	@Parameter(names = {NETWORK_IDS_COMMAND_KEY}, description = "Network Ids", splitter = CommaParameterSplitter.class)
	protected List<String> networksId = null;

	public static final String NAME_COMMAND_KEY = "--name";

	@Parameter(names = { NAME_COMMAND_KEY }, description = "Name")
	protected String name;

	public static final String REQUIREMENTS_COMMAND_KEY = "--requirements";
	@Parameter(names = { REQUIREMENTS_COMMAND_KEY }, converter = KeyValueConverter.class)
	protected Map<String, String> requirements;

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
	public HashMap getHTTPHashMap() throws InvalidParameterException {
		HashMap body = new HashMap();

		HashMap requiredParams = getRequiredParams();
		HashMap optionalParams = getOptionalParams();

		CommandUtil.extendMap(body, requiredParams);
		CommandUtil.extendMap(body, optionalParams);

		return body;
	}

	private HashMap getRequiredParams() throws InvalidParameterException {
		HashMap body = new HashMap();

		String vCpu = getvCPU();
		String memory = getMemory();
		String disk = getDisk();
		String imageId = getImageId();

		checkComputeValues(vCpu, memory, disk, imageId);

		body.put(VCPU_KEY, vCpu);
		body.put(MEMORY_KEY, memory);
		body.put(DISK_KEY, disk);
		body.put(IMAGE_ID_KEY, imageId);

		return body;
	}

	private HashMap getOptionalParams() throws InvalidParameterException{
		HashMap body = new HashMap();

		body.put(NETWORK_IDS_KEY, getNetworkIds());
		body.put(NAME_KEY, getName());
		body.put(PUBLIC_KEY_KEY, getPublicKeyFileContent());
		body.put(PROVIDER_KEY, getProvider());
		body.put(USER_DATA_KEY, getUserDataList());
		body.put(REQUIREMENTS_KEY, getRequirements());

		return body;
	}

	private void checkComputeValues(String vCpu, String memory, String disk, String imageId) {
		try {
			Preconditions.checkNotNull(vCpu);
			Preconditions.checkNotNull(memory);
			Preconditions.checkNotNull(disk);
			Preconditions.checkNotNull(imageId);
		} catch (NullPointerException e) {
			throw new NullPointerException(Messages.Exception.MISSING_CREATE_COMPUTE_PARAMS);
		}
	}


	private List getUserDataList() throws InvalidParameterException {
		List userDataList = new ArrayList();

		List<String> userDataValues = getUserData();
		if (userDataValues != null) {
			for (int i = 0; i < userDataValues.size(); i++) {
				String userData = userDataValues.get(i);
				String data[] = userData.split(USER_DATA_SEPARATOR);

				if (data.length != 2) {
					throw new InvalidParameterException(Messages.Exception.MALFORMED_USER_DATA);
				}

				String filePath = data[0];
				String fileFormat = data[1];

				String userDataFileContent = null;

				try {

					userDataFileContent = CommandUtil.getFileContent(filePath, Messages.Exception.NO_USER_DATA);
				} catch (FogbowCLIException e) {

					throw new InvalidParameterException(e.getMessage());
				}
				String fileType = getUserDataFileType(fileFormat);

				String encodedUserDataFileContent = Base64.getEncoder().encodeToString(userDataFileContent.getBytes());
				String tag = getFileName(filePath);

				HashMap userDataMap = new HashMap<String, String>();
				userDataMap.put(USER_DATAFILE_CONTENT_KEY, encodedUserDataFileContent);
				userDataMap.put(USER_DATAfILE_FILE_TYPE_KEY, fileType);
				userDataMap.put(USER_DATAfILE_TAG_KEY, tag);

				userDataList.add(userDataMap);
			}
		}

		return userDataList;
	}

	private String getPublicKeyFileContent() {
		String providedPath = getPublicKeyPath();

		String publicKeyContent = null;

		try {
			publicKeyContent = CommandUtil.getFileContent(providedPath, Messages.Exception.PUBLIC_KEY_FILE_NOT_FOUND);
		} catch (FogbowCLIException e){
			try {
				publicKeyContent = CommandUtil.getFileContent(DEFAULT_PUBLIC_KEY_PATH_FILE, Messages.Exception.PUBLIC_KEY_FILE_NOT_FOUND);
			} catch (FogbowCLIException f){
				e.printStackTrace();
			}
		}

		return publicKeyContent;
	}

	private String getFileName(String path) {
		String[] paths = path.split("/");
		return paths[paths.length - 1];
	}


	private String getUserDataFileType(String fileType) throws InvalidParameterException {
		try {
			CloudInitUserDataBuilder.FileType.valueOf(fileType);
		} catch (Exception e) {
			throw new InvalidParameterException(Messages.Exception.INCONSISTENT_PARAMS);
		}
		return fileType;
	}

}
