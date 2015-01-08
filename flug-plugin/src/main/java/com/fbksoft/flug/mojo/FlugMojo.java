package com.fbksoft.flug.mojo;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
	 * Sources
	 *
	 * @parameter
	 * @required
	 */
	List<String> sources;

	/**
	 * @parameter default-value="target/generated-sources/flug"
	 * @required
	 */
	File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		System.out.println("sources:");
		for (String string : sources) {
			System.out.println(sources);
		}
		System.out.println("outputDir: " + outputDirectory);
	}
}
