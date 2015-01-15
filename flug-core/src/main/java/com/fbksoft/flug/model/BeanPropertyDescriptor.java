package com.fbksoft.flug.model;

import java.util.Collection;
import java.util.Set;

import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.Type;

/**
 * @author fkoch
 *
 */
public class BeanPropertyDescriptor {

	private BeanProperty property;
	private boolean readOnly;

	public BeanPropertyDescriptor(JavaClass clazz, BeanProperty beanProperty) {
		this.property = beanProperty;
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
		String packageName = getPackageName();
		if (packageName != null) {

			if (property.getType().getJavaClass().isEnum()) {
				return false;
			}

			return includeSet.stream().anyMatch(p -> p.equals(packageName));
		} else {
			return false;
		}
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

		JavaClass javaClass = genericTypes[0].getJavaClass();
		String genericTypePackage = javaClass.getPackageName();

		if (includeSet.stream().anyMatch(p -> p.equals(genericTypePackage))) {
			return true;
		}

		return false;
	}

	public Type[] getGenericType() {
		return property.getType().getActualTypeArguments();
	}

	@Override
	public String toString() {
		return property.getName();
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getGetterName() {
		return property.getAccessor().getName();
	}
}
