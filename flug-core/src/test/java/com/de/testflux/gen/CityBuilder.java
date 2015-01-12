package com.de.testflux.gen;

import com.de.testflux.City;

public class CityBuilder<P> {

	private String String;

	P _parent;

	public CityBuilder(P _parent) {
		this._parent = _parent;
	}

	public CityBuilder<P> String(java.lang.String String) {
		this.String = String;
		return this;
	}



	public P end() {
		return _parent;
	}

	public City build() {
		City instance = new City();
		instance.setName(String);
					
		return instance;
	}	
}