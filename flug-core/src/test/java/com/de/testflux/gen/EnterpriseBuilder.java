package com.de.testflux.gen;

import com.de.testflux.Enterprise;

public class EnterpriseBuilder {

	private AddressBuilder<EnterpriseBuilder> addressBuilder;
	private String name;
	private java.util.List<DepartmentBuilder<EnterpriseBuilder>> departmentListBuilder;


	public EnterpriseBuilder() {
	}

	public AddressBuilder< EnterpriseBuilder > address() {
		this.addressBuilder = new AddressBuilder< EnterpriseBuilder >(this);
		return this.addressBuilder;
	}

	public EnterpriseBuilder name(java.lang.String name) {
		this.name = name;
		return this;
	}

	public DepartmentBuilder<EnterpriseBuilder> departmentList() {
		if (this.departmentListBuilder == null) {
			this.departmentListBuilder = new java.util.ArrayList<DepartmentBuilder<EnterpriseBuilder>>();
		} 
		DepartmentBuilder<EnterpriseBuilder> val = new DepartmentBuilder<EnterpriseBuilder>(this);
		this.departmentListBuilder.add(val);
		return val;
	}



	public Enterprise build() {
		Enterprise instance = new Enterprise();
		instance.setAddress(addressBuilder == null ? null : addressBuilder.build());	instance.setName(name);	instance.setDepartmentList(departmentListBuilder == null ? null : departmentListBuilder.stream().map(p -> p.build()).collect(java.util.stream.Collectors.toList()));		
		return instance;
	}	
}