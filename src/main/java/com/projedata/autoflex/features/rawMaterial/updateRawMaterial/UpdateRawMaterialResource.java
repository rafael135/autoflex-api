package com.projedata.autoflex.features.rawMaterial.updateRawMaterial;

import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.features.rawMaterial.RawMaterialDto;
import com.projedata.autoflex.features.rawMaterial.RawMaterialMapper;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UpdateRawMaterialResource {
    
    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    public UpdateRawMaterialResource(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @Transactional
    @Path("/{id}")
    @PUT
    public Response update(@PathParam("id") Long id, UpdateRawMaterialCommand command) {
        RawMaterial rawMaterial = this.rawMaterialRepository.findById(id);

        if (rawMaterial == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        rawMaterial.setStockQuantity(command.stockQuantity());
        rawMaterial.setName(command.name());

        this.rawMaterialRepository.persist(rawMaterial);

        RawMaterialDto dto = this.rawMaterialMapper.toDto(rawMaterial);

        return Response.status(Response.Status.OK)
            .entity(dto)
            .build();
    }
}
