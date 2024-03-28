package org.eclipse.digitaltwin.basyx.aasrepository;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.core.FilterParams;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

public class AasFilterParams extends FilterParams {

	public AasFilterParams() {
	}

	public AasFilterParams(@Nullable String idShort, @Nullable PaginationInfo paginationInfo, @Nullable Set<String> ids, @Nullable List<SpecificAssetId> assetIds) {
		super(idShort, paginationInfo, ids);
		this.assetIds = assetIds;
	}

	@Nullable
	private List<SpecificAssetId> assetIds;

	@Nullable
	public List<SpecificAssetId> getAssetIds() {
		return assetIds;
	}

	public void setAssetIds(@Nullable List<SpecificAssetId> assetIds) {
		this.assetIds = assetIds;
	}
}
