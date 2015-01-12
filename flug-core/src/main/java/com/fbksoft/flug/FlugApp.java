package com.fbksoft.flug;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.fbksoft.flug.model.BeanClass;
import com.fbksoft.flug.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;

public class FlugApp {

	private File outputDirectory = new File(".");
	public Stack<BuilderEntity> workList = new Stack<>();
	public Set<String> visitedClasses = new HashSet<>();
	public Set<String> includeSet = new HashSet<>();

	private String outputPackageName;

	public FlugApp(JavaClass[] classes, String rootPackage, String outputPackageName, File outputDirectory) {
		init(rootPackage, outputPackageName, outputDirectory);

		for (JavaClass javaClass : classes) {
			workList.push(new BuilderEntity(javaClass, null));
		}
	}

	public FlugApp(Class<?>[] classes, String rootPackage, String outputPackageName, File outputDirectory) {
		init(rootPackage, outputPackageName, outputDirectory);

		for (Class<?> clazz : classes) {
			workList.push(new BuilderEntity(clazz, null));
		}
	}

	private void init(String rootPackage, String outputPackageName, File outputDirectory) {
		this.outputPackageName = outputPackageName;
		this.outputDirectory = outputDirectory;
		this.includeSet.add(rootPackage);

		System.out.println("output directory = " + outputDirectory);
	}

	public void run() throws Exception {
		while (!workList.isEmpty()) {
			writeNewBuilder(workList.pop());
		}
	}

	private void writeNewBuilder(BuilderEntity classItem) throws Exception {
		BeanClass beanClass = classItem.getBeanClass();

		writeNewBuilder(classItem, beanClass);

		visitedClasses.add(beanClass.getSimpleName());
	}

	private void writeNewBuilder(BuilderEntity builder, BeanClass beanClass) throws IntrospectionException, ClassNotFoundException, IOException {
		STGroup groupFile = new STGroupFile("src/main/stg/external.stg");

		boolean topLevel = builder.parentBuilder == null;

		ST entityTemplate = topLevel ? groupFile.getInstanceOf("topLevelBuilderClass") : groupFile.getInstanceOf("builderClass");
		String javaClassName = beanClass.getSimpleName();
		String javaClassQName = beanClass.getQualifiedName();

		BeanProperty[] beanProperties = beanClass.getBeanProperties();

		for (BeanProperty beanProperty : beanProperties) {

			String propertyClassSimpleName = beanProperty.getClassSimpleName();
			String writeMethodName = beanProperty.getSetterName();

			if (propertyClassSimpleName.equals("class")) {
				continue;
			}

			if (writeMethodName == null || propertyClassSimpleName == null) {
				continue;
			}

			String propertyPackageName = beanProperty.getPackageName();

			String currentBuilderClass = topLevel ? javaClassName + "Builder" : javaClassName + "Builder<P>";

			String propertyBuilderName = propertyClassSimpleName + "Builder";

			if (isBuildable(propertyPackageName)) {

				if (!visitedClasses.contains(beanProperty.getQualifiedName())) {
					workList.push(builder.getSubBuilder(beanProperty.getName()));
				}

				ST fieldTemplate = groupFile.getInstanceOf("field");

				String fieldType = beanProperty.getClassSimpleName() + "Builder<" + javaClassName + "Builder" + (topLevel ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType);
				fieldTemplate.add("name", propertyBuilderName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Builder setter
				ST setterTemplate = groupFile.getInstanceOf("builderSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", beanProperty.getClassSimpleName());
				setterTemplate.add("valueName", propertyClassSimpleName);
				setterTemplate.add("parentBuilder", javaClassName);

				// instance init
				ST instanceInitTemplate = groupFile.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", beanProperty.getSetterName());
				instanceInitTemplate.add("value", propertyBuilderName + " == null ? null : " + propertyBuilderName + ".build()");
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else {
				// Non buildable property

				// Normal Field
				ST fieldTemplate = groupFile.getInstanceOf("field");
				fieldTemplate.add("type", beanProperty.getClassSimpleName());
				fieldTemplate.add("name", propertyClassSimpleName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Normal setter
				ST setterTemplate = groupFile.getInstanceOf("normalSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", javaClassName);
				setterTemplate.add("valueName", propertyClassSimpleName);
				setterTemplate.add("valueType", beanProperty.getQualifiedName());
				entityTemplate.add("setterMethods", setterTemplate.render());

				// instance init
				ST instanceInitTemplate = groupFile.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", writeMethodName);
				instanceInitTemplate.add("value", propertyClassSimpleName);
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

			}

		}

		entityTemplate.add("class", javaClassName);
		entityTemplate.add("qualifiedClass", javaClassQName);

		entityTemplate.add("package", outputPackageName);

		// entityTemplate.add("setterMethods", "");

		writeTemplate(outputDirectory, javaClassName + "Builder.java", entityTemplate);
	}

	private boolean isBuildable(String propertyPackageName) {
		return propertyPackageName != null && includeSet.stream().anyMatch(p -> p.equals(propertyPackageName));
	}

	private static void writeTemplate(File dir, String fileName, ST entityTemplate) throws IOException {
		FileWriter fileWriter = new FileWriter(new File(dir, fileName));
		fileWriter.write(entityTemplate.render());
		fileWriter.close();
	}

}
