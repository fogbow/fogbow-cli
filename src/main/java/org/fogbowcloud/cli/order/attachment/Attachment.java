package org.fogbowcloud.cli.order.attachment;

import com.beust.jcommander.Parameter;

public class Attachment {
	
	public static final String PROVIDER_COMMAND_KEY =  "--provider";
	@Parameter(names = {PROVIDER_COMMAND_KEY}, description = "Provider")
	private String provider = null;
	
	public static final String VOLUME_ID_COMMAND_KEY =  "--volumeId";
	@Parameter(names = {VOLUME_ID_COMMAND_KEY}, description = "Volume Id")
	private String volumeId = null;
	
	public static final String COMPUTE_ID_COMMAND_KEY =  "--computeId";
	@Parameter(names = {COMPUTE_ID_COMMAND_KEY}, description = "Compute Id")
	private String computeId = null;
	
	public static final String DEVICE_COMMAND_KEY =  "--device";
	@Parameter(names = { DEVICE_COMMAND_KEY }, description = "Device")
	private String device = null;
	
	public Attachment(){
		
	}
	
	public Attachment(String provider, String volumeId, String computeId, String device) {
		super();
		this.provider = provider;
		this.volumeId = volumeId;
		this.computeId = computeId;
		this.device = device;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public String getVolumeId() {
		return volumeId;
	}
	
	public String getComputeId() {
		return computeId;
	}
	
	public String getDevice() {
		return device;
	}
}
