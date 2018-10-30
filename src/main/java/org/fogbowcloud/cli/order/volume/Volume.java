package org.fogbowcloud.cli.order.volume;

import com.beust.jcommander.Parameter;

public class Volume {
	
	public static final String PROVIDER_COMMAND_KEY =  "--provider";
	@Parameter(names = {PROVIDER_COMMAND_KEY}, description = "Provider")
	private String provider = null;
	
	public static final String VOLUME_SIZE_COMMAND_KEY =  "--volume-size";
	@Parameter(names = { VOLUME_SIZE_COMMAND_KEY }, description = "Volume size")
	private Integer volumeSize = null;

	public static final String NAME_COMMAND_KEY =  "--name";
	@Parameter(names = {NAME_COMMAND_KEY}, description = "Name")
	private String name = null;
	
	public Volume() {
		
	}

	public Volume(String provider, Integer volumeSize, String name) {
		super();
		this.provider = provider;
		this.volumeSize = volumeSize;
		this.name = name;
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
}
