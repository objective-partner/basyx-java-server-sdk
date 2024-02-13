/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.ElementCount;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

import static org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils.deriveAssetLinksFromSpecificAssetIds;
import static org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils.deriveShellFromIdAndSpecificAssetIds;

/**
 * In-memory implementation of the {@link AasDiscoveryService}
 *
 * @author zhangzai
 *
 */
public class InMemoryAasDiscoveryService implements AasDiscoveryService {

	private String aasDiscoveryServiceName;

	private final Map<String, Set<AssetLink>> assetLinks = new LinkedHashMap<>();
	private final Map<String, List<SpecificAssetId>> assetIds = new LinkedHashMap<>();

	/**
	 * Creates the {@link InMemoryAasDiscoveryService}
	 * 
	 */
	public InMemoryAasDiscoveryService() {
	}

	/**
	 * Creates the {@link InMemoryAasDiscoveryService}
	 * 
	 * @param aasDiscoveryServiceName
	 *            of the Aas Discovery Service
	 */
	public InMemoryAasDiscoveryService(String aasDiscoveryServiceName) {
		this.aasDiscoveryServiceName = aasDiscoveryServiceName;
	}

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		Set<String> shellIds = getShellIdsWithAssetLinks(assetIds);

		return paginateList(pInfo, new ArrayList<>(shellIds));
	}

	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		throwIfAssetLinkDoesNotExist(shellIdentifier);

		return assetIds.get(shellIdentifier);
	}

	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> specificAssetIds) {
		synchronized (assetLinks) {
			throwIfAssetLinkExists(shellIdentifier);

			List<AssetLink> shellAssetLinks = deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
			assetLinks.put(shellIdentifier, new HashSet<>(shellAssetLinks));
			assetIds.put(shellIdentifier, specificAssetIds);
		}

		return specificAssetIds;
	}

	@Override
	public List<ElementCount> getAssetLinkNames(String prefix, Integer minCount) {
		Stream<Map.Entry<String, Long>> assetNamesCounted = assetLinks.values().stream().flatMap(Set::stream).map(AssetLink::getName).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream();
		if (prefix != null && !prefix.isBlank()) {
			assetNamesCounted = assetNamesCounted.filter(e -> e.getKey().startsWith(prefix));
		}
		if (minCount != null && minCount > 0) {
			assetNamesCounted = assetNamesCounted.filter(e -> e.getValue() > minCount);
		}
		return assetNamesCounted.map(e -> new ElementCount(e.getKey(), e.getValue())).collect(Collectors.toList());
	}

	@Override
	public List<ElementCount> getAssetLinkValues(String assetLinkName, String prefix, Integer minCount) {
		Stream<Map.Entry<String, Long>> assetNameValuesCounted = assetLinks.values().stream().flatMap(Set::stream).filter(e -> assetLinkName.equals(e.getName())).map(AssetLink::getValue).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream();
		if (prefix != null && !prefix.isBlank()) {
			assetNameValuesCounted = assetNameValuesCounted.filter(e -> e.getKey().startsWith(prefix));
		}
		if (minCount != null && minCount > 0) {
			assetNameValuesCounted = assetNameValuesCounted.filter(e -> e.getValue() > minCount);
		}
		return assetNameValuesCounted.map(e -> new ElementCount(e.getKey(), e.getValue())).collect(Collectors.toList());
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		synchronized (assetLinks) {
			throwIfAssetLinkDoesNotExist(shellIdentifier);

			assetLinks.remove(shellIdentifier);
			assetIds.remove(shellIdentifier);
		}
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

	private Set<String> getShellIdsWithAssetLinks(List<AssetLink> requestedLinks) {
		return assetLinks.entrySet().stream().filter(entry -> entry.getValue().containsAll(requestedLinks)).map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	private void throwIfAssetLinkDoesNotExist(String shellIdentifier) {
		if (!assetLinks.containsKey(shellIdentifier))
			throw ExceptionBuilderFactory.getInstance().assetLinkDoesNotExistException().missingIdentifier(shellIdentifier).build();
	}

	private void throwIfAssetLinkExists(String shellIdentifier) {
		if (assetLinks.containsKey(shellIdentifier))
			throw ExceptionBuilderFactory.getInstance().collidingIdentifierException().collidingIdentifier(shellIdentifier).build();
	}

}
