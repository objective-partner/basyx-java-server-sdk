package org.eclipse.digitaltwin.basyx.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.ITraceableMessageSerializer;
import org.eclipse.digitaltwin.basyx.core.exceptions.TraceableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceableMessageSerializer implements ITraceableMessageSerializer {

  private static final Logger log = LoggerFactory.getLogger(TraceableMessageSerializer.class);
  private final ObjectMapper objectMapper;

  public TraceableMessageSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String serialize(TraceableMessage message, String correlationId) {
    try {
      return objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      log.error("Error happened while preparing TraceableMessage={}", message, e);
      throw ExceptionBuilderFactory.getInstance().baSyxResponseException().correlationId(correlationId).build();
    }
  }
}
