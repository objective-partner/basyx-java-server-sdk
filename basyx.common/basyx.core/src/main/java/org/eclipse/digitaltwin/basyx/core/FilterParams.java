package org.eclipse.digitaltwin.basyx.core;

import jakarta.annotation.Nullable;

import java.util.Set;

import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Filter parameters, typically when searching all object.
 * 
 * @author Oweis Al-Agtash
 */
public class FilterParams {

	public FilterParams() {
	}

	public FilterParams(@Nullable String idShort, @Nullable PaginationInfo paginationInfo, @Nullable Set<String> ids) {
		this.idShort = idShort;
		this.paginationInfo = paginationInfo;
		this.ids = ids;
	}

	@Nullable
	private String idShort;

	@Nullable
	private PaginationInfo paginationInfo;

	@Nullable
	private Set<String> ids;

	@Nullable
	public String getIdShort() {
		return idShort;
	}

	public void setIdShort(@Nullable String idShort) {
		this.idShort = idShort;
	}

	@Nullable
	public PaginationInfo getPaginationInfo() {
		return paginationInfo;
	}

	public void setPaginationInfo(@Nullable PaginationInfo paginationInfo) {
		this.paginationInfo = paginationInfo;
	}

	@Nullable
	public Set<String> getIds() {
		return ids;
	}

	public void setIds(@Nullable Set<String> ids) {
		this.ids = ids;
	}
}
