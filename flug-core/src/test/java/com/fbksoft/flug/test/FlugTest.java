package com.fbksoft.flug.test;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import com.fbksoft.flug.FlugApp;
import com.thoughtworks.qdox.JavaDocBuilder;

public class FlugTest {

	@Test
	public void testJavaGen() throws Exception {

		File parent = new File("d:/dev/helios/HeliosCore/src/test/java/");

		String generatedSourcesPackage = "com.de.helios.test.tools.flux.xxx";
		File outputDir = new File(parent, "com/de/helios/test/tools/flux/xxx");

		JavaDocBuilder builder = new JavaDocBuilder();

		Files.walk(new File("D:/dev/helios/HeliosExternal/target/generated-sources/xjc/").toPath()).forEach(f -> {
			try {
				builder.addSource(f.toFile());
			} catch (Exception e) {
			}
		});

		String topLevelClass = "com.de.helios.data.x12x13.AffairesType";

		new FlugApp(topLevelClass, builder, generatedSourcesPackage, outputDir).run();

	}
}
