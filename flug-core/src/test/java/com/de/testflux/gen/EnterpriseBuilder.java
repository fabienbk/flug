package com.de.testflux.gen;

import com.de.testflux.Enterprise;

public class EnterpriseBuilder {

	private AddressBuilder<EnterpriseBuilder> AddressBuilder;
	private String String;


	public EnterpriseBuilder() {
	}

	public AddressBuilder< EnterpriseBuilder > Address() {
		this.AddressBuilder = new AddressBuilder< EnterpriseBuilder >(this);
		return this.AddressBuilder;
	}

	public EnterpriseBuilder String(java.lang.String String) {
		this.String = String;
		return this;
	}



	public Enterprise build() {
		Enterprise instance = new Enterprise();
		instance.setAddress(AddressBuilder == null ? null : AddressBuilder.build());
			instance.setName(String);
					
		return instance;
	}	
}