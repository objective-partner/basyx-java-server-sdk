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

import org.springframework.stereotype.Component;

/**
 * Indicates that the requested submodel element is not a File SubmodelElement
 *
 * @author danish, Al-Agtash
 */
@SuppressWarnings("serial")
public class ParentElementNotHierarchicalException extends BaSyxResponseException {

	private ParentElementNotHierarchicalException(int httpStatusCode, String reason, String correlationId, String timestamp) {
		super(httpStatusCode, reason, correlationId, timestamp);
	}

	@Component
	public static class Builder extends BaSyxResponseExceptionBuilder<Builder> {

		public Builder(ITraceableMessageSerializer serializer) {
			super(serializer);
			messageReference("ParentElementNotHierarchicalException");
			returnCode(400);
			technicalMessageTemplate("SubmodelElement '{SubmodelElementId}' cannot be added under Path '{ParentIdShortPath}' in Submodel '{SubmodelId}'.");
		}

		public Builder submodelElementId(String value) {
			param("SubmodelElementId", value);
			return this;
		}

		public Builder parentIdShortPath(String value) {
			param("ParentIdShortPath", value);
			return this;
		}

		public Builder submodelId(String value) {
			param("SubmodelId", value);
			return this;
		}

		@Override
		public ParentElementNotHierarchicalException build() {
			return new ParentElementNotHierarchicalException(getReturnCode(), composeMessage(), getCorrelationId(), getTimestamp());
		}
	}
}
