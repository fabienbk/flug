package com.de.testflux.gen;

import com.de.testflux.Address;

public class AddressBuilder<P> {

	private CityBuilder<AddressBuilder<P>> cityBuilder;
	private String line1;
	private String line2;
	private int number;

	P _parent;

	public AddressBuilder(P _parent) {
		this._parent = _parent;
	}

	public CityBuilder< AddressBuilder<P> > cityBuilder() {
		this.cityBuilder = new CityBuilder< AddressBuilder<P> >(this);
		return this.cityBuilder;
	}

	public AddressBuilder<P> line1(java.lang.String line1) {
		this.line1 = line1;
		return this;
	}

	public AddressBuilder<P> line2(java.lang.String line2) {
		this.line2 = line2;
		return this;
	}

	public AddressBuilder<P> number(int number) {
		this.number = number;
		return this;
	}



	public P end() {
		return _parent;
	}

	public Address build() {
		Address instance = new Address();
		instance.setCity(cityBuilder == null ? null : cityBuilder.build());
			instance.setLine1(line1);
			instance.setLine2(line2);
			instance.setNumber(number);
					
		return instance;
	}	
}