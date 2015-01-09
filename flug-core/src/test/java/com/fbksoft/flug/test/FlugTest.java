package com.fbksoft.flug.test;

import org.junit.Test;

import com.de.testflux.Enterprise;
import com.fbksoft.flug.FlugApp;

public class FlugTest {

	@Test
	public void test() throws Exception {
		new FlugApp(new Class[] { Enterprise.class }, "com.de.testflux").run();
	}
}
