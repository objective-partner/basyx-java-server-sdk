package org.eclipse.digitaltwin.basyx.core.exceptions;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * General BaSyx exception to be extended by other specific exceptions.
 * Indicates something went wrong in BaSyx Apps.
 *
 * @author Al-Agtash
 *
 */
public class BaSyxResponseException extends RuntimeException {

	private final String correlationId;
	private final int httpStatusCode;
	private final String timestamp;

  protected BaSyxResponseException() {
    this.correlationId = UUID.randomUUID().toString();
    this.httpStatusCode = 500;
    this.timestamp = OffsetDateTime.now().toString();
  }

  public BaSyxResponseException(int httpStatusCode, String reason, String correlationId) {
    super(reason);
    if (correlationId == null || correlationId.isBlank()){
      correlationId = UUID.randomUUID().toString();
    }
    this.correlationId = correlationId;
    this.httpStatusCode = httpStatusCode;
    this.timestamp = OffsetDateTime.now().toString();
  }

  public BaSyxResponseException(int httpStatusCode, String reason) {
    super(reason);
    this.correlationId = UUID.randomUUID().toString();
    this.httpStatusCode = httpStatusCode;
    this.timestamp = OffsetDateTime.now().toString();
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }
  public String getTimestamp() {
    return timestamp;
  }
}
