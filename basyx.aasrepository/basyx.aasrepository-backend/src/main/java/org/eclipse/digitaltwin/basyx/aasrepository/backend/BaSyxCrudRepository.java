package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.util.List;

import org.eclipse.digitaltwin.basyx.core.FilterParams;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.springframework.data.repository.CrudRepository;

public interface BaSyxCrudRepository<T, ID> extends CrudRepository<T, ID> {

	CursorResult<List<T>> findAll(FilterParams filterParams);

}
