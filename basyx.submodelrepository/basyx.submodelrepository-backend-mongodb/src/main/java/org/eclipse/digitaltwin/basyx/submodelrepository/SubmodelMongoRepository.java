package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.apache.tika.utils.StringUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;

public class SubmodelMongoRepository implements BaSyxCrudRepository<Submodel, String, SubmodelFilterParams> {
	private final SimpleMongoRepository<Submodel, String> repo;
	private final MongoTemplate template;
	private final MappingMongoEntityInformation<Submodel, String> entityInformation;
	private static final String MONGO_ID = "_id";

	public SubmodelMongoRepository(MongoPersistentEntity<Submodel> entity, MongoTemplate template) {
		configureIndexForSubmodelId(template);
		MappingMongoEntityInformation<Submodel, String> mongoEntityInformation = new MappingMongoEntityInformation<>(entity);
		this.entityInformation = mongoEntityInformation;
		this.template = template;
		this.repo = new SimpleMongoRepository<>(mongoEntityInformation, template);

	}

	private void configureIndexForSubmodelId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(MONGO_ID, Direction.ASC);
		mongoTemplate.indexOps(Submodel.class).ensureIndex(idIndex);
	}

	@Override
	public CursorResult<List<Submodel>> findAll(SubmodelFilterParams filterParams) {
		List<AggregationOperation> allAggregations = new LinkedList<>();
		applyFilter(allAggregations, filterParams.getIdShort(), filterParams.getSemanticId(), filterParams.getIds());
		applyPagination(filterParams.getPaginationInfo(), allAggregations);

		if (allAggregations.isEmpty()) {
			List<Submodel> submodels = StreamSupport.stream(findAll().spliterator(), false).toList();
			return new CursorResult<>(null, submodels);
		}

		Aggregation aggregation = Aggregation.newAggregation(allAggregations);
		List<Submodel> results = template.aggregate(aggregation, entityInformation.getCollectionName(), entityInformation.getJavaType()).getMappedResults();

		String cursor = resolveCursor(filterParams.getPaginationInfo(), results, Submodel::getId);
		return new CursorResult<>(cursor, results);
	}

	@Override
	public <S extends Submodel> S save(S entity) {
		return repo.save(entity);
	}

	@Override
	public <S extends Submodel> Iterable<S> saveAll(Iterable<S> entities) {
		return repo.saveAll(entities);
	}

	@Override
	public Optional<Submodel> findById(String s) {
		return repo.findById(s);
	}

	@Override
	public boolean existsById(String s) {
		return repo.existsById(s);
	}

	@Override
	public Iterable<Submodel> findAll() {
		return repo.findAll();
	}

	@Override
	public Iterable<Submodel> findAllById(Iterable<String> strings) {
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
	public void delete(Submodel entity) {
		repo.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends String> strings) {
		repo.deleteAllById(strings);
	}

	@Override
	public void deleteAll(Iterable<? extends Submodel> entities) {
		repo.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		repo.deleteAll();
	}

	private void applyFilter(List<AggregationOperation> allAggregation, String idShort, Reference semanticId, Set<String> submodelIds) {
		final String CUSTOM_SEMANTIC_ID_KEYS = "customSemanticIdKeys";
		final String CUSTOM_SUPPLEMENTAL_ID_KEYS = "customSupplementalIdKeys";
		final String SEMANTIC_ID_KEYS_VALUE = "semanticId.keys.value";
		final String SEMANTIC_ID_KEYS = "semanticId.keys";
		final String SUPPLEMENTAL_SEMANTIC_IDS_KEYS_VALUE = "supplementalSemanticIds.keys.value";
		final String SUPPLEMENTAL_SEMANTIC_IDS_KEYS = "supplementalSemanticIds.keys";
		final String ID_SHORT = "idShort";

		if (!CollectionUtils.isEmpty(submodelIds)) {
			applyRegexMatching(allAggregation, submodelIds);
		}

		if (!StringUtils.isBlank(idShort)) {
			allAggregation.add(Aggregation.match(Criteria.where(ID_SHORT).is(idShort)));
		}

		List<String> keys = new ArrayList<>();
		if (semanticId != null) {
			if (!CollectionUtils.isEmpty(semanticId.getKeys())) {
				keys = semanticId.getKeys().stream().map(Key::getValue).toList();
			}
			allAggregation.add(Aggregation.match(new Criteria().orOperator(Criteria.where(SEMANTIC_ID_KEYS).size(keys.size()), Criteria.where(SUPPLEMENTAL_SEMANTIC_IDS_KEYS).size(keys.size()))));
			allAggregation.add(AddFieldsOperation.addField(CUSTOM_SEMANTIC_ID_KEYS).withValue("$" + SEMANTIC_ID_KEYS_VALUE).addField(CUSTOM_SUPPLEMENTAL_ID_KEYS).withValue("$" + SUPPLEMENTAL_SEMANTIC_IDS_KEYS_VALUE).build());
			Criteria criteria = new Criteria().orOperator(Criteria.where(CUSTOM_SEMANTIC_ID_KEYS).is(keys), Criteria.where(CUSTOM_SUPPLEMENTAL_ID_KEYS).elemMatch(Criteria.where("$eq").is(keys)));
			allAggregation.add(Aggregation.match(criteria));
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

		applySorting(allAggregations); // only sort for pagination

		if (paginationInfo.hasCursor()) {
			allAggregations.add(Aggregation.match(Criteria.where(MONGO_ID).gt(paginationInfo.getCursor())));
		}
		if (paginationInfo.hasLimit()) {
			allAggregations.add(Aggregation.limit(paginationInfo.getLimit()));
		}
	}

	private void applySorting(List<AggregationOperation> allAggregations) {
		SortOperation sortOp = Aggregation.sort(Direction.ASC, MONGO_ID);
		allAggregations.add(sortOp);
	}

	private <T> String resolveCursor(PaginationInfo pRequest, List<T> foundDescriptors, Function<T, String> idResolver) {
		if (pRequest == null || foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		T last = foundDescriptors.get(foundDescriptors.size() - 1);
		return idResolver.apply(last);
	}
}
