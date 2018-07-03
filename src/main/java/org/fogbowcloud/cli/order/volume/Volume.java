package org.fogbowcloud.cli.order.volume;

import com.beust.jcommander.Parameter;

public class Volume {
	
	public static final String PROVIDING_MEMBER_COMMAND_KEY =  "--providing-member";
	@Parameter(names = { PROVIDING_MEMBER_COMMAND_KEY }, description = "Providing member")
	private String providingMember = null;
	
	public static final String VOLUME_SIZE_COMMAND_KEY =  "--volume-size";
	@Parameter(names = { VOLUME_SIZE_COMMAND_KEY }, description = "Volume size")
	private Integer volumeSize = null;
	
	public Volume() {
		
	}
	
	public Volume(String providingMember, Integer volumeSize) {
		super();
		this.providingMember = providingMember;
		this.volumeSize = volumeSize;
	}

	public String getProvidingMember() {
		return providingMember;
	}
	
	public void setProvidingMember(String providingMember) {
		this.providingMember = providingMember;
	}
	
	public Integer getVolumeSize() {
		return volumeSize;
	}
	
	public void setVolumeSize(Integer volumeSize) {
		this.volumeSize = volumeSize;
	}
}
