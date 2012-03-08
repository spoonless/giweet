package org.giweet;

import static org.junit.Assert.*;

import java.util.List;

import org.giweet.annotation.Step;
import org.junit.Test;

public class MethodStepScannerTest {
	
	public static class DummyStep {
		
		public int step1;
		public int step2;
		public int step3;
		
		public void methodNotStep(){}

		@Step
		public void methodStep_1(){
			step1++;
		}
		
		@Step("method step 2")
		public void methodStep2(){
			step2++;
		}

		@Step({"method step 3", "other method step 3"})
		public void methodStep3(){
			step3++;
		}
	}

	@Test
	public void canScan() throws Exception {
		MethodStepScanner underTest = new MethodStepScanner();
		DummyStep instance = new DummyStep();
		
		List<MethodStepDescriptor> result = underTest.scan(instance);
		
		assertEquals(4, result.size());
		
		MethodStepDescriptor desc = result.get(0);
		assertEquals("method step 1", desc.getValue());
		desc.invoke();
		assertEquals(1, instance.step1);

		desc = result.get(1);
		assertEquals("method step 2", desc.getValue());
		desc.invoke();
		assertEquals(1, instance.step2);

		desc = result.get(2);
		assertEquals("method step 3", desc.getValue());
		desc.invoke();
		assertEquals(1, instance.step3);

		desc = result.get(3);
		assertEquals("other method step 3", desc.getValue());
		desc.invoke();
		assertEquals(2, instance.step3);
	}

}
