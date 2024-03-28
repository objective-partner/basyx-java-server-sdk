package org.eclipse.digitaltwin.basyx.submodelrepository;

import jakarta.annotation.Nullable;

import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.FilterParams;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Submodel filter parameters.
 * 
 * @author Oweis Al-Agtash
 */
public class SubmodelFilterParams extends FilterParams {

	public SubmodelFilterParams() {
	}

	public SubmodelFilterParams(@Nullable String idShort, @Nullable PaginationInfo paginationInfo, @Nullable Set<String> ids, @Nullable Reference semanticId) {
		super(idShort, paginationInfo, ids);
		this.semanticId = semanticId;
	}

	@Nullable
	private Reference semanticId;

	@Nullable
	public Reference getSemanticId() {
		return semanticId;
	}

	public void setSemanticId(@Nullable Reference semanticId) {
		this.semanticId = semanticId;
	}
}
