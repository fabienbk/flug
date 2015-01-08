package com.fbksoft.flug.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

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
	 * @parameter default-value="target/generated-sources/flug"
	 * @required
	 */
	// File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

		JavaDocBuilder builder = new JavaDocBuilder();

		System.out.println("sources:");
		for (String fileName : sources) {
			System.out.println(sources);

			List compileSourceRoots = project.getCompileSourceRoots();
			for (Object object : compileSourceRoots) {
				System.out.println("***" + object.toString());
			}

			try {
				builder.addSource(new File(new File((String) compileSourceRoots.get(0)), fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}

			JavaClass[] classes = builder.getClasses();
			for (JavaClass javaClass : classes) {
				System.out.println("class name  " + javaClass.getName());
			}
		}

	}
}
