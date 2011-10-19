package com.springsense.wikiindex;

import org.json.JSONString;

public class EmbeddedAlienJSON implements JSONString {

	private final String alienJSON;
	
	public EmbeddedAlienJSON(String alienJSON) {
		super();
		this.alienJSON = alienJSON;
	}

	@Override
	public String toJSONString() {
		return alienJSON;
	}

}
