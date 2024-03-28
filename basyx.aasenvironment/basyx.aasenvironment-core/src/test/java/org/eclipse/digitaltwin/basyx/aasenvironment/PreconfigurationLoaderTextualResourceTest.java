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
package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration.AasEnvironmentPreconfigurationLoader;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilterParams;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.TraceableMessageSerializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelFilterParams;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PreconfigurationLoaderTextualResourceTest {

	private static final String TEST_ENVIRONMENT_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment.json";
	private static final String TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_version_on_second.json";
	private static final String TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_version_and_revision_on_second.json";

	private static final String TEST_ENVIRONMENT_SHELLS_ONLY_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_with_shells_only.json";
	private static final String TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_with_submodels_only.json";

	private static final PaginationInfo ALL = new PaginationInfo(0, null);

	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;
	private final ResourceLoader rLoader = new DefaultResourceLoader();

	@Before
	public void setUp() {
		submodelRepository = Mockito.spy(new SimpleSubmodelRepositoryFactory(new SubmodelInMemoryBackendProvider(), new InMemorySubmodelServiceFactory()).create());
		aasRepository = Mockito.spy(new SimpleAasRepositoryFactory(new AasInMemoryBackendProvider(), new InMemoryAasServiceFactory(), getThumbnailFolder()).create());
		conceptDescriptionRepository = Mockito.spy(new SimpleConceptDescriptionRepositoryFactory(new ConceptDescriptionInMemoryBackendProvider(), "cdRepo").create());

		TraceableMessageSerializer messageSerializer = new TraceableMessageSerializer(new ObjectMapper());
		ExceptionBuilderFactory builderFactory = new ExceptionBuilderFactory(messageSerializer);
		ExceptionBuilderFactory.setInstance(builderFactory);
	}

	@Test
	public void testWithEmptyResource_NoElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of());
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);
		AasFilterParams filterParams = new AasFilterParams();
		filterParams.setPaginationInfo(ALL);
		Assert.assertTrue(aasRepository.getAllAas(filterParams).getResult().isEmpty());

		SubmodelFilterParams submodelFilterParams = new SubmodelFilterParams();
		submodelFilterParams.setPaginationInfo(ALL);
		Assert.assertTrue(submodelRepository.getAllSubmodels(submodelFilterParams).getResult().isEmpty());
		Assert.assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().isEmpty());

		Mockito.verify(aasRepository, Mockito.never()).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.never()).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
	}

	@Test
	public void testWithResourceFile_AllElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException, SerializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_JSON));
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);
		AasFilterParams filterParams = new AasFilterParams();
		filterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, aasRepository.getAllAas(filterParams).getResult().size());

		SubmodelFilterParams submodelFilterParams = new SubmodelFilterParams();
		submodelFilterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(submodelFilterParams).getResult().size());

		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceNoVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException, SerializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_JSON));

		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		AasFilterParams filterParams = new AasFilterParams();
		filterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, aasRepository.getAllAas(filterParams).getResult().size());

		SubmodelFilterParams submodelFilterParams = new SubmodelFilterParams();
		submodelFilterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(submodelFilterParams).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceWithSameVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException, SerializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));

		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		AasFilterParams filterParams = new AasFilterParams();
		filterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, aasRepository.getAllAas(filterParams).getResult().size());

		SubmodelFilterParams submodelFilterParams = new SubmodelFilterParams();
		submodelFilterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(submodelFilterParams).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceNewRevision_ElementsAreOverriden() throws InvalidFormatException, IOException, DeserializationException, SerializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);
		envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON));
		envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository);

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(1)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(1)).updateSubmodel(Mockito.anyString(), Mockito.any());

		AasFilterParams filterParams = new AasFilterParams();
		filterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, aasRepository.getAllAas(filterParams).getResult().size());

		SubmodelFilterParams submodelFilterParams = new SubmodelFilterParams();
		submodelFilterParams.setPaginationInfo(ALL);
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(submodelFilterParams).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDuplicateSubmodelIdsInEnvironments_ExceptionIsThrown() throws InvalidFormatException, IOException, DeserializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_SHELLS_ONLY_JSON, TEST_ENVIRONMENT_SHELLS_ONLY_JSON));
		ExceptionBuilderFactory.getInstance().collidingIdentifierException().collidingIdentifier("aas1").build();
		String expectedMsg = ExceptionBuilderFactory.getInstance().collidingIdentifierException().collidingIdentifier("aas1").build().getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository));
	}

	@Test
	public void testDuplicateShellIdsInEnvironments_ExceptionIsThrown() {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, List.of(TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON, TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON));
		String expectedMsg = ExceptionBuilderFactory.getInstance().collidingIdentifierException().collidingIdentifier("sm1").build().getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> envLoader.loadPreconfiguredEnvironments(aasRepository, submodelRepository, conceptDescriptionRepository));
	}

	private static String getThumbnailFolder() {
		String projectRoot = System.getProperty("user.dir");
		return projectRoot + "/target/thumbnail_storage";
	}
}
