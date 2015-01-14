package com.fbksoft.flug.model;

import java.util.Collection;
import java.util.Set;

import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.Type;

public class BeanPropertyDescriptor {

	private BeanProperty property;

	public BeanPropertyDescriptor(BeanProperty beanProperty) {
		this.property = beanProperty;

		if (getGenericType() != null && getGenericType().length == 1) {
			System.out.println(getGenericType()[0]);
		}
	}

	public JavaClass getJavaClass() {
		return property.getType().getJavaClass();
	}

	public String getClassSimpleName() {
		return property.getType().getJavaClass().getName();
	}

	public String getSetterName() {
		JavaMethod setter = property.getMutator();
		return setter == null ? null : setter.getName();
	}

	public String getPackageName() {
		JavaPackage packageObject = property.getType().getJavaClass().getPackage();
		return packageObject == null ? null : packageObject.getName();
	}

	public String getQualifiedName() {
		return property.getType().getFullyQualifiedName();
	}

	public String getName() {
		return property.getName();
	}

	public boolean isBuildable(Set<String> includeSet) {
		return getPackageName() != null && includeSet.stream().anyMatch(p -> p.equals(getPackageName()));
	}

	public boolean isCollection() {
		return property.getType().getJavaClass().isA(Collection.class.getName());
	}

	public boolean isBuildableCollection(Set<String> includeSet) {
		if (!isCollection()) {
			return false;
		}
		Type[] genericTypes = getGenericType();
		if (genericTypes == null || genericTypes.length > 1) {
			return false;
		}

		String genericTypePackage = genericTypes[0].getJavaClass().getPackageName();
		if (includeSet.stream().anyMatch(p -> p.equals(genericTypePackage))) {
			return true;
		}

		return false;
	}

	public Type[] getGenericType() {
		return property.getType().getActualTypeArguments();
	}

}
