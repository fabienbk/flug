package com.de.testflux.gen;

import com.de.testflux.Address;

public class AddressBuilder<P> {

	private CityBuilder<AddressBuilder<P>> CityBuilder;
	private String String;
	private String String;
	private int int;

	P _parent;

	public AddressBuilder(P _parent) {
		this._parent = _parent;
	}

	public CityBuilder< AddressBuilder<P> > City() {
		this.CityBuilder = new CityBuilder< AddressBuilder<P> >(this);
		return this.CityBuilder;
	}

	public AddressBuilder<P> String(java.lang.String String) {
		this.String = String;
		return this;
	}

	public AddressBuilder<P> String(java.lang.String String) {
		this.String = String;
		return this;
	}

	public AddressBuilder<P> int(int int) {
		this.int = int;
		return this;
	}



	public P end() {
		return _parent;
	}

	public Address build() {
		Address instance = new Address();
		instance.setCity(CityBuilder == null ? null : CityBuilder.build());
			instance.setLine1(String);
			instance.setLine2(String);
			instance.setNumber(int);
					
		return instance;
	}	
}