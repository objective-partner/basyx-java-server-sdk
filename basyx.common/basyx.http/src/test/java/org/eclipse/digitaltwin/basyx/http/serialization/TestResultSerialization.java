package org.eclipse.digitaltwin.basyx.http.serialization;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.eclipse.digitaltwin.basyx.http.model.Message;
import org.eclipse.digitaltwin.basyx.http.model.Message.MessageTypeEnum;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestResultSerialization {

	@Test
	public void testResultSerialization() throws JsonProcessingException {
		Message message = new Message();
		message.code("500");
		message.correlationId(UUID.randomUUID().toString());
		message.messageType(MessageTypeEnum.EXCEPTION);
		message.setText("Should not happen");
		message.setTimestamp(OffsetDateTime.now().toString());
		Result result = new Result();
		result.addMessagesItem(message);

		ObjectMapper objectMapper = new ObjectMapper();
		System.out.print(objectMapper.writeValueAsString(result));
	}

}
