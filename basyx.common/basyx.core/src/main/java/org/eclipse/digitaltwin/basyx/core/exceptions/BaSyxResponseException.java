package org.eclipse.digitaltwin.basyx.core.exceptions;
import java.time.OffsetDateTime;
import java.util.UUID;import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;import org.springframework.web.server.ResponseStatusException;

public class BaSyxResponseException extends ResponseStatusException {

	private final String correlationId;
	private final String timestamp;

  public BaSyxResponseException(HttpStatus httpStatus, String reason, String correlationId) {
    super(httpStatus, reason);
    if (correlationId == null || correlationId.isBlank()){
      correlationId = UUID.randomUUID().toString();
    }
    this.correlationId = correlationId;
    this.timestamp = OffsetDateTime.now().toString();
  }

  public BaSyxResponseException(HttpStatus httpStatus, String reason) {
    super(httpStatus, reason);
    this.correlationId = UUID.randomUUID().toString();
    this.timestamp = OffsetDateTime.now().toString();
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getTimestamp() {
    return timestamp;
  }
}
