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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.FilterParams;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

/**
 * InMemory implementation for the AAS backend
 * 
 * @author mateusmolina
 * 
 */
public class AasInMemoryBackend implements BaSyxCrudRepository<AssetAdministrationShell, String> {

	private final Map<String, AssetAdministrationShell> inMemoryStore = new LinkedHashMap<>();

	@Override
	public <S extends AssetAdministrationShell> S save(S entity) {
		inMemoryStore.put(entity.getId(), entity);

		return entity;
	}

	@Override
	public <S extends AssetAdministrationShell> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities)
			inMemoryStore.put(entity.getId(), entity);

		return entities;
	}

	@Override
	public Optional<AssetAdministrationShell> findById(String id) {
		return Optional.ofNullable(inMemoryStore.get(id));
	}

	@Override
	public boolean existsById(String id) {
		return inMemoryStore.containsKey(id);
	}

	@Override
	public Iterable<AssetAdministrationShell> findAll() {
		return inMemoryStore.values();
	}

	@Override
	public Iterable<AssetAdministrationShell> findAllById(Iterable<String> ids) {
		return StreamSupport.stream(ids.spliterator(), false).map(inMemoryStore::get).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public long count() {
		return inMemoryStore.size();
	}

	@Override
	public void deleteById(String id) {
		inMemoryStore.remove(id);
	}

	@Override
	public void delete(AssetAdministrationShell entity) {
		inMemoryStore.remove(entity.getId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids)
			inMemoryStore.remove(id);
	}

	@Override
	public void deleteAll(Iterable<? extends AssetAdministrationShell> entities) {
		for (AssetAdministrationShell entity : entities)
			inMemoryStore.remove(entity.getId());
	}

	@Override
	public void deleteAll() {
		inMemoryStore.clear();
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> findAll(FilterParams filterParams) {
		Iterable<AssetAdministrationShell> iterable = findAll();
		List<AssetAdministrationShell> allAas = StreamSupport.stream(iterable.spliterator(), false).toList();

		TreeMap<String, AssetAdministrationShell> aasMap = allAas.stream().collect(Collectors.toMap(AssetAdministrationShell::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShell> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShell::getId);

		return paginationSupport.getPaged(filterParams.getPaginationInfo());
	}
}
