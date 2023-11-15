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

/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.46).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.pagination.InlineResponse200;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-10-10T10:16:17.046754509Z[GMT]")
@Validated
public interface LookupApi {

    @Operation(summary = "Deletes all specific Asset identifiers linked to an Asset Administration Shell to edit discoverable content", description = "", tags={ "Asset Administration Shell Basic Discovery API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Specific Asset identifiers deleted successfully"),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/lookup/shells/{aasIdentifier}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier);


    @Operation(summary = "Returns a list of Asset Administration Shell ids linked to specific Asset identifiers", description = "", tags={ "Asset Administration Shell Basic Discovery API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Requested Asset Administration Shell ids", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/lookup/shells",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<PagedResult> getAllAssetAdministrationShellIdsByAssetLink(@Parameter(in = ParameterIn.QUERY, description = "A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.0.1#/components/schemas/SpecificAssetId)" ,schema=@Schema()) @Valid @RequestParam(value = "assetIds", required = false) List<Base64UrlEncodedIdentifier> assetIds, @Min(1)@Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array" ,schema=@Schema(allowableValues={ "1" }, minimum="1"
)) @Valid @RequestParam(value = "limit", required = false) Integer limit, @Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue" ,schema=@Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor);


    @Operation(summary = "Returns a list of specific Asset identifiers based on an Asset Administration Shell id to edit discoverable content", description = "", tags={ "Asset Administration Shell Basic Discovery API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Requested specific Asset identifiers", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SpecificAssetID.class)))),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/lookup/shells/{aasIdentifier}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<SpecificAssetID>> getAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier);


    @Operation(summary = "Creates specific Asset identifiers linked to an Asset Administration Shell to edit discoverable content", description = "", tags={ "Asset Administration Shell Basic Discovery API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Specific Asset identifiers created successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SpecificAssetID.class)))),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/lookup/shells/{aasIdentifier}",
        produces = { "application/json" }, 
        consumes = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<List<SpecificAssetID>> postAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier, @Parameter(in = ParameterIn.DEFAULT, description = "A list of specific Asset identifiers", required=true, schema=@Schema()) @Valid @RequestBody List<SpecificAssetID> body);

}

