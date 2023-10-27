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

import java.time.OffsetDateTime;
import java.util.UUID;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.http.model.Message;
import org.eclipse.digitaltwin.basyx.http.model.Message.MessageTypeEnum;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures overall Exception to HTTP status code mapping
 * 
 * @author schnicke
 *
 */
@ControllerAdvice
public class BaSyxExceptionHandler extends ResponseEntityExceptionHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler(ElementDoesNotExistException.class)
	public ResponseEntity<String> handleElementNotFoundException(ElementDoesNotExistException exception, WebRequest request) {
		String resultJson = deriveResultFromException(exception, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(resultJson, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AssetLinkDoesNotExistException.class)
	public <T> ResponseEntity<T> handleElementNotFoundException(AssetLinkDoesNotExistException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CollidingIdentifierException.class)
	public ResponseEntity<String> handleCollidingIdentifierException(CollidingIdentifierException exception, WebRequest request) {
    String resultJson = deriveResultFromException(exception, HttpStatus.CONFLICT);
		return new ResponseEntity<>(resultJson, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(CollidingAssetLinkException.class)
	public <T> ResponseEntity<T> handleCollidingIdentifierException(CollidingAssetLinkException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
		String resultJson = deriveResultFromException(exception, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(resultJson, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IdentificationMismatchException.class)
	public ResponseEntity<String> handleIdMismatchException(IdentificationMismatchException exception) {
	  String resultJson = deriveResultFromException(exception, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(resultJson, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FeatureNotSupportedException.class)
	public ResponseEntity<String> handleFeatureNotSupportedException(FeatureNotSupportedException exception) {
		String resultJson = deriveResultFromException(exception, HttpStatus.NOT_IMPLEMENTED);
    return new ResponseEntity<>(resultJson, HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(NotInvokableException.class)
	public ResponseEntity<String> handleNotInvokableException(NotInvokableException exception) {
		String resultJson = deriveResultFromException(exception, HttpStatus.METHOD_NOT_ALLOWED);
    return new ResponseEntity<>(resultJson, HttpStatus.METHOD_NOT_ALLOWED);
	}
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<String> handleResponseStatusException(ResponseStatusException exception) {
    HttpStatus httpStatus = HttpStatus.valueOf(exception.getStatusCode().value());
		String resultJson = deriveResultFromException(exception, httpStatus);
    return new ResponseEntity<>(resultJson, httpStatus);
	}

  private String deriveResultFromException(Exception exception, HttpStatus statusCode) {
    Message message = new Message();
    message.code(String.valueOf(statusCode.value()));
    message.correlationId(UUID.randomUUID().toString());
    message.messageType(MessageTypeEnum.EXCEPTION);
    message.setText(exception.getMessage());
    message.setTimestamp(OffsetDateTime.now().toString());

    Result result = new Result();
    result.addMessagesItem(message);
    String resultJson;

    try {
      resultJson = objectMapper.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to marshal result object, while handling exception in cause", exception);
    }
    return resultJson;
  }
}
