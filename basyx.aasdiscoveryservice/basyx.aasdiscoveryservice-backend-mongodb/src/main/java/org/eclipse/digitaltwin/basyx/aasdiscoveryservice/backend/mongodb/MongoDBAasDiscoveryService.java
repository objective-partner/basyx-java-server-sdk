/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.ElementCount;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.Index;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * MongoDB implementation of the {@link AasDiscoveryService}
 *
 * @author danish
 */
public class MongoDBAasDiscoveryService implements AasDiscoveryService {
	private static final String SHELL_IDENTIFIER = "shellIdentifier";
	private static final String ASSET_LINKS = "assetLinks";
	private final MongoTemplate mongoTemplate;
	private final String collectionName;
	private String aasDiscoveryServiceName;

	public MongoDBAasDiscoveryService(MongoTemplate mongoTemplate, String collectionName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		configureIndexForConceptDescriptionId(mongoTemplate);
	}

	public MongoDBAasDiscoveryService(MongoTemplate mongoTemplate, String collectionName, String aasDiscoveryServiceName) {
		this(mongoTemplate, collectionName);
		this.aasDiscoveryServiceName = aasDiscoveryServiceName;
	}

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where(ASSET_LINKS).all(assetIds));
		ProjectionOperation projectionOperation = Aggregation.project(SHELL_IDENTIFIER);
		GroupOperation groupOperation = Aggregation.group(SHELL_IDENTIFIER);

		Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, collectionName, Document.class);
		List<String> shellIds = results.getMappedResults().stream().map(doc -> doc.get("_id").toString()).collect(Collectors.toList());

		return paginateList(pInfo, shellIds);
	}

	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		AasDiscoveryDocument document = mongoTemplate.findOne(getSingleObjectQuery(shellIdentifier), AasDiscoveryDocument.class, collectionName);

		if (document == null)
			throw ExceptionBuilderFactory.getInstance().assetLinkDoesNotExistException().missingIdentifier(shellIdentifier).build();

		return document.getSpecificAssetIds();
	}

	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds) {
		Query query = getSingleObjectQuery(shellIdentifier);

		if (mongoTemplate.exists(query, AasDiscoveryDocument.class, collectionName))
			throw ExceptionBuilderFactory.getInstance().collidingIdentifierException().collidingIdentifier(shellIdentifier).build();

		Set<AssetLink> assetLinks = new HashSet<>(AasDiscoveryUtils.deriveAssetLinksFromSpecificAssetIds(assetIds));

		AasDiscoveryDocument document = new AasDiscoveryDocument(shellIdentifier, assetLinks, assetIds);

		mongoTemplate.save(document, collectionName);

		return assetIds;
	}

	@Override
	public List<ElementCount> getAssetLinkNames(String prefix, Integer minCount) {
		List<AggregationOperation> aggregationOperations = new ArrayList<>(4);
		aggregationOperations.add(Aggregation.unwind("assetLinks"));
		if (prefix != null && !prefix.isBlank()) {
			aggregationOperations.add(Aggregation.match(Criteria.where("assetLinks.name").regex("^" + prefix)));
		}
		aggregationOperations.add(Aggregation.group("assetLinks.name").count().as("count"));
		aggregationOperations.add( Aggregation.project().andExpression("_id").as("element").andInclude("count"));
		Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
		AggregationResults<ElementCount> results = mongoTemplate.aggregate(aggregation, collectionName, ElementCount.class);
		return results.getMappedResults();
	}

	@Override
	public List<ElementCount> getAssetLinkValues(String assetLinkName, String prefix, Integer minCount) {
		List<AggregationOperation> aggregationOperations = new ArrayList<>(4);
		aggregationOperations.add(Aggregation.unwind("assetLinks"));
		aggregationOperations.add(Aggregation.match(Criteria.where("assetLinks.name").is(assetLinkName)));
		if (prefix != null && !prefix.isBlank()) {
			aggregationOperations.add(Aggregation.match(Criteria.where("assetLinks.value").regex("^" + prefix)));
		}
		aggregationOperations.add(Aggregation.group("assetLinks.value").count().as("count"));
		aggregationOperations.add( Aggregation.project().andExpression("_id").as("element").andInclude("count"));
		Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
		AggregationResults<ElementCount> results = mongoTemplate.aggregate(aggregation, collectionName, ElementCount.class);
		return results.getMappedResults();
	}

	private static Query getSingleObjectQuery(String shellIdentifier) {
		return new Query().addCriteria(Criteria.where(SHELL_IDENTIFIER).is(shellIdentifier));
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		Query query = getSingleObjectQuery(shellIdentifier);

		DeleteResult result = mongoTemplate.remove(query, AssetLink.class, collectionName);

		if (result.getDeletedCount() == 0)
			throw ExceptionBuilderFactory.getInstance().assetLinkDoesNotExistException().missingIdentifier(shellIdentifier).build();
	}

	@Override
	public String getName() {
		return aasDiscoveryServiceName == null ? AasDiscoveryService.super.getName() : aasDiscoveryServiceName;
	}

	private CursorResult<List<String>> paginateList(PaginationInfo pInfo, List<String> shellIdentifiers) {
		TreeMap<String, String> shellIdentifierMap = shellIdentifiers.stream()
				.collect(Collectors.toMap(Function.identity(), Function.identity(), (a, b) -> a, TreeMap::new));

		PaginationSupport<String> paginationSupport = new PaginationSupport<>(shellIdentifierMap, Function.identity());

		return paginationSupport.getPaged(pInfo);
	}

	private boolean containsMatchingAssetId(List<SpecificAssetId> containedSpecificAssetIds, List<String> queryAssetIds) {
		return queryAssetIds.stream()
				.anyMatch(queryAssetId -> containedSpecificAssetIds.stream()
						.anyMatch(containedAssetId -> containedAssetId.getValue()
								.equals(queryAssetId)));
	}

	private void configureIndexForConceptDescriptionId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(SHELL_IDENTIFIER, Direction.ASC);
		mongoTemplate.indexOps(AssetLink.class)
				.ensureIndex(idIndex);
	}

}
