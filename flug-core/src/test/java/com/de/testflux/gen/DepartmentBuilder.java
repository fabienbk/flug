package com.de.testflux.gen;

import com.de.testflux.Department;

public class DepartmentBuilder<P> {

	private String name;

	P _parent;

	public DepartmentBuilder(P _parent) {
		this._parent = _parent;
	}

	public DepartmentBuilder<P> name(java.lang.String name) {
		this.name = name;
		return this;
	}



	public P end() {
		return _parent;
	}

	public Department build() {
		Department instance = new Department();
		instance.setName(name);			
		return instance;
	}	
}