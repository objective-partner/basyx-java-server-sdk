package org.eclipse.digitaltwin.basyx.core.exceptions;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;

public class ExceptionBuilderFactory {

	private static ITraceableMessageSerializer serializer;
	private static ExceptionBuilderFactory instance;

	public ExceptionBuilderFactory(ITraceableMessageSerializer serializer) {
		ExceptionBuilderFactory.serializer = serializer;
		ExceptionBuilderFactory.instance = this;
	}

	public static ExceptionBuilderFactory getInstance() {
		return instance;
	}

	public ITraceableMessageSerializer getSerializer() {
		return serializer;
	}

	public AssetLinkDoesNotExistException.Builder assetLinkDoesNotExistException() {
		return new AssetLinkDoesNotExistException.Builder(serializer);
	}

	public BaSyxResponseException.Builder<?> baSyxResponseException() {
		return new BaSyxResponseException.Builder<>(serializer);
	}

	public CollidingIdentifierException.Builder collidingIdentifierException() {
		return new CollidingIdentifierException.Builder(serializer);
	}

	public ElementDoesNotExistException.Builder elementDoesNotExistException() {
		return new ElementDoesNotExistException.Builder(serializer);
	}
	
	public ElementNotAFileException.Builder elementNotAFileException() {
		return new ElementNotAFileException.Builder(serializer);
	}
	
	public FeatureNotImplementedException.Builder featureNotImplementedException() {
		return new FeatureNotImplementedException.Builder(serializer);
	}
	
	public FeatureNotSupportedException.Builder featureNotSupportedException() {
		return new FeatureNotSupportedException.Builder(serializer);
	}
	
	public FileDoesNotExistException.Builder fileDoesNotExistException() {
		return new FileDoesNotExistException.Builder(serializer);
	}
	
	public FileHandlingException.Builder fileHandlingException() {
		return new FileHandlingException.Builder(serializer);
	}

	public IdentificationMismatchException.Builder identificationMismatchException() {
		return new IdentificationMismatchException.Builder(serializer);
	}
	
	public NotInvokableException.Builder notInvokableException() {
		return new NotInvokableException.Builder(serializer);
	}
}