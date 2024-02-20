package org.eclipse.digitaltwin.basyx.core.exceptions;

import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public class TraceableMessage {

	private String technicalMessage;

	private Reference messageReference;

	private Map<String, Object> params;

	public TraceableMessage() {
		// for object mapper
	}

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
