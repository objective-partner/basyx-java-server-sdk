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

import java.util.UUID;

/**
 * Indicates that the requested element does not exist
 * 
 * @author schnicke, Al-Agtash
 *
 */
@SuppressWarnings("serial")
public class ElementDoesNotExistException extends BaSyxResponseException {

	public ElementDoesNotExistException() {
    super(404, "Element does not exist", UUID.randomUUID().toString());
	}

	public ElementDoesNotExistException(String elementId) {
    super(404, getMessage(elementId), UUID.randomUUID().toString());
	}

	public ElementDoesNotExistException(String elementId, String correlationId) {
    super(404, getMessage(elementId), correlationId);
  }
	private static String getMessage(String elementId) {
		return "Element with Id " + elementId + " does not exist";
	}
}
