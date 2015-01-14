package com.fbksoft.flug;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.fbksoft.flug.model.BeanClass;
import com.fbksoft.flug.model.BeanPropertyDescriptor;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

public class FlugApp {

	private File outputDirectory = new File(".");
	public Stack<BuilderEntity> workList = new Stack<>();
	public Set<String> visitedClasses = new HashSet<>();
	public Set<String> includeSet = new HashSet<>();

	private String outputPackageName;

	public FlugApp(JavaDocBuilder builder, String rootPackage, String outputPackageName, File outputDirectory) {
		init(rootPackage, outputPackageName, outputDirectory);

		for (JavaClass javaClass : builder.getClasses()) {
			workList.push(new BuilderEntity(javaClass, true));
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

		System.out.println("*** Writer builder for " + beanClass.getQualifiedName());
		STGroup groupFile = new STGroupFile("src/main/stg/external.stg");

		ST entityTemplate = builder.isTopLevel() ? groupFile.getInstanceOf("topLevelBuilderClass") : groupFile.getInstanceOf("builderClass");
		String javaClassName = beanClass.getSimpleName();
		String javaClassQName = beanClass.getQualifiedName();

		for (BeanPropertyDescriptor beanProperty : beanClass.getBeanProperties()) {

			String propertyClassSimpleName = beanProperty.getClassSimpleName();
			String writeMethodName = beanProperty.getSetterName();
			String propertyName = beanProperty.getName();

			if (propertyClassSimpleName.equals("class")) {
				continue;
			}

			if (writeMethodName == null || propertyClassSimpleName == null) {
				continue;
			}

			String currentBuilderClass = builder.isTopLevel() ? javaClassName + "Builder" : javaClassName + "Builder<P>";

			String propertyBuilderName = propertyName + "Builder";

			if (beanProperty.isBuildable(includeSet)) {

				if (!visitedClasses.contains(beanProperty.getQualifiedName())) {
					workList.push(builder.getSubBuilder(beanProperty.getName(), includeSet));
				}

				ST fieldTemplate = groupFile.getInstanceOf("field");

				String fieldType = beanProperty.getClassSimpleName() + "Builder<" + javaClassName + "Builder" + (builder.isTopLevel() ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType);
				fieldTemplate.add("name", propertyBuilderName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Builder setter
				ST setterTemplate = groupFile.getInstanceOf("builderSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", beanProperty.getClassSimpleName());
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("parentBuilder", javaClassName);

				// instance init
				ST instanceInitTemplate = groupFile.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", beanProperty.getSetterName());
				instanceInitTemplate.add("value", propertyBuilderName + " == null ? null : " + propertyBuilderName + ".build()");
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else if (beanProperty.isBuildableCollection(includeSet)) {

				if (!visitedClasses.contains(beanProperty.getQualifiedName())) {
					workList.push(builder.getSubBuilder(beanProperty.getName(), includeSet));
				}

				ST fieldTemplate = groupFile.getInstanceOf("field");

				String fieldType = beanProperty.getQualifiedName();
				String fieldTypeGenericArgument = beanProperty.getGenericType()[0].getJavaClass().getName() + "Builder<" + javaClassName + "Builder"
								+ (builder.isTopLevel() ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType + "<" + fieldTypeGenericArgument + ">");
				fieldTemplate.add("name", propertyBuilderName);
				entityTemplate.add("fields", fieldTemplate.render());

				String concreteCollectionClass = getConcreteCollectionClassName(beanProperty.getJavaClass(), fieldTypeGenericArgument);

				// Builder setter
				ST setterTemplate = groupFile.getInstanceOf("collectionSetter");
				setterTemplate.add("currentBuilderClass", fieldTypeGenericArgument);
				setterTemplate.add("class", beanProperty.getClassSimpleName());
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("parentBuilder", javaClassName);
				setterTemplate.add("collectionClass", fieldType + "<" + fieldTypeGenericArgument + ">");

				setterTemplate.add("concreteCollectionClass", concreteCollectionClass);

				// instance init
				ST instanceInitTemplate = groupFile.getInstanceOf("instanceInitList");
				instanceInitTemplate.add("setter", beanProperty.getSetterName());
				instanceInitTemplate.add("propName", propertyBuilderName);
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else {
				// Non buildable property

				// Normal Field
				ST fieldTemplate = groupFile.getInstanceOf("field");
				fieldTemplate.add("type", beanProperty.getClassSimpleName());
				fieldTemplate.add("name", propertyName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Normal setter
				ST setterTemplate = groupFile.getInstanceOf("normalSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", javaClassName);
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("valueType", beanProperty.getQualifiedName());
				entityTemplate.add("setterMethods", setterTemplate.render());

				// instance init
				ST instanceInitTemplate = groupFile.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", writeMethodName);
				instanceInitTemplate.add("value", propertyName);
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

			}

		}

		entityTemplate.add("class", javaClassName);
		entityTemplate.add("qualifiedClass", javaClassQName);

		entityTemplate.add("package", outputPackageName);

		// entityTemplate.add("setterMethods", "");

		writeTemplate(outputDirectory, javaClassName + "Builder.java", entityTemplate);
	}

	private String getConcreteCollectionClassName(JavaClass javaClass, String genericType) {
		Class<?> clazz = null;
		if (javaClass.isA(Map.class.getName())) {
			clazz = HashMap.class;
		} else if (javaClass.isA(List.class.getName())) {
			clazz = ArrayList.class;
		} else if (javaClass.isA(Set.class.getName())) {
			clazz = HashSet.class;
		} else {
			clazz = ArrayList.class;
		}
		return clazz.getName() + "<" + genericType + ">";
	}

	private static void writeTemplate(File dir, String fileName, ST entityTemplate) throws IOException {
		FileWriter fileWriter = new FileWriter(new File(dir, fileName));
		fileWriter.write(entityTemplate.render());
		fileWriter.close();
	}

	public static void main(String[] args) {
	}

}
