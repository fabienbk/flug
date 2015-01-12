package com.fbksoft.flug;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fbksoft.flug.model.BeanClass;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;

public class BuilderEntity {

	public BeanClass beanClass;
	public String parentBuilder;

	private JavaClass javaClass;

	private Map<String, List<PropertyDescriptor>> propertiesByName;

	public BuilderEntity(JavaClass javaClass, String parentBuilder) {
		this.javaClass = javaClass;
		this.beanClass = BeanClass.from(javaClass);
		this.parentBuilder = parentBuilder;
	}

	public BuilderEntity(Class<?> clazz, String parentBuilder) {
		try {
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			propertiesByName = Arrays.asList(propertyDescriptors).stream().collect(Collectors.groupingBy(PropertyDescriptor::getName));
		} catch (IntrospectionException e) {
		}

		this.beanClass = BeanClass.from(clazz);
		this.parentBuilder = parentBuilder;
	}

	public BeanClass getBeanClass() {
		return beanClass;
	}

	public String getBuilderClassSimpleName() {
		return beanClass.getSimpleName() + "Builder";
	}

	public BuilderEntity getSubBuilder(String propertyName) {
		if (javaClass != null) {
			BeanProperty beanProperty = javaClass.getBeanProperty(propertyName);
			JavaClass propJavaClass = beanProperty.getType().getJavaClass();
			return new BuilderEntity(propJavaClass, getBuilderClassSimpleName());
		} else {
			Class<?> propertyType = propertiesByName.get(propertyName).get(0).getPropertyType();
			return new BuilderEntity(propertyType, getBuilderClassSimpleName());
		}
	}
}
