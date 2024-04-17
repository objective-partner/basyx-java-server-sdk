/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.core.exceptions.ExceptionBuilderFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.file.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.file.FileRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.gridfs.model.GridFSFile;

/**
 * A MongoDB implementation of the {@link FileRepository}
 * 
 * @author danish
 */
public class MongoDBSubmodelFileRepository implements FileRepository {

	private String MONGO_FILENAME = "filename";

	private GridFsTemplate gridFsTemplate;

	public MongoDBSubmodelFileRepository(GridFsTemplate gridFsTemplate) {
		this.gridFsTemplate = gridFsTemplate;
	}

	@Override
	public String save(FileMetadata fileMetadata) throws FileHandlingException {
		gridFsTemplate.store(fileMetadata.getFileContent(), fileMetadata.getFileName(), fileMetadata.getContentType());
		return fileMetadata.getFileName();
	}

	@Override
	public InputStream find(String fileId) throws FileDoesNotExistException {
		if (!exists(fileId))
			throw ExceptionBuilderFactory.getInstance().fileDoesNotExistException().elementPath(fileId).build();

		GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where(MONGO_FILENAME).is(fileId)));

		return getGridFsFileAsInputStream(file);
	}

	@Override
	public void delete(String fileId) throws FileDoesNotExistException {
		if (!exists(fileId))
			throw ExceptionBuilderFactory.getInstance().fileDoesNotExistException().elementPath(fileId).build();

		gridFsTemplate.delete(new Query(Criteria.where(MONGO_FILENAME).is(fileId)));
	}

	@Override
	public boolean exists(String fileId) {
		return gridFsTemplate.findOne(new Query(Criteria.where(MONGO_FILENAME).is(fileId))) != null;
	}

	private InputStream getGridFsFileAsInputStream(GridFSFile file) {
		try {
			return gridFsTemplate.getResource(file).getInputStream();
		} catch (IllegalStateException | IOException e) {
			throw new IllegalStateException("Unable to get the file resource as input stream. " + e.getMessage());
		}
	}
}
