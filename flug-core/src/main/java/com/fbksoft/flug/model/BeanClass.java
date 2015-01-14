package com.fbksoft.flug.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;

public class BeanClass {

	private JavaClass clazz;
	private Map<String, BeanPropertyDescriptor> beanProperties = new HashMap<>();

	public BeanClass(JavaClass clazz) {
		this.clazz = clazz;

		for (BeanProperty beanProperty : clazz.getBeanProperties()) {
			beanProperties.put(beanProperty.getName(), new BeanPropertyDescriptor(beanProperty));
		}

	}

	public String getQualifiedName() {
		return clazz.getFullyQualifiedName();
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

}
