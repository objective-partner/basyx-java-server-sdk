package org.eclipse.digitaltwin.basyx.core.exceptions;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.springframework.util.StringUtils;

/**
 * General BaSyx exception to be extended by other specific exceptions. Indicates something went wrong in BaSyx Apps.
 *
 * @author Al-Agtash
 */
@SuppressWarnings("serial")
public class BaSyxResponseException extends RuntimeException {

  private final String correlationId;
  private final int httpStatusCode;
  private final String timestamp;

  protected BaSyxResponseException(int httpStatusCode, String reason, String correlationId, String timestamp) {
    super(reason);
    this.correlationId = correlationId;
    this.httpStatusCode = httpStatusCode;
    this.timestamp = timestamp;
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

  public static class BaSyxResponseExceptionBuilder<T extends BaSyxResponseExceptionBuilder<T>> implements
      ITraceableExceptionBuilder {

    private final ITraceableMessageSerializer serializer;
    private String correlationId;
    private String technicalMessageTemplate;
    private Reference messageReference;
    private Integer returnCode;
    private String timestamp;
    private String composedTechnicalMessage = null;

    private final Map<String, Object> params = new HashMap<>();

    public BaSyxResponseExceptionBuilder(ITraceableMessageSerializer serializer) {
      this.serializer = serializer;
      returnCode(500);
      messageReference("BaSyxResponseException");
      technicalMessageTemplate("Something went wrong.");
    }

    @SuppressWarnings("unchecked")
    public T correlationId(String correlationId) {
      this.correlationId = correlationId;
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T technicalMessageTemplate(String technicalMessageTemplate) {
      this.technicalMessageTemplate = technicalMessageTemplate;
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T messageReference(Reference messageReference) {
      this.messageReference = messageReference;
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    public T messageReference(String messageIdShort) {
      this.messageReference = new DefaultReference.Builder().keys(Arrays.asList( //
          new DefaultKey.Builder().type(KeyTypes.SUBMODEL)
              .value("https://basyx.objective-partner.com/enterprise/errormessages/v1/r0").build(), //
          new DefaultKey.Builder().type(KeyTypes.MULTI_LANGUAGE_PROPERTY).value(messageIdShort)
              .build())
      ).type(ReferenceTypes.MODEL_REFERENCE).build();
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T returnCode(int returnCode) {
      this.returnCode = returnCode;
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T param(String key, Object value) {
      params.put(key, value);
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    public T params(Map<String, Object> newParams) {
      params.putAll(newParams);
      this.composedTechnicalMessage = null;
      return (T) this;
    }

    public String getCorrelationId() {
      if (correlationId == null) {
        correlationId = UUID.randomUUID().toString();
      }
      return correlationId;
    }

    protected int getReturnCode() {
      if (returnCode == null) {
        returnCode = 500;
      }
      return returnCode;
    }

    protected String getTimestamp() {
      if (timestamp == null) {
        timestamp = OffsetDateTime.now().toString();
      }
      return timestamp;
    }

    public String composeTechnicalMessage() {
      if (composedTechnicalMessage == null) {
        String technicalMessage = technicalMessageTemplate;
        String tmp = technicalMessageTemplate;
        List<Map.Entry<String, Object>> unusedParams = new LinkedList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
          tmp = tmp.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
          if (!tmp.equals(technicalMessage)) {
            technicalMessage = tmp;
          } else {
            unusedParams.add(entry);
          }
        }
        if (!unusedParams.isEmpty()) {
          technicalMessage += " - unused exception params: ";
          technicalMessage += StringUtils.collectionToCommaDelimitedString(unusedParams);
        }
        composedTechnicalMessage = technicalMessage;
      }
      return composedTechnicalMessage;
    }

    protected String composeMessage() {
      return serializer.serialize(new TraceableMessage(composeTechnicalMessage(), messageReference, params),
          correlationId);
    }

    public BaSyxResponseException build() {
      return new BaSyxResponseException(getReturnCode(), composeMessage(), getCorrelationId(), getTimestamp());
    }

    @Override
    public String getTechnicalMessageTemplate() {
      return technicalMessageTemplate;
    }

    @Override
    public Reference getMessageReference() {
      if (messageReference == null) {
        messageReference = new DefaultReference.Builder().keys(Arrays.asList( //
            new DefaultKey.Builder().type(KeyTypes.SUBMODEL)
                .value("https://basyx.objective-partner.com/enterprise/errormessages/v1/r0").build(), //
            new DefaultKey.Builder().type(KeyTypes.MULTI_LANGUAGE_PROPERTY).value("BaSyxResponseException").build()) //
        ).type(ReferenceTypes.MODEL_REFERENCE).build();
      }
      return messageReference;
    }
  }
}
