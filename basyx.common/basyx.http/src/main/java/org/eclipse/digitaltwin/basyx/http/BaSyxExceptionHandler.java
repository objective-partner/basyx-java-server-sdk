/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http;

import java.util.UUID;

import org.eclipse.digitaltwin.basyx.core.exceptions.BaSyxResponseException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.http.model.Message;
import org.eclipse.digitaltwin.basyx.http.model.Message.MessageTypeEnum;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures overall Exception to HTTP status code mapping
 *
 * @author schnicke
 */

@ControllerAdvice
public class BaSyxExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(BaSyxExceptionHandler.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
		String correlationId = UUID.randomUUID().toString();
		logger.debug("[{}] {}", correlationId, exception.getMessage(), exception);
		String resultJson = deriveResultFromException(exception, HttpStatus.BAD_REQUEST, correlationId);
		return new ResponseEntity<>(resultJson, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BaSyxResponseException.class)
	public ResponseEntity<String> handleBaSyxResponseException(BaSyxResponseException exception) {
		logger.warn("[{}] {}", exception.getCorrelationId(), exception.getMessage(), exception);
		HttpStatus httpStatus = HttpStatus.valueOf(exception.getHttpStatusCode());
		String resultJson = deriveResultFromException(exception);
		return new ResponseEntity<>(resultJson, httpStatus);
	}

	private String deriveResultFromException(Exception exception, HttpStatus statusCode, String correlationId) {
		BaSyxResponseException responseException = ExceptionBuilderFactory.getInstance().baSyxResponseException().technicalMessageTemplate(exception.getMessage()).returnCode(statusCode.value()).correlationId(correlationId).build();

		return deriveResultFromException(responseException);
	}

	private String deriveResultFromException(BaSyxResponseException exception) {
		Message message = new Message();
		message.code(String.valueOf(exception.getHttpStatusCode()));
		message.correlationId(exception.getCorrelationId());
		message.setText(exception.getMessage());
		message.setTimestamp(exception.getTimestamp());
		message.messageType(MessageTypeEnum.EXCEPTION);

		Result result = new Result();
		result.addMessagesItem(message);
		return tryMarshalResult(exception, result);
	}

	private String tryMarshalResult(Exception exception, Result result) {
		try {
			return objectMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			String reason = "Failed to marshal result object, while handling exception in cause";
			logger.warn(reason, exception);
			throw new RuntimeException(reason, exception);
		}
	}

	@ExceptionHandler(OperationDelegationException.class)
	public <T> ResponseEntity<T> handleNullSubjectException(OperationDelegationException exception) {
		return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
	}
}
