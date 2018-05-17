package org.fogbowcloud.cli.compute;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.internal.util.reflection.Whitebox;

import com.google.gson.Gson;


public class TestCommandCompute {
	
	private Compute compute;
	
	private CommandCompute commandCompute;
	
	@Before
	public void setUp(){
		//PowerMockito.mockStatic(HttpUtil.class);
		this.commandCompute = Mockito.spy(new CommandCompute());
		this.compute = new Compute();
	}
	
	@Test
	public void testComputeToJson() {
		
		Whitebox.setInternalState(this.compute, "providingMember", "providingMember");
		Whitebox.setInternalState(this.compute, "publicKey", "publicKey");
		Whitebox.setInternalState(this.compute, "imageName", "imageName");
		Whitebox.setInternalState(this.compute, "vCPU", "vCPU");
		Whitebox.setInternalState(this.compute, "memory", "memory");
		Whitebox.setInternalState(this.compute, "disk", "disk");
		
		Whitebox.setInternalState(commandCompute, "compute", compute);
		
		String computeJson = commandCompute.computeToJson();
		
		Gson gson = new Gson();
		Compute computeFromJson = gson.fromJson(computeJson, Compute.class);
		
		Assert.assertThat(compute, new ReflectionEquals(computeFromJson, new String[0]));
	}
	
}
