package org.eclipse.digitaltwin.basyx.http.serialization;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.eclipse.digitaltwin.aas4j.v3.model.Message;
import org.eclipse.digitaltwin.aas4j.v3.model.MessageTypeEnum;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMessage;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResult;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestResultSerialization {

	@Test
	public void testResultSerialization() throws JsonProcessingException {
		Message message = new DefaultMessage.Builder().code("500").correlationId(UUID.randomUUID().toString()).messageType(MessageTypeEnum.EXCEPTION).text("Should not happen").timestamp(OffsetDateTime.now().toString()).build();
		Result result = new DefaultResult.Builder().messages(message).build();

		ObjectMapper objectMapper = new ObjectMapper();
		System.out.print(objectMapper.writeValueAsString(result));
	}

}
