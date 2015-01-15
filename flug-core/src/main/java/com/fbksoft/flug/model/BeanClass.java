package com.fbksoft.flug.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

public class BeanClass {

	private JavaClass clazz;
	private Map<String, BeanPropertyDescriptor> beanProperties = new HashMap<>();

	public BeanClass(JavaClass clazz) {
		this.clazz = clazz;
		for (BeanProperty beanProperty : clazz.getBeanProperties()) {
			beanProperties.put(beanProperty.getName(), new BeanPropertyDescriptor(clazz, beanProperty));
		}

		System.out.println(beanProperties);

		List<JavaMethod> methods = Arrays.asList(clazz.getMethods());

		for (JavaMethod javaMethod : methods) {
			if (javaMethod.isPropertyAccessor()) {
				String propertyName = javaMethod.getPropertyName();

				// search for matching mutator
				boolean foundMutator = methods.stream().anyMatch(p -> p.isPropertyMutator() && p.getPropertyName().equals(propertyName));

				if (!foundMutator) {
					BeanProperty beanProperty = new BeanProperty(propertyName);
					beanProperty.setAccessor(javaMethod);
					beanProperty.setType(javaMethod.getPropertyType());

					BeanPropertyDescriptor value = new BeanPropertyDescriptor(clazz, beanProperty);
					value.setReadOnly(true);
					beanProperties.put(beanProperty.getName(), value);
				}
			}
		}

		System.out.println(beanProperties);
	}

	public String getQualifiedName() {
		return clazz.getFullyQualifiedName().replaceAll("\\$", ".");
	}

	public BeanPropertyDescriptor getBeanProperty(String name) {
		return beanProperties.get(name);
	}

	public Collection<BeanPropertyDescriptor> getBeanProperties() {
		return beanProperties.values();
	}

	public String getSimpleName() {
		return clazz.getName();
	}

	public String getPackageName() {
		return clazz.getPackageName();
	}

}
