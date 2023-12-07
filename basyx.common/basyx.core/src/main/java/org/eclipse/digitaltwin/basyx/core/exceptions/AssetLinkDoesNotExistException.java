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
 * Indicates that the requested Asset link does not exist
 * 
 * @author danish, Al-Agtash
 *
 */
@SuppressWarnings("serial")
public class AssetLinkDoesNotExistException extends BaSyxResponseException {

  public AssetLinkDoesNotExistException() {
		super(404, "Asset link does not exist", UUID.randomUUID().toString());
  }

	public AssetLinkDoesNotExistException(String shellIdentifier) {
		super(404, getMessage(shellIdentifier), UUID.randomUUID().toString());
	}
	public AssetLinkDoesNotExistException(String shellIdentifier, String correlationId) {
		super(404, getMessage(shellIdentifier), correlationId);
	}

	private static String getMessage(String shellIdentifier) {
		return "Asset link for shell Id " + shellIdentifier + " does not exist";
	}
}
