package org.example.api;

import io.apicurio.registry.types.ArtifactType;
import io.apicurio.registry.types.RuleType;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletionStage;
import org.example.api.beans.ArtifactMetaData;
import org.example.api.beans.EditableMetaData;
import org.example.api.beans.Rule;
import org.example.api.beans.VersionMetaData;

/**
 * A JAX-RS interface. An implementation of this interface must be provided.
 */
@Path("/artifacts")
public interface ArtifactsResource {
  /**
   * <p>
   * Creates a new artifact by POSTing the artifact content. The body of the
   * request should be the raw content of the artifact. This will typically be in
   * JSON format for <em>most</em> of the supported types, but may be in another
   * format for a few (e.g. Protobuff).
   * </p>
   * <p>
   * The registry will attempt to figure out what kind of artifact is being added
   * from the following supported list:
   * </p>
   * <ul>
   * <li>Avro (AVRO)</li>
   * <li>Protobuff (PROTOBUFF)</li>
   * <li>JSON Schema (JSON)</li>
   * <li>OpenAPI (OPENAPI)</li>
   * <li>AsyncAPI (ASYNCAPI)</li>
   * </ul>
   * <p>
   * Alternatively, the artifact type can be indicated by either explicitly
   * specifying the type via the <code>X-Registry-ArtifactType</code> HTTP Request
   * Header or by including a hint in the Request's <code>Content-Type</code>. For
   * example:
   * </p>
   * 
   * <pre>
   * <code>Content-Type: application/json; artifactType=AVRO
  </code>
   * </pre>
   * <p>
   * This operation may fail for one of the following reasons:
   * </p>
   * <ul>
   * <li>An invalid <code>ArtifactType</code> was indicated (HTTP error
   * <code>400</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @POST
  @Produces("application/json")
  @Consumes({"application/json", "application/x-yaml"})
  CompletionStage<ArtifactMetaData> createArtifact(
      @HeaderParam("X-Registry-ArtifactType") ArtifactType xRegistryArtifactType,
      @HeaderParam("X-Registry-ArtifactId") String xRegistryArtifactId, InputStream data);

  /**
   * <p>
   * Returns the latest version of the artifact in its raw form. The
   * <code>Content-Type</code> of the response will depend on what type of
   * artifact it is. In most cases this will be <code>application/json</code> but
   * for some types it may be different (e.g. <em>protobuff</em>).
   * </p>
   * <p>
   * This operation may fail for one of the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}")
  @GET
  @Produces({"application/json", "application/x-yaml"})
  Response getLatestArtifact(@PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Updates an artifact by uploading new content. The body of the request should
   * be the raw content of the artifact. This will typically be in JSON format for
   * <em>most</em> of the supported types, but may be in another format for a few
   * (e.g. Protobuff).
   * </p>
   * <p>
   * The registry will attempt to figure out what kind of artifact is being added
   * from the following supported list:
   * </p>
   * <ul>
   * <li>Avro (AVRO)</li>
   * <li>Protobuff (PROTOBUFF)</li>
   * <li>JSON Schema (JSON)</li>
   * <li>OpenAPI (OPENAPI)</li>
   * <li>AsyncAPI (ASYNCAPI)</li>
   * </ul>
   * <p>
   * Alternatively, the artifact type can be indicated by either explicitly
   * specifying the type via the <code>X-Registry-ArtifactType</code> HTTP Request
   * Header or by including a hint in the Request's <code>Content-Type</code>. For
   * example:
   * </p>
   * 
   * <pre>
   * <code>Content-Type: application/json; artifactType=AVRO
  </code>
   * </pre>
   * <p>
   * The update could fail for a number of reasons including:
   * </p>
   * <ul>
   * <li>No artifact with the <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>The new content violates one of the rules configured for the artifact
   * (HTTP error <code>400</code>)</li>
   * <li>The provided Artifact Type is not recognized (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * <p>
   * When successful, this creates a new version of the artifact, making it the
   * most recent (and therefore official) version of the artifact.
   * </p>
   * 
   */
  @Path("/{artifactId}")
  @PUT
  @Produces("application/json")
  @Consumes({"application/json", "application/x-yaml"})
  ArtifactMetaData updateArtifact(@PathParam("artifactId") String artifactId,
      @HeaderParam("X-Registry-ArtifactType") ArtifactType xRegistryArtifactType, InputStream data);

