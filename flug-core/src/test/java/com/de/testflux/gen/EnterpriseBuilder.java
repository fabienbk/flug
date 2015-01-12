package com.de.testflux.gen;

import com.de.testflux.Enterprise;

public class EnterpriseBuilder {

	private AddressBuilder<EnterpriseBuilder> addressBuilder;
	private String name;


	public EnterpriseBuilder() {
	}

	public AddressBuilder< EnterpriseBuilder > addressBuilder() {
		this.addressBuilder = new AddressBuilder< EnterpriseBuilder >(this);
		return this.addressBuilder;
	}

	public EnterpriseBuilder name(java.lang.String name) {
		this.name = name;
		return this;
	}



	public Enterprise build() {
		Enterprise instance = new Enterprise();
		instance.setAddress(addressBuilder == null ? null : addressBuilder.build());
			instance.setName(name);
					
		return instance;
	}	
}