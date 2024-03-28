package org.eclipse.digitaltwin.basyx.core.exceptions;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public interface ITraceableExceptionBuilder {

	String getTechnicalMessageTemplate();

	Reference getMessageReference();

	String getNamespace();
}