  /**
   * <p>
   * Deletes an artifact completely, resulting in all versions of the artifact
   * also being deleted. This may fail for one of the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with the <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}")
  @DELETE
  void deleteArtifact(@PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Gets the meta-data for an artifact in the registry. The returned meta-data
   * will include both generated (read-only) and editable meta-data (such as name
   * and description).
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/meta")
  @GET
  @Produces("application/json")
  ArtifactMetaData getArtifactMetaData(@PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Updates the editable parts of the artifact's meta-data. Not all meta-data
   * fields can be updated. For example <code>createdOn</code> and
   * <code>createdBy</code> are both read-only properties.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with the <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/meta")
  @PUT
  @Consumes("application/json")
  void updateArtifactMetaData(@PathParam("artifactId") String artifactId, EditableMetaData data);

  /**
   * <p>
   * Returns information about a single rule configured for an artifact. This is
   * useful when you want to know what the current configuration settings are for
   * a specific rule.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No rule with this name/type is configured for this artifact (HTTP error
   * <code>404</code>)</li>
   * <li>Invalid rule type (HTTP error <code>400</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules/{rule}")
  @GET
  @Produces("application/json")
  Rule getArtifactRuleConfig(@PathParam("rule") String rule, @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Updates the configuration of a single rule for the artifact. The
   * configuration data is specific to each rule type, so the configuration of the
   * <strong>Compatibility</strong> rule will be of a different format than the
   * configuration of the <strong>Validation</strong> rule.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No rule with this name/type is configured for this artifact (HTTP error
   * <code>404</code>)</li>
   * <li>Invalid rule type (HTTP error <code>400</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules/{rule}")
  @PUT
  @Produces("application/json")
  @Consumes("application/json")
  Rule updateArtifactRuleConfig(@PathParam("rule") String rule, @PathParam("artifactId") String artifactId, Rule data);

  /**
   * <p>
   * Deletes a rule from the artifact. This results in the rule no longer applying
   * for this artifact. If this is the only rule configured for the artifact, then
   * this is the same as deleting <strong>all</strong> rules: the globally
   * configured rules will now apply to this artifact.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No rule with this name/type is configured for this artifact (HTTP error
   * <code>404</code>)</li>
   * <li>Invalid rule type (HTTP error <code>400</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules/{rule}")
  @DELETE
  void deleteArtifactRule(@PathParam("rule") String rule, @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Returns a list of all version numbers for the artifact.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions")
  @GET
  @Produces("application/json")
  List<Long> listArtifactVersions(@PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Creates a new version of the artifact by uploading new content. The
   * configured rules for the artifact will be applied, and if they all pass then
   * the new content will be added as the most recent version of the artifact. If
   * any of the rules fail then an error will be returned.
   * </p>
   * <p>
   * The body of the request should be the raw content of the new artifact
   * version. This will typically be in JSON format for <em>most</em> of the
   * supported types, but may be in another format for a few (e.g. Protobuff).
   * </p>
   * <p>
   * The registry will attempt to figure out what kind of artifact is being added
   * from the following supported list:
   * </p>
   * <ul>
   * <li>Avro (AVRO)</li>
   * <li>Protobuff (PROTOBUFF)</li>
   * <li>JSON Schema (JSON)</li>
   * <li>OpenAPI (OPENAPI)</li>
   * <li>AsyncAPI (ASYNCAPI)</li>
   * </ul>
   * <p>
   * Alternatively, the artifact type can be indicated by either explicitly
   * specifying the type via the <code>X-Registry-ArtifactType</code> HTTP Request
   * Header or by including a hint in the Request's <code>Content-Type</code>.
   * </p>
   * <p>
   * For example:
   * </p>
   * 
   * <pre>
   * <code>Content-Type: application/json; artifactType=AVRO
  </code>
   * </pre>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions")
  @POST
  @Produces("application/json")
  @Consumes({"application/json", "application/x-yaml"})
  VersionMetaData createArtifactVersion(@PathParam("artifactId") String artifactId,
      @HeaderParam("X-Registry-ArtifactType") ArtifactType xRegistryArtifactType, InputStream data);

  /**
   * <p>
   * Retrieves a single version of the artifact content. Both the
   * <code>artifactId</code> and the unique <code>version</code> number must be
   * provided. The <code>Content-Type</code> of the response will depend on what
   * type of artifact it is. In most cases this will be
   * <code>application/json</code> but for some types it may be different (e.g.
   * <em>protobuff</em>).
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No version with this <code>version</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions/{version}")
  @GET
  @Produces({"application/json", "application/x-yaml"})
  Response getArtifactVersion(@PathParam("version") Integer version, @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Deletes a single version of the artifact. Both the <code>artifactId</code>
   * and the unique <code>version</code> are needed. If this is the only version
   * of the artifact, then this operation is the same as deleting the entire
   * artifact.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No version with this <code>version</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions/{version}")
  @DELETE
  void deleteArtifactVersion(@PathParam("version") Integer version, @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Retrieves the meta-data for a single version of the artifact. The version
   * meta-data is a subset of the artifact meta-data - it is only the meta-data
   * that is specific to the version (and so doesn't include e.g.
   * <code>modifiedOn</code>).
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No version with this <code>version</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions/{version}/meta")
  @GET
  @Produces("application/json")
  VersionMetaData getArtifactVersionMetaData(@PathParam("version") Integer version,
      @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Updates the user-editable portion of the artifact version's meta-data. Only
   * some of the meta-data fields are editable by the user. For example the
   * <code>description</code> is editable but the <code>createdOn</code> is not.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No version with this <code>version</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions/{version}/meta")
  @PUT
  @Consumes("application/json")
  void updateArtifactVersionMetaData(@PathParam("version") Integer version, @PathParam("artifactId") String artifactId,
      EditableMetaData data);

  /**
   * <p>
   * Deletes the user-editable meta-data properties of the artifact version. Any
   * properties that are not user-editable will be preserved.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>No version with this <code>version</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/versions/{version}/meta")
  @DELETE
  void deleteArtifactVersionMetaData(@PathParam("version") Integer version, @PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Returns a list of all rules configured for the artifact. The set of rules
   * determines how the content of an artifact can evolve over time. If no rules
   * are configured for an artifact, then the set of globally configured rules
   * will be used. If no global rules are defined then there are no restrictions
   * on content evolution.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules")
  @GET
  @Produces("application/json")
  List<RuleType> listArtifactRules(@PathParam("artifactId") String artifactId);

  /**
   * <p>
   * Adds a rule to the list of rules that get applied to the artifact when adding
   * new versions. All configured rules must pass in order to successfully add a
   * new artifact version.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>Rule (named in the request body) is unknown (HTTP error
   * <code>400</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules")
  @POST
  @Consumes("application/json")
  void createArtifactRule(@PathParam("artifactId") String artifactId, Rule data);

  /**
   * <p>
   * Deletes all of the rules configured for the artifact. After this is done, the
   * global rules will once again apply to the artifact.
   * </p>
   * <p>
   * This operation can fail for the following reasons:
   * </p>
   * <ul>
   * <li>No artifact with this <code>artifactId</code> exists (HTTP error
   * <code>404</code>)</li>
   * <li>A server error occurred (HTTP error <code>500</code>)</li>
   * </ul>
   * 
   */
  @Path("/{artifactId}/rules")
  @DELETE
  void deleteArtifactRules(@PathParam("artifactId") String artifactId);
}
