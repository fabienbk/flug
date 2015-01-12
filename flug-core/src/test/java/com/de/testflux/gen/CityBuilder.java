package com.de.testflux.gen;

import com.de.testflux.City;

public class CityBuilder<P> {

	private String name;

	P _parent;

	public CityBuilder(P _parent) {
		this._parent = _parent;
	}

	public CityBuilder<P> name(java.lang.String name) {
		this.name = name;
		return this;
	}



	public P end() {
		return _parent;
	}

	public City build() {
		City instance = new City();
		instance.setName(name);
					
		return instance;
	}	
}