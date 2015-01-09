package com.fbksoft.flug.model;

public class BeanProperty {

	private String classSimpleName;
	private String setterName;
	private String packageName;
	private boolean primitive;
	private String name;

	public BeanProperty(String name, String classSimpleName, String setterName, String packageName, boolean primitive) {
		super();
		this.name = name;
		this.classSimpleName = classSimpleName;
		this.setterName = setterName;
		this.packageName = packageName;
		this.primitive = primitive;
	}

	public String getClassSimpleName() {
		return classSimpleName;
	}

	public String getSetterName() {
		return setterName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getQualifiedName() {
		return primitive ? classSimpleName : packageName + "." + classSimpleName;
	}

	public String getName() {
		return name;
	}

}
