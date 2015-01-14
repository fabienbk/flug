package com.fbksoft.flug.test;

import java.io.File;

import org.junit.Test;

import com.fbksoft.flug.FlugApp;
import com.thoughtworks.qdox.JavaDocBuilder;

public class FlugTest {

	@Test
	public void testJavaGen() throws Exception {

		File parent = new File("d:/dev/helios/HeliosCore/src/test/java/");

		File outputDir = new File(parent, "com/de/helios/test/tools/flux/xxx");

		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSource(new File("D:/dev/helios/HeliosExternal/target/generated-sources/xjc/com/de/helios/data/x12x13/AffaireType.java"));

		new FlugApp(builder, "com.de.helios.data.x12x13", "com.de.helios.test.tools.flux.xxx", outputDir).run();

	}
}
