package org.eclipse.digitaltwin.basyx.core.exceptions;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import java.util.Map;

public class TraceableMessage {

  private String technicalMessage;
	
  private Reference messageTemplate;

  private Map<String, Object> params;

  public TraceableMessage(String technicalMessage, Reference messageTemplate, Map<String, Object> params) {
	this.technicalMessage = technicalMessage;
    this.messageTemplate = messageTemplate;
    this.params = params;
  }

  public String getTechnicalMessage() {
	  return technicalMessage;
  }
  
  public Reference getMessageTemplate() {
    return messageTemplate;
  }

  public Map<String, Object> getParams() {
    return params;
  }
}