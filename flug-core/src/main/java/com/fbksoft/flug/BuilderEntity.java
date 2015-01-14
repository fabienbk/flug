package com.fbksoft.flug;

import java.util.Set;

import com.fbksoft.flug.model.BeanClass;
import com.fbksoft.flug.model.BeanPropertyDescriptor;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

public class BuilderEntity {

	private BeanClass beanClass;
	private boolean topLevel;

	public BuilderEntity(JavaClass javaClass, boolean topLevel) {
		this.beanClass = new BeanClass(javaClass);
		this.topLevel = topLevel;
	}

	public BeanClass getBeanClass() {
		return beanClass;
	}

	public String getBuilderClassSimpleName() {
		return beanClass.getSimpleName() + "Builder";
	}

	public BuilderEntity getSubBuilder(String propertyName, Set<String> includeSet) {
		BeanPropertyDescriptor beanProperty = beanClass.getBeanProperty(propertyName);
		JavaClass javaClass = beanProperty.getJavaClass();

		if (beanProperty.isBuildableCollection(includeSet)) {
			Type genericType = beanProperty.getGenericType()[0];
			return new BuilderEntity(genericType.getJavaClass(), false);
		} else {
			return new BuilderEntity(javaClass, false);
		}
	}

	public boolean isTopLevel() {
		return topLevel;
	}
}
