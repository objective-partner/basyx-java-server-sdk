package org.eclipse.digitaltwin.basyx.core.exceptions;

public interface ITraceableMessageSerializer {

	String serialize(TraceableMessage message, String correlationId);
}
