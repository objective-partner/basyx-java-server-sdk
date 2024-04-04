/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.delegated_operations;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Message;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.core.exceptions.BaSyxResponseException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvocationFailedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.delegated_operations.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelFilterParams;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Decorator for linking {@link SubmodelRepository} with SubmodelRegistry
 *
 * @author danish
 */
public class DelegatedOperationSubmodelRepository implements SubmodelRepository {

	public static final String DELEGATED_OPERATION = "DelegatedOperation";
	private static final Logger logger = LoggerFactory.getLogger(DelegatedOperationSubmodelRepository.class);

	private final SubmodelRepository decorated;
	private final SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;
	private final AttributeMapper attributeMapper;
	private final ObjectMapper objectMapper;

	public DelegatedOperationSubmodelRepository(SubmodelRepository decorated, SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink, AttributeMapper attributeMapper, ObjectMapper objectMapper) {
		this.decorated = decorated;
		this.submodelRepositoryRegistryLink = submodelRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
		this.objectMapper = objectMapper;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(SubmodelFilterParams filterParams) {
		return decorated.getAllSubmodels(filterParams);
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodelsMetadata(SubmodelFilterParams filterParams) {
		return decorated.getAllSubmodelsMetadata(filterParams);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		decorated.createSubmodel(submodel);
	}

	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo paginationInfo) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, paginationInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, submodelElementIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, submodelElementIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement submodelElement) {
		decorated.createSubmodelElement(submodelId, submodelElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException, NotInvokableException {
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);

		if (submodelElement instanceof InvokableOperation operation) {
			return operation.invoke(input);
		}

		String delegatedSubmodelId = getDelegatedSubmodelId(submodelId, idShortPath, submodelElement);

		SubmodelDescriptor delegatedSubmodelDescriptor;
		try {
			delegatedSubmodelDescriptor = submodelRepositoryRegistryLink.getRegistryApi().getSubmodelDescriptorById(delegatedSubmodelId);
		} catch (ApiException e) {
			throw ExceptionBuilderFactory.getInstance().elementDoesNotExistException().missingElement(delegatedSubmodelId).elementType(KeyTypes.SUBMODEL).build();
		}

		Endpoint endpoint = selectSubmodelEndpoint(submodelId, idShortPath, delegatedSubmodelDescriptor);

		HttpPost invokeOperationRequest = composeDelegatedOperationRequest(idShortPath, input, endpoint);

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			BasicHttpClientResponseHandler responseHandler = new BasicHttpClientResponseHandler();
			String response = httpClient.execute(invokeOperationRequest, responseHandler);
			OperationResult operationResult = objectMapper.readValue(response, OperationResult.class);
			return operationResult.getOutputArguments().toArray(new OperationVariable[0]);
		} catch (HttpResponseException e) {
			throw handleUnsuccessfulResponse(e, submodelId, idShortPath, invokeOperationRequest);
		} catch (JsonProcessingException e) {
			throw handleParsingException(e, submodelId, idShortPath, invokeOperationRequest);
		} catch (IOException e) {
			throw handleFailedRequest(e, submodelId, idShortPath, invokeOperationRequest);
		}
	}

	private BaSyxResponseException handleFailedRequest(IOException e, String submodelId, String idShortPath, HttpPost request) {
		logger.warn("Failing calling endpoint {} for Element={} in Submodel={}.", request.getRequestUri(), idShortPath, submodelId, e);

		String technicalMessageTemplate = "Failed to execute API request for invoking Element '{IdShortPath}' in '{SubmodelId}'.";
		return ExceptionBuilderFactory.getInstance().baSyxResponseException().technicalMessageTemplate(technicalMessageTemplate).param("IdShortPath", idShortPath).param("SubmodelId", submodelId).build();
	}

	private InvocationFailedException handleUnsuccessfulResponse(HttpResponseException e, String submodelId, String idShortPath, HttpPost request) {
		try {
			Result errorResult = objectMapper.readValue(e.getReasonPhrase(), Result.class);
			Optional<Message> firstMessage = errorResult.getMessages().stream().findFirst();
			if (firstMessage.isEmpty()) {
				throw ExceptionBuilderFactory.getInstance().invocationFailedException().submodelId(submodelId).idShortPath(idShortPath).build();
			}
			Message message = firstMessage.get();
			String correlationId = message.getCorrelationId();
			logger.warn("[{}] Failed to call Operation '{}' in Submodel '{}' on '{}'", correlationId, idShortPath, submodelId, request.getRequestUri());
			logger.warn("[{}] Received error response: \n {}", correlationId, message.getText());
			return ExceptionBuilderFactory.getInstance().invocationFailedException().correlationId(correlationId).reason(message.getText()).submodelId(submodelId).idShortPath(idShortPath).build();
		} catch (JsonProcessingException ex) {
			throw handleParsingException(ex, submodelId, idShortPath, request);
		}
	}

