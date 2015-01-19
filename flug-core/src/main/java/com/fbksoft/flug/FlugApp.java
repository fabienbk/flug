package com.fbksoft.flug;

import java.io.File;
import java.io.FileWriter;
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
import com.thoughtworks.qdox.model.JavaPackage;

public class FlugApp {

	// Init fields
	private File outputDirectory = new File(".");
	private Stack<BuilderEntity> workList = new Stack<>();
	private Set<String> visitedClasses = new HashSet<>();
	private Set<String> includeSet = new HashSet<>();
	private String outputPackageName;

	// Running fields
	private FileWriter fileWriter;
	private String rootFileName;
	private STGroup templateStore;

	public FlugApp(String topLevelClassName, JavaDocBuilder builder, String outputPackageName, File outputDirectory) {
		Set<String> rootPackages = new HashSet<>();

		for (JavaPackage javaPackage : builder.getPackages()) {
			rootPackages.add(javaPackage.getName());
		}

		this.outputPackageName = outputPackageName;
		this.outputDirectory = outputDirectory;
		this.includeSet = rootPackages;

		System.out.println("output directory = " + outputDirectory);
		templateStore = new STGroupFile("src/main/stg/external.stg");

		for (JavaClass javaClass : builder.getClasses()) {
			if (javaClass.getFullyQualifiedName().equals(topLevelClassName)) {
				this.rootFileName = javaClass.getName() + "Builder.java";
				workList.push(new BuilderEntity(javaClass, true));
				break;
			}
		}
	}

	public void run() throws Exception {

		this.fileWriter = new FileWriter(new File(outputDirectory, rootFileName));

		ST fileTemplate = templateStore.getInstanceOf("topLevelBuilderClass");
		fileTemplate.add("package", outputPackageName);

		while (!workList.isEmpty()) {
			writeNewBuilder(workList.pop(), fileTemplate);
		}

		fileWriter.write(fileTemplate.render());
		fileWriter.close();
	}

	private void writeNewBuilder(BuilderEntity builder, ST topLevelTemplate) throws Exception {
		BeanClass beanClass = builder.getBeanClass();

		System.out.println("*** Writer +" + builder.isTopLevel() + " builder for " + beanClass.getQualifiedName());

		ST entityTemplate = builder.isTopLevel() ? topLevelTemplate : templateStore.getInstanceOf("builderClass");
		String javaClassName = beanClass.getSimpleName();
		String javaClassQName = beanClass.getQualifiedName();

		for (BeanPropertyDescriptor beanProperty : beanClass.getBeanProperties()) {

			String propertyClassSimpleName = beanProperty.getClassSimpleName();
			String writeMethodName = beanProperty.getSetterName();
			String propertyName = beanProperty.getName();

			if (propertyClassSimpleName.equals("class")) {
				continue;
			}

			String currentBuilderClass = builder.isTopLevel() ? javaClassName + "Builder" : javaClassName + "Builder<P>";

			String propertyBuilderName = propertyName + "Builder";

			if (beanProperty.isBuildable(includeSet)) {

				System.out.println("   " + beanProperty.toString() + " is buildable");

				if (!visitedClasses.contains(beanProperty.getQualifiedName())) {
					visitedClasses.add(beanProperty.getQualifiedName());
					workList.push(builder.getSubBuilder(beanProperty.getName(), includeSet));
				}

				ST fieldTemplate = templateStore.getInstanceOf("field");

				String fieldType = beanProperty.getClassSimpleName() + "Builder<" + javaClassName + "Builder" + (builder.isTopLevel() ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType);
				fieldTemplate.add("name", propertyBuilderName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Builder setter
				ST setterTemplate = templateStore.getInstanceOf("builderSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", beanProperty.getClassSimpleName());
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("parentBuilder", javaClassName);

				// instance init
				ST instanceInitTemplate = templateStore.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", beanProperty.getSetterName());
				instanceInitTemplate.add("value", propertyBuilderName + " == null ? null : " + propertyBuilderName + ".build()");
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else if (beanProperty.isBuildableCollection(includeSet)) {

				System.out.println("   " + beanProperty.toString() + " is collection - buildable");

				String fullyQualifiedName = beanProperty.getGenericType()[0].getJavaClass().getFullyQualifiedName();
				if (!visitedClasses.contains(fullyQualifiedName)) {
					visitedClasses.add(fullyQualifiedName);
					workList.push(builder.getSubBuilder(beanProperty.getName(), includeSet));
				}

				ST fieldTemplate = templateStore.getInstanceOf("field");

				String fieldType = beanProperty.getQualifiedName();
				String fieldTypeGenericArgument = beanProperty.getGenericType()[0].getJavaClass().getName() + "Builder<" + javaClassName + "Builder"
								+ (builder.isTopLevel() ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType + "<" + fieldTypeGenericArgument + ">");
				fieldTemplate.add("name", propertyBuilderName);
				entityTemplate.add("fields", fieldTemplate.render());

				String concreteCollectionClass = getConcreteCollectionClassName(beanProperty.getJavaClass(), fieldTypeGenericArgument);

				// Builder setter
				ST setterTemplate = templateStore.getInstanceOf("collectionSetter");
				setterTemplate.add("currentBuilderClass", fieldTypeGenericArgument);
				setterTemplate.add("class", beanProperty.getClassSimpleName());
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("parentBuilder", javaClassName);
				setterTemplate.add("collectionClass", fieldType + "<" + fieldTypeGenericArgument + ">");

				setterTemplate.add("concreteCollectionClass", concreteCollectionClass);

				// instance init
				ST instanceInitTemplate = templateStore.getInstanceOf("instanceInitList");
				instanceInitTemplate.add("setter", beanProperty.getGetterName());
				instanceInitTemplate.add("propName", propertyBuilderName);
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else {
				// Non buildable property

				System.out.println("   " + beanProperty.toString() + " is NOT buildable");

				// Normal Field
				ST fieldTemplate = templateStore.getInstanceOf("field");
				fieldTemplate.add("type", beanProperty.getQualifiedName());
				fieldTemplate.add("name", propertyName);
				entityTemplate.add("fields", fieldTemplate.render());

				// Normal setter
				ST setterTemplate = templateStore.getInstanceOf("normalSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", javaClassName);
				setterTemplate.add("propertyName", propertyName);
				setterTemplate.add("valueType", beanProperty.getQualifiedName());
				entityTemplate.add("setterMethods", setterTemplate.render());

				// instance init
				ST instanceInitTemplate = templateStore.getInstanceOf("instanceInit");
				instanceInitTemplate.add("setter", writeMethodName);
				instanceInitTemplate.add("value", propertyName);
				entityTemplate.add("instanceInit", instanceInitTemplate.render());

			}

		}

		entityTemplate.add("class", javaClassName);
		entityTemplate.add("qualifiedClass", javaClassQName);

		if (!builder.isTopLevel()) {
			entityTemplate.add("package", outputPackageName);
			topLevelTemplate.add("innerClass", entityTemplate.render());
		}
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

}
