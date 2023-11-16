/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.41).
 * https://github.com/swagger-api/swagger-codegen Do not edit the class manually.
 */
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.pagination.GetConceptDescriptionsResult;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-21T12:35:49.719724407Z[GMT]")
@Validated
public interface ConceptDescriptionRepositoryHTTPApi {

	@Operation(summary = "Deletes a Concept Description", description = "", tags = { "Concept Description Repository API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Concept Description deleted successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/concept-descriptions/{cdIdentifier}", produces = { "application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier);

	@Operation(summary = "Returns all Concept Descriptions", description = "", tags = { "Concept Description Repository API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested Concept Descriptions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetConceptDescriptionsResult.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/concept-descriptions", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<PagedResult> getAllConceptDescriptions(@Parameter(in = ParameterIn.QUERY, description = "The Concept Description’s IdShort", schema = @Schema()) @Valid @RequestParam(value = "idShort", required = false) String idShort,
			@Parameter(in = ParameterIn.QUERY, description = "IsCaseOf reference (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "isCaseOf", required = false) Base64UrlEncodedIdentifier isCaseOf,
			@Parameter(in = ParameterIn.QUERY, description = "DataSpecification reference (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "dataSpecificationRef", required = false) Base64UrlEncodedIdentifier dataSpecificationRef,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor);

	@Operation(summary = "Returns a specific Concept Description", description = "", tags = { "Concept Description Repository API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested Concept Description", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConceptDescription.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/concept-descriptions/{cdIdentifier}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<ConceptDescription> getConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier);

	@Operation(summary = "Creates a new Concept Description", description = "", tags = { "Concept Description Repository API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Concept Description created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConceptDescription.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/concept-descriptions", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<ConceptDescription> postConceptDescription(@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body);

	@Operation(summary = "Updates an existing Concept Description", description = "", tags = { "Concept Description Repository API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Concept Description updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/concept-descriptions/{cdIdentifier}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	ResponseEntity<Void> putConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body);
}
