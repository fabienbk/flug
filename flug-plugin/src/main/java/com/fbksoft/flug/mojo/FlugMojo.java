package com.fbksoft.flug.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.fbksoft.flug.FlugApp;
import com.thoughtworks.qdox.JavaDocBuilder;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class FlugMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 * @since 1.0
	 */
	MavenProject project;

	/**
	 * Package
	 *
	 * @parameter
	 * @required
	 */
	List<String> sources;

	/**
	 * packageName
	 *
	 * @parameter
	 * @required
	 */
	String packageName;

	/**
	 * topLevelClassName
	 *
	 * @parameter
	 * @required
	 */
	String topLevelClassName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		JavaDocBuilder builder = new JavaDocBuilder();

		for (String fileName : sources) {
			List<?> compileSourceRoots = project.getCompileSourceRoots();
			try {
				builder.addSource(new File(new File((String) compileSourceRoots.get(0)), fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}

			String baseOutputDirectoryPath = project.getBuild().getOutputDirectory();

			System.out.println(baseOutputDirectoryPath);

			File baseOutputDirectory = new File(baseOutputDirectoryPath);
			File outputDirectory = new File(baseOutputDirectory, "generated-sources/flug");

			if (!outputDirectory.exists()) {
				outputDirectory.mkdir();
				System.out.println("Created " + outputDirectory.getAbsolutePath());
			} else {
				System.out.println("Output " + outputDirectory.getAbsolutePath() + " exists");
			}

			try {
				new FlugApp(topLevelClassName, builder, packageName, outputDirectory).run();
			} catch (Exception e) {
				e.printStackTrace();
			}

			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
		}

	}
}
