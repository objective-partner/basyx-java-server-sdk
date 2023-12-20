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

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Indicates that the requested file does not exist
 * 
 * @author danish, Al-Agtash
 *
 */
@SuppressWarnings("serial")
public class FileDoesNotExistException extends BaSyxResponseException {

	public FileDoesNotExistException() {
	}

	public FileDoesNotExistException(int httpStatusCode, String reason, String correlationId, String timestamp) {
		super(httpStatusCode, reason, correlationId, timestamp);
	}

	@Component
	public static class Builder extends BaSyxResponseException.Builder<Builder> {

		public Builder(ITraceableMessageSerializer serializer) {
			super(serializer);
			messageTemplate(new DefaultReference.Builder().keys(Arrays.asList( //
					new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value("https://basyx.objective-partner.com/enterprise/errormessages/v1/r0").build(), //
					new DefaultKey.Builder().type(KeyTypes.MULTI_LANGUAGE_PROPERTY).value("FileDoesNotExistException").build()) //
			).type(ReferenceTypes.MODEL_REFERENCE).build());
			returnCode(406);
			technicalMessageTemplate("Requested File inside the Asset administration shell '{ShellIdentifier}' / element path '{ElementPath}' does not exist");
		}

		public Builder shellIdentifier(String value) {
			param("ShellIdentifier", value);
			return this;
		}
		
		public Builder elementPath(String value) {
			param("ElementPath", value);
			return this;
		}

		public FileDoesNotExistException build() {
			return new FileDoesNotExistException(getReturnCode(), composeMessage(), getCorrelationId(), getTimestamp());
		}
	}
}