	private static BaSyxResponseException handleParsingException(JsonProcessingException e, String submodelId, String idShortPath, HttpPost request) {
		logger.error("Failed parsing Error Response after invoking endpoint {} of Element {} in {}.", request.getRequestUri(), idShortPath, submodelId, e);
		String technicalMessageTemplate = "Something went wrong while reading error response received after invoking Element '{IdShortPath}' in '{SubmodelId}'.";
		return ExceptionBuilderFactory.getInstance().baSyxResponseException().technicalMessageTemplate(technicalMessageTemplate).param("IdShortPath", idShortPath).param("SubmodelId", submodelId).build();
	}

	String getDelegatedSubmodelId(String submodelId, String idShortPath, SubmodelElement submodelElement) throws NotInvokableException {
		if (!(submodelElement instanceof Operation operation)) {
			String correlationId = UUID.randomUUID().toString();
			logger.warn("[{}] SubmodelElement '{}' in Submodel '{}' is not an Operation.", correlationId, idShortPath, submodelId);
			throw ExceptionBuilderFactory.getInstance().notInvokableException().submodelId(submodelId).idShortPath(idShortPath).correlationId(correlationId).build();
		}
		Optional<Extension> delegatedOperationExtension = operation.getExtensions().stream().filter(e -> DELEGATED_OPERATION.equalsIgnoreCase(e.getName())).findFirst();
		if (delegatedOperationExtension.isEmpty()) {
			String correlationId = UUID.randomUUID().toString();
			logger.warn("[{}] Operation '{}' in Submodel '{}' has no 'DelegatedOperation' Extension.", correlationId, idShortPath, submodelId);
			throw ExceptionBuilderFactory.getInstance().notInvokableException().submodelId(submodelId).idShortPath(idShortPath).correlationId(correlationId).build();
		}
		List<Reference> refersTo = delegatedOperationExtension.get().getRefersTo();
		if (refersTo == null || refersTo.isEmpty()) {
			String correlationId = UUID.randomUUID().toString();
			logger.warn("[{}] Operation '{}' in Submodel '{}' has no reference in 'DelegatedOperation' Extension.", correlationId, idShortPath, submodelId);
			throw ExceptionBuilderFactory.getInstance().notInvokableException().submodelId(submodelId).idShortPath(idShortPath).correlationId(correlationId).build();
		}

		Optional<Key> submodelKey = refersTo.get(0).getKeys().stream().filter(k -> k.getType() == KeyTypes.SUBMODEL).findFirst();
		if (submodelKey.isEmpty()) {
			String correlationId = UUID.randomUUID().toString();
			logger.warn("[{}] Operation '{}' in Submodel '{}' doesn't reference a Submodel in 'DelegatedOperation' Extension.", correlationId, idShortPath, submodelId);
			throw ExceptionBuilderFactory.getInstance().notInvokableException().submodelId(submodelId).idShortPath(idShortPath).correlationId(correlationId).build();
		}
		return submodelKey.get().getValue();
	}

	Endpoint selectSubmodelEndpoint(String submodelId, String idShortPath, SubmodelDescriptor delegatedSubmodelDescriptor) {
		Pattern p = Pattern.compile("^SUBMODEL(-REPOSITORY)?-3.*");
		Optional<Endpoint> endpoint = delegatedSubmodelDescriptor.getEndpoints().stream()
				// Search for Submodel endpoints...
				.filter(ep -> p.matcher(ep.getInterface()).matches())
				// Exclude endpoints that our base url
				.filter(ep -> !ep.getProtocolInformation().getHref().startsWith(submodelRepositoryRegistryLink.getSubmodelRepositoryBaseURL())).findFirst();
		if (endpoint.isEmpty()) {
			String correlationId = UUID.randomUUID().toString();

			logger.warn("[{}] Operation '{}' in Submodel '{}' delegates to Submodel '{}', but registry holds no SUBMODEL/SUBMODEL-REPOSITORY endpoint.", correlationId, idShortPath, submodelId, delegatedSubmodelDescriptor.getId());

			throw ExceptionBuilderFactory.getInstance().notInvokableException().submodelId(submodelId).idShortPath(idShortPath).correlationId(correlationId).build();
		}
		return endpoint.get();
	}

	HttpPost composeDelegatedOperationRequest(String idShortPath, OperationVariable[] input, Endpoint endpoint) {
		HttpPost invokeOperationRequest = new HttpPost(endpoint.getProtocolInformation().getHref() + "/submodel-elements/" + idShortPath + "/invoke");
		invokeOperationRequest.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		invokeOperationRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null) {
			invokeOperationRequest.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		String requestPayloadJson = null;
		try {
			requestPayloadJson = objectMapper.writeValueAsString(Arrays.asList(input));
		} catch (JsonProcessingException e) {
			// Internal Server Error or Bad Request?
			// input comes directly from the user, so it is not possible that it is
			// malformed?...
			throw new RuntimeException(e);
		}
		StringEntity stringEntity = new StringEntity(requestPayloadJson, StandardCharsets.UTF_8);
		invokeOperationRequest.setEntity(stringEntity);
		return invokeOperationRequest;
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream, String contentType) throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(submodelId, idShortPath, fileName, inputStream, contentType);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(submodelId, idShortPath);
	}
}
