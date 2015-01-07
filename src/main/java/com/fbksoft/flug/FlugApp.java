package com.fbksoft.flug;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.de.helios.data.x12x13.AffaireType;

public class FlugApp {

	public Stack<BuilderEntity> workList = new Stack<>();
	public Set<String> visitedClasses = new HashSet<>();
	public Set<String> includeSet = new HashSet<>();

	private String outputPackageName = "com.fbksoft.flug.gen";

	public void run() throws Exception {
		workList.push(new BuilderEntity(AffaireType.class, null));
		includeSet.add("com.de");

		while (!workList.isEmpty()) {
			writeNewClass(workList.pop());
		}
	}

	private void writeNewClass(BuilderEntity classItem) throws Exception {

		Class<?> domainClass = classItem.domainClass;
		System.out.println(domainClass.getName() + " " + classItem.parentBuilder);

		STGroup groupFile = new STGroupFile("src/main/stg/test.stg");

		boolean topLevel = classItem.parentBuilder == null;

		ST entityTemplate = topLevel ? groupFile.getInstanceOf("topLevelBuilderClass") : groupFile.getInstanceOf("builderClass");

		BeanInfo beanInfo = Introspector.getBeanInfo(domainClass);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

			if (propertyDescriptor.getName().equals("class") || propertyDescriptor == null) {
				continue;
			}

			if (propertyDescriptor.getWriteMethod() == null) {
				continue;
			}

			Class<?> propertyType = propertyDescriptor.getPropertyType();
			if (propertyType == null) {
				continue;
			}

			Package propertyPackage = propertyType.getPackage();

			String currentBuilderClass = topLevel ? domainClass.getSimpleName() + "Builder" : domainClass.getSimpleName() + "Builder<P>";

			if (propertyPackage != null && propertyPackage.getName().startsWith(domainClass.getPackage().getName())) {

				if (!visitedClasses.contains(propertyType.getName())) {
					workList.push(new BuilderEntity(Class.forName(propertyType.getName()), domainClass.getSimpleName() + "Builder"));
				}

				ST fieldTemplate = groupFile.getInstanceOf("field");

				String fieldType = propertyType.getSimpleName() + "Builder<" + domainClass.getSimpleName() + "Builder" + (topLevel ? "" : "<P>") + ">";

				fieldTemplate.add("type", fieldType);
				fieldTemplate.add("name", propertyDescriptor.getName() + "Builder");
				entityTemplate.add("fields", fieldTemplate.render());

				// Builder setter

				ST setterTemplate = groupFile.getInstanceOf("builderSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", propertyType.getSimpleName());
				setterTemplate.add("valueName", propertyDescriptor.getName());
				setterTemplate.add("parentBuilder", domainClass.getSimpleName());

				entityTemplate.add("setterMethods", setterTemplate.render());

			} else {

				// Normal Field
				ST fieldTemplate = groupFile.getInstanceOf("field");
				fieldTemplate.add("type", propertyType.getName());
				fieldTemplate.add("name", propertyDescriptor.getName());
				entityTemplate.add("fields", fieldTemplate.render());

				// Normal setter
				ST setterTemplate = groupFile.getInstanceOf("normalSetter");
				setterTemplate.add("currentBuilderClass", currentBuilderClass);
				setterTemplate.add("class", domainClass.getSimpleName());
				setterTemplate.add("valueName", propertyDescriptor.getName());
				setterTemplate.add("valueType", propertyDescriptor.getPropertyType().getName());

				entityTemplate.add("setterMethods", setterTemplate.render());
			}

		}

		entityTemplate.add("class", domainClass.getSimpleName());
		entityTemplate.add("qualifiedClass", domainClass.getName());

		entityTemplate.add("package", outputPackageName);
		entityTemplate.add("subBuildersFields", "");

		entityTemplate.add("subBuildersInit", "");
		entityTemplate.add("setterMethods", "");
		entityTemplate.add("instanceInit", "");

		writeTemplate(new File("src/main/java/com/fbksoft/flug/gen"), domainClass.getSimpleName() + "Builder.java", entityTemplate);
		visitedClasses.add(domainClass.getName());
	}

	private static void writeTemplate(File dir, String fileName, ST entityTemplate) throws IOException {
		FileWriter fileWriter = new FileWriter(new File(dir, fileName));
		fileWriter.write(entityTemplate.render());
		fileWriter.close();
	}

	public static void main(String[] args) throws Exception {
		new FlugApp().run();

		// new
		// AffaireTypeBuilder().annulation().commentaire("hello").end().demande().communicationDistributeur().attenteRetourFrn(Boolean.TRUE);
		//
		// AffaireType affaireType = new AffaireType();
		// AffaireAnnulationType annulation = new AffaireAnnulationType();
		// annulation.setCommentaire("hello");
		// affaireType.setAnnulation(annulation);
		// DemandeType demande = new DemandeType();
		// DemandeCommunicationDistributeurType demandeCommunication = new DemandeCommunicationDistributeurType();
		// demandeCommunication.setAttenteRetourFrn(Boolean.TRUE);
		// demande.setCommunicationDistributeur(demandeCommunication);
		// affaireType.setDemande(demande);

	}

}
