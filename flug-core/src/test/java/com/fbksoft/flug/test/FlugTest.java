package com.fbksoft.flug.test;

import org.junit.Test;

import com.de.testflux.Enterprise;
import com.fbksoft.flug.FlugApp;

import java.io.File;

public class FlugTest {

	@Test
	public void test() throws Exception {

		File outputDir = new File("src/test/java/com/de/testflux/gen");
		System.out.println(outputDir.getAbsolutePath());

		new FlugApp(new Class[] { Enterprise.class },"com.de.testflux", "com.de.testflux.gen", outputDir).run();
	}
}
