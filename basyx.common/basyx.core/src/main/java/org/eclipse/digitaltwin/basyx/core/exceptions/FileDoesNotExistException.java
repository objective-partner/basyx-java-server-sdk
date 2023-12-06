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

package org.eclipse.digitaltwin.basyx.core.exceptions;

import java.util.UUID;import org.springframework.http.HttpStatus; /**
 * Indicates that the requested file does not exist
 * 
 * @author danish
 *
 */
@SuppressWarnings("serial")
public class FileDoesNotExistException extends BaSyxResponseException {

	public FileDoesNotExistException() {
		super(HttpStatus.NOT_FOUND, "Requested File inside File Element does not exist", UUID.randomUUID().toString());
	}
	public FileDoesNotExistException(String elementId) {
		super(HttpStatus.NOT_FOUND, getMsg(elementId), UUID.randomUUID().toString());
	}

  public FileDoesNotExistException(String elementId, String correlationId) {
		super(HttpStatus.NOT_FOUND, getMsg(elementId), correlationId);
	}

	private static String getMsg(String elementId) {
		return "Requested File inside File SubmodelElement with ID : " + elementId + " does not exist";
	}

}
