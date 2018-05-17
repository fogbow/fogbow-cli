package org.fogbowcloud.cli.compute;

import com.beust.jcommander.Parameter;

public class Compute {

	@Parameter(names = { "--providing-member", "-pm" }, description = "Providing member")
	private String providingMember = null;
	
	@Parameter(names = { "--public-key", "-pk" }, description = "Public key")
	private String publicKey = null;
	
	@Parameter(names = { "--image-name", "-i" }, description = "Image name")
	private String imageName = null;
	
	@Parameter(names = { "--vcpu", "-v" }, description = "Vcpu")
	private String vCPU = null;	
	
	@Parameter(names = { "--memory", "-m" }, description = "Memory")
	private String memory = null;	
	
	@Parameter(names = { "--disk", "-d" }, description = "Disk")
	private String disk = null;
	
}
