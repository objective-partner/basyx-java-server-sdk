package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.FilterParams;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class AasMongoRepository implements BaSyxCrudRepository<AssetAdministrationShell, String> {
	private final SimpleMongoRepository<AssetAdministrationShell, String> repo;

	public AasMongoRepository(MongoPersistentEntity<AssetAdministrationShell> entity, MongoTemplate template) {
		this.repo = new SimpleMongoRepository<>(new MappingMongoEntityInformation<>(entity), template);
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> findAll(FilterParams filterParams) {

		return null;
	}

	@Override
	public <S extends AssetAdministrationShell> S save(S entity) {
		return repo.save(entity);
	}

	@Override
	public <S extends AssetAdministrationShell> Iterable<S> saveAll(Iterable<S> entities) {
		return repo.saveAll(entities);
	}

	@Override
	public Optional<AssetAdministrationShell> findById(String s) {
		return repo.findById(s);
	}

	@Override
	public boolean existsById(String s) {
		return repo.existsById(s);
	}

	@Override
	public Iterable<AssetAdministrationShell> findAll() {
		return repo.findAll();
	}

	@Override
	public Iterable<AssetAdministrationShell> findAllById(Iterable<String> strings) {
		return repo.findAllById(strings);
	}

	@Override
	public long count() {
		return repo.count();
	}

	@Override
	public void deleteById(String s) {
		repo.deleteById(s);
	}

	@Override
	public void delete(AssetAdministrationShell entity) {
		repo.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends String> strings) {
		repo.deleteAllById(strings);
	}

	@Override
	public void deleteAll(Iterable<? extends AssetAdministrationShell> entities) {
		repo.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		repo.deleteAll();
	}
}
