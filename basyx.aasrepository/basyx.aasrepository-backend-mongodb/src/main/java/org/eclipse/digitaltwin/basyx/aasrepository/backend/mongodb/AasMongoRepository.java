package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilterParams;
import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;

public class AasMongoRepository implements BaSyxCrudRepository<AssetAdministrationShell, String, AasFilterParams> {
	private final SimpleMongoRepository<AssetAdministrationShell, String> repo;
	private final MongoTemplate template;
	private final MappingMongoEntityInformation<AssetAdministrationShell, String> entityInformation;
	private static final String MONGO_ID = "_id";

	public AasMongoRepository(MongoPersistentEntity<AssetAdministrationShell> entity, MongoTemplate template) {
		configureIndexForAasId(template);
		MappingMongoEntityInformation<AssetAdministrationShell, String> mongoEntityInformation = new MappingMongoEntityInformation<>(entity);
		this.entityInformation = mongoEntityInformation;
		this.template = template;
		this.repo = new SimpleMongoRepository<>(mongoEntityInformation, template);
	}

	private void configureIndexForAasId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(MONGO_ID, Direction.ASC);
		mongoTemplate.indexOps(AssetAdministrationShell.class).ensureIndex(idIndex);
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> findAll(AasFilterParams filterParams) {
		List<AggregationOperation> allAggregations = new LinkedList<>();
		applyFilter(allAggregations, filterParams.getIdShort(), filterParams.getIds());
		PaginationInfo paginationInfo = filterParams.getPaginationInfo();
		applyPagination(paginationInfo, allAggregations);

		if (allAggregations.isEmpty()) {
			List<AssetAdministrationShell> allAas = StreamSupport.stream(findAll().spliterator(), false).toList();
			return new CursorResult<>(null, allAas);
		}

		Aggregation aggregation = Aggregation.newAggregation(allAggregations);
		List<AssetAdministrationShell> results = template.aggregate(aggregation, entityInformation.getCollectionName(), entityInformation.getJavaType()).getMappedResults();

		String cursor = resolveCursor(paginationInfo, results, AssetAdministrationShell::getId);
		return new CursorResult<>(cursor, results);
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

	private void applyFilter(List<AggregationOperation> allAggregation, String idShort, Set<String> aasIds) {

		if (!CollectionUtils.isEmpty(aasIds)) {
			applyRegexMatching(allAggregation, aasIds);
		}

		final String ID_SHORT = "idShort";
		if (idShort != null && !idShort.isBlank()) {
			allAggregation.add(Aggregation.match(Criteria.where(ID_SHORT).is(idShort)));
		}
	}

	public void applyRegexMatching(List<AggregationOperation> allAggregation, Set<String> ids) {
		List<Criteria> regexCriteria = new ArrayList<>();

		for (String idPattern : ids) {
			regexCriteria.add(Criteria.where(MONGO_ID).regex(idPattern));
		}

		Criteria combinedCriteria = new Criteria().orOperator(regexCriteria.toArray(new Criteria[0]));
		allAggregation.add(Aggregation.match(combinedCriteria));
	}

	private void applyPagination(PaginationInfo paginationInfo, List<AggregationOperation> allAggregations) {
		if (paginationInfo == null) {
			return;
		}
		if (paginationInfo.hasCursor()) {
			allAggregations.add(Aggregation.match(Criteria.where(MONGO_ID).gt(paginationInfo.getCursor())));
		}
		if (paginationInfo.hasLimit()) {
			allAggregations.add(Aggregation.limit(paginationInfo.getLimit()));
		}
	}

	private <T> String resolveCursor(PaginationInfo pRequest, List<T> foundDescriptors, Function<T, String> idResolver) {
		if (pRequest == null || foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		T last = foundDescriptors.get(foundDescriptors.size() - 1);
		return idResolver.apply(last);
	}
}
