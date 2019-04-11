package cloud.fogbow.cli.ras.order.attachment;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.ras.FogbowCliResource;
import com.beust.jcommander.Parameter;

import java.util.HashMap;

public class Attachment implements FogbowCliResource {
	public static final String COMPUTE_ID_KEY = "computeId";
	public static final String DEVICE_KEY = "device";
	public static final String PROVIDER_KEY = "provider";
	public static final String VOLUME_ID_KEY = "volumeId";

	@Parameter(names = {CliCommonParameters.PROVIDER_COMMAND_KEY},
			description = Documentation.CommonParameters.PROVIDER)
	private String provider = null;
	
	public static final String VOLUME_ID_COMMAND_KEY =  "--volume-id";
	@Parameter(names = {VOLUME_ID_COMMAND_KEY}, description = "Volume Id")
	private String volumeId = null;
	
	public static final String COMPUTE_ID_COMMAND_KEY =  "--compute-id";
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

	@Override
	public HashMap getHttpHashMap() {
		HashMap body = new HashMap();

		body.put(COMPUTE_ID_KEY, this.computeId);
		body.put(DEVICE_KEY, this.device);
		body.put(PROVIDER_KEY, this.provider);
		body.put(VOLUME_ID_KEY, this.volumeId);
		return body;
	}
}
