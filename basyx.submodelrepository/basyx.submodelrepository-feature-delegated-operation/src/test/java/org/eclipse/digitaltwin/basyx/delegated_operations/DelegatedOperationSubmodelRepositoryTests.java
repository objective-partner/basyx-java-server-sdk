package org.eclipse.digitaltwin.basyx.delegated_operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultExtension;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.delegated_operations.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.http.TraceableMessageSerializer;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(MockitoJUnitRunner.class)
public class DelegatedOperationSubmodelRepositoryTests {

	@Mock
	private AttributeMapper attributeMapper;

	@Mock
	private SubmodelRepository submodelRepository;

	@Mock
	private SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;

	private static ObjectMapper objectMapper;

	@BeforeClass
	public static void setup() {
		objectMapper = new ObjectMapper();

		prepareExceptionBuilderFactory();
	}

	@Before
	public void setupTest() {
		Logger logger = (Logger) LoggerFactory.getLogger(DelegatedOperationSubmodelRepository.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);
	}

	@Test
	public void testInvokableOperationDelegation() {
		DelegatedOperationSubmodelRepository testee = new DelegatedOperationSubmodelRepository(submodelRepository, submodelRepositoryRegistryLink, attributeMapper, objectMapper);

		String submodelId = "submodelId";
		String idShortPath = "idShortPath";
		OperationVariable[] input = new OperationVariable[0];

		InvokableOperation invokableOperation = mock(InvokableOperation.class);
		when(submodelRepository.getSubmodelElement(submodelId, idShortPath)).thenReturn(invokableOperation);

		testee.invokeOperation(submodelId, idShortPath, input);

		verify(invokableOperation).invoke(input);
	}

	@Test
	public void testGetDelegatedSubmodelId() throws NotInvokableException {
		// Arrange
		String expectedDelegatedShellId = "delegatedShellId";
		String expectedDelegatedSubmodelId = "delegatedSubmodelId";
		String expectedDelegatedOperationIdShort = "delegatedOperationIdShort";
		List<Key> keys = Arrays.asList(new DefaultKey.Builder().type(KeyTypes.ASSET_ADMINISTRATION_SHELL).value(expectedDelegatedShellId).build(), new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(expectedDelegatedSubmodelId).build(),
				new DefaultKey.Builder().type(KeyTypes.OPERATION).value(expectedDelegatedOperationIdShort).build());
		Reference reference = new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE).keys(keys).build();
		Extension extension = new DefaultExtension.Builder().name(DelegatedOperationSubmodelRepository.DELEGATED_OPERATION).refersTo(reference).build();
		Operation operation = new DefaultOperation.Builder().idShort("operationIdShort").extensions(Collections.singletonList(extension)).build();

		DelegatedOperationSubmodelRepository testee = new DelegatedOperationSubmodelRepository(null, null, null, null);

		// Act
		String actualDelegatedSubmodelId = testee.getDelegatedSubmodelId("submodelId", "operationIdShort", operation);

		// Assert
		assertEquals(expectedDelegatedSubmodelId, actualDelegatedSubmodelId);
	}

	@Test
	public void testSelectSubmodelEndpoint() {
		SubmodelRepositoryRegistryLink link = new SubmodelRepositoryRegistryLink(null, "http://localhost/submodels");

		// Arrange
		String expectedHref = "http://example.com/submodel";

		DelegatedOperationSubmodelRepository repository = new DelegatedOperationSubmodelRepository(null, link, null, null);

		SubmodelDescriptor submodelDescriptor = composeDescriptor(expectedHref, "SUBMODEL-3.0");
		Endpoint actualEndpoint = repository.selectSubmodelEndpoint("submodelId", "idShortPath", submodelDescriptor);
		assertEquals(expectedHref, actualEndpoint.getProtocolInformation().getHref());

		submodelDescriptor = composeDescriptor(expectedHref, "SUBMODEL-REPOSITORY-3.0");
		actualEndpoint = repository.selectSubmodelEndpoint("submodelId", "idShortPath", submodelDescriptor);
		assertEquals(expectedHref, actualEndpoint.getProtocolInformation().getHref());

		try {
			submodelDescriptor = composeDescriptor(expectedHref, "SHELL-REPOSITORY-3.0");
			repository.selectSubmodelEndpoint("submodelId", "idShortPath", submodelDescriptor);
			fail("Not valid endpoint type");
		} catch (NotInvokableException e) {
			assertTrue(e.getMessage().contains("is not invokable"));
		}
	}

	private static SubmodelDescriptor composeDescriptor(String expectedHref, String interfaceType) {
		Endpoint endpoint0 = new Endpoint();
		endpoint0.setInterface("DESCRIPTION-3.0");
		endpoint0.setProtocolInformation(new ProtocolInformation().href("http://localhost/description"));

		Endpoint endpoint1 = new Endpoint();
		endpoint1.setInterface(interfaceType);
		endpoint1.setProtocolInformation(new ProtocolInformation().href(expectedHref));

		Endpoint endpoint2 = new Endpoint();
		endpoint2.setInterface(interfaceType);
		endpoint2.setProtocolInformation(new ProtocolInformation().href(expectedHref + "/missmatch..."));

		List<Endpoint> endpoints = Arrays.asList(endpoint0, endpoint1, endpoint2);

		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor();
		submodelDescriptor.setEndpoints(endpoints);
		return submodelDescriptor;
	}

	private static void prepareExceptionBuilderFactory() {
		TraceableMessageSerializer messageSerializer = new TraceableMessageSerializer(objectMapper);
		ExceptionBuilderFactory builderFactory = new ExceptionBuilderFactory(messageSerializer);
		ExceptionBuilderFactory.setInstance(builderFactory);
	}
}
