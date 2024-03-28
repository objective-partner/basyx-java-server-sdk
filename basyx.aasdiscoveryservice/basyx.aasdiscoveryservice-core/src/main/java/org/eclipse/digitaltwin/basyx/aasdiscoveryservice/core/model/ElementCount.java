package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model;

public class ElementCount {

	private String element;

	private Long count;

	protected ElementCount() {
	}

	public ElementCount(String element, Long count) {
		this.element = element;
		this.count = count;
	}

	public String getElement() {
		return element;
	}

	public Long getCount() {
		return count;
	}
}
