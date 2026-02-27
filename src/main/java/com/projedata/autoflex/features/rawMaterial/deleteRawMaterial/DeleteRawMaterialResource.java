package com.projedata.autoflex.features.rawMaterial.deleteRawMaterial;

import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.infrastructure.repositories.ProductMaterialRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
public class DeleteRawMaterialResource {

    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMaterialRepository productMaterialRepository;

    public DeleteRawMaterialResource(RawMaterialRepository rawMaterialRepository, ProductMaterialRepository productMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMaterialRepository = productMaterialRepository;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        RawMaterial rawMaterial = this.rawMaterialRepository.findById(id);

        if (rawMaterial == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long usageCount = this.productMaterialRepository.count("rawMaterial.id", id);

        if (usageCount > 0) {
            throw new IllegalStateException("Raw material is in use by one or more products and cannot be deleted.");
        }

        this.rawMaterialRepository.delete(rawMaterial);

        return Response.noContent().build();
    }
}
