package cloud.fogbow.cli.ras.order.volume;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import cloud.fogbow.cli.utils.KeyValueUtil.KeyValueConverter;
import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;

public class Volume implements FogbowCliResource {
	public static final String NAME_KEY = "name";
	public static final String PROVIDER_KEY = "provider";
	public static final String REQUIREMENTS_KEY = "requirements";
	public static final String VOLUME_SIZE_KEY = "volumeSize";

	@Parameter(names = {CliCommonParameters.PROVIDER_COMMAND_KEY},
			description = Documentation.CommonParameters.PROVIDER)
	private String provider = null;
	
	public static final String VOLUME_SIZE_COMMAND_KEY =  "--volume-size";
	@Parameter(names = { VOLUME_SIZE_COMMAND_KEY }, description = "Volume size")
	private Integer volumeSize = null;

	public static final String NAME_COMMAND_KEY =  "--name";
	@Parameter(names = {NAME_COMMAND_KEY}, description = "Name")
	private String name = null;

	public static final String REQUIREMENTS = "--requirements";
	@Parameter(names = { REQUIREMENTS }, converter = KeyValueConverter.class)
	private Map<String, String> requirements;

	public Volume() {
		
	}

	public Volume(String provider, Integer volumeSize, String name, Map<String, String> requirements) {
		this.provider = provider;
		this.volumeSize = volumeSize;
		this.name = name;
		this.requirements = requirements;
	}

	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public Integer getVolumeSize() {
		return volumeSize;
	}
	
	public void setVolumeSize(Integer volumeSize) {
		this.volumeSize = volumeSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getRequirements() {
		return requirements;
	}

	public void setRequirements(Map<String, String> requirements) {
		this.requirements = requirements;
	}

	@Override
	public HashMap getHttpHashMap() {
		HashMap body = new HashMap();

		body.put(NAME_KEY, this.name);
		body.put(PROVIDER_KEY, this.provider);
		body.put(REQUIREMENTS_KEY, this.requirements);
		body.put(VOLUME_SIZE_KEY, this.volumeSize);

		return body;
	}
}

