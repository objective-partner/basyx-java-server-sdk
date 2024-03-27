package org.eclipse.digitaltwin.basyx.core.exceptions;

import org.eclipse.digitaltwin.basyx.core.exceptions.BaSyxResponseException.BaSyxResponseExceptionBuilder;

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

	public static void setInstance(ExceptionBuilderFactory instance) {
		ExceptionBuilderFactory.instance = instance;
	}

	public ITraceableMessageSerializer getSerializer() {
		return serializer;
	}

	public AssetLinkDoesNotExistException.Builder assetLinkDoesNotExistException() {
		return new AssetLinkDoesNotExistException.Builder(serializer);
	}

	public BaSyxResponseExceptionBuilder<?> baSyxResponseException() {
		return new BaSyxResponseExceptionBuilder<>(serializer);
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

	public InvocationFailedException.Builder invocationFailedException() {
		return new InvocationFailedException.Builder(serializer);
	}

	public InvalidIdShortPathElementsException.Builder invalidIdShortPathElementsException() {
		return new InvalidIdShortPathElementsException.Builder(serializer);
	}

	public RepositoryRegistryLinkException.Builder repositoryRegistryLinkException() {
		return new RepositoryRegistryLinkException.Builder(serializer);
	}

	public RepositoryRegistryUnlinkException.Builder repositoryRegistryUnlinkException() {
		return new RepositoryRegistryUnlinkException.Builder(serializer);
	}

	public MissingIdentifierException.Builder missingIdentifierException() {
		return new MissingIdentifierException.Builder(serializer);
	}

	public MissingAuthorizationConfigurationException.Builder missingAuthorizationConfigurationException() {
		return new MissingAuthorizationConfigurationException.Builder(serializer);
	}

	public InsufficientPermissionException.Builder insufficientPermissionException() {
		return new InsufficientPermissionException.Builder(serializer);
	}

	public NullSubjectException.Builder nullSubjectException() {
		return new NullSubjectException.Builder(serializer);
	}

	public OperationDelegationException.Builder operationDelegationException() {
		return new OperationDelegationException.Builder(serializer);
	}
	public CollidingIdShortException.Builder collidingIdShortException() {
		return new CollidingIdShortException.Builder(serializer);
	}
	public IdShortNotAllowedException.Builder idShortNotAllowedException() {
		return new IdShortNotAllowedException.Builder(serializer);
	}
}
