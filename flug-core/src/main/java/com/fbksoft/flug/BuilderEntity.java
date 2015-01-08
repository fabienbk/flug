package com.fbksoft.flug;

public class BuilderEntity {

	public Class<?> domainClass;
	public String parentBuilder;

	public BuilderEntity(Class<?> clazz, String parentBuilder) {
		super();
		this.domainClass = clazz;
		this.parentBuilder = parentBuilder;
	}

}