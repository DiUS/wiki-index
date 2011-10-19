package com.springsense.wikiindex;

import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONString;

import com.springsense.disambig.SentenceDisambiguationResult;

public class EmbeddedDisambiguationResult implements JSONString {

	private final SentenceDisambiguationResult[] disambiguationResult;

	public EmbeddedDisambiguationResult(SentenceDisambiguationResult[] result) {
		super();
		this.disambiguationResult = result;
	}

	@Override
	public String toJSONString() {
		try {
			if (disambiguationResult == null) {
				return null;
			}

			StringWriter writer = new StringWriter();

			JsonGenerator json = factory.createJsonGenerator(writer);
			json.writeStartArray();
			for (int i = 0; i < disambiguationResult.length; i++) {
				disambiguationResult[i].toJson(json, false);
			}
			json.writeEndArray();
			json.flush();

			String jsonString = writer.toString();
			json.close();

			return jsonString;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't serialise to JSON due to an error", e);
		}
	}

	private static final JsonFactory factory = new JsonFactory();
}
