package org.eclipse.digitaltwin.basyx.core.exceptions;

import java.util.Map;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public class TraceableMessage {

  private final String technicalMessage;

  private final Reference messageReference;

  private final Map<String, Object> params;

  public TraceableMessage(String technicalMessage, Reference messageReference, Map<String, Object> params) {
    this.technicalMessage = technicalMessage;
    this.messageReference = messageReference;
    this.params = params;
  }

  public String getTechnicalMessage() {
    return technicalMessage;
  }

  public Reference getMessageReference() {
    return messageReference;
  }

  public Map<String, Object> getParams() {
    return params;
  }
}
