package com.fbksoft.flug.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.thoughtworks.qdox.model.JavaClass;

public class BeanClass {

	private String simpleName;
	private BeanProperty[] beanProperties;
	private String qualifiedName;

	public static BeanClass from(Class<?> clazz) {
		BeanClass beanClass = new BeanClass();
		beanClass.setSimpleName(clazz.getSimpleName());
		beanClass.setQualifiedName(clazz.getName());

		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		BeanProperty[] beanProperties = new BeanProperty[propertyDescriptors.length];

		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			//@formatter:off
			
			Class<?> propertyType = propertyDescriptor.getPropertyType();			
			Package packageName = propertyType.getPackage();			
			Method writeMethod = propertyDescriptor.getWriteMethod();
			
			beanProperties[i] = new BeanProperty(
							propertyDescriptor.getName(),
							propertyType.getSimpleName(), 
							writeMethod == null ? null : writeMethod.getName(), 
							packageName == null ? null : packageName.getName(),
							propertyType.isPrimitive());			
		}

		beanClass.setBeanProperties(beanProperties);
		return beanClass;
	}

	public static BeanClass from(JavaClass clazz) {
		BeanClass beanClass = new BeanClass();
		beanClass.setSimpleName(clazz.getName());
		beanClass.setQualifiedName(clazz.getFullyQualifiedName());

		com.thoughtworks.qdox.model.BeanProperty[] originalBeanProperties = clazz.getBeanProperties();
		BeanProperty[] beanProperties = new BeanProperty[originalBeanProperties.length];

		for (int i = 0; i < originalBeanProperties.length; i++) {
			com.thoughtworks.qdox.model.BeanProperty beanProperty = originalBeanProperties[i];
			//@formatter:off
			beanProperties[i] = new BeanProperty(	
								beanProperty.getName(),
								beanProperty.getType().getJavaClass().getName(),
								beanProperty.getMutator() == null ? null : beanProperty.getMutator().getName(),
								beanProperty.getType().getJavaClass().getPackageName(),
								beanProperty.getType().isPrimitive());						
		}
		beanClass.setBeanProperties(beanProperties);
		return beanClass;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public BeanProperty[] getBeanProperties() {
		return beanProperties;
	}

	public void setBeanProperties(BeanProperty[] beanProperties) {
		this.beanProperties = beanProperties;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
}
