package org.eclipse.digitaltwin.basyx.core;

import java.util.List;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.springframework.data.repository.CrudRepository;

/**
 * Extended {@link CrudRepository} with BaSyx specific implementation.
 *
 * @author Oweis Al-Agtash
 */
public interface BaSyxCrudRepository<T, ID, F extends FilterParams> extends CrudRepository<T, ID> {

	/**
	 * Blabla
	 * 
	 * @param filterParams
	 * @return
	 */
	CursorResult<List<T>> findAll(F filterParams);

}
