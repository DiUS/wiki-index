package com.springsense.wikiindex;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class EmbeddedAlienJSONTest {

	@Test
	public void shouldUseDirectAlienJSONinStream() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("valueIsFullyFormedJson", new EmbeddedAlienJSON("{\"key\":\"value\"}"));
		assertThat(obj.toString(), equalTo("{\"valueIsFullyFormedJson\":{\"key\":\"value\"}}"));
	}
}
