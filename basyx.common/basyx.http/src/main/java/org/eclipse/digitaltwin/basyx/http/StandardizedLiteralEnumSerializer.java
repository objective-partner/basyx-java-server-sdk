package org.eclipse.digitaltwin.basyx.http;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StandardizedLiteralEnumSerializer<T extends StandardizedLiteralEnum> extends JsonSerializer<T> {

	@Override
	public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(value.getValue());
	}
}
