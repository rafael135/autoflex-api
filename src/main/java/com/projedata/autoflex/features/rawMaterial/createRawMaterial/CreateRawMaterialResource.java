package com.projedata.autoflex.features.rawMaterial.createRawMaterial;

import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.features.rawMaterial.RawMaterialDto;
import com.projedata.autoflex.features.rawMaterial.RawMaterialMapper;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateRawMaterialResource {
    
    private final RawMaterialRepository repository;
    private final RawMaterialMapper rawMaterialMapper;

    public CreateRawMaterialResource(RawMaterialRepository repository, RawMaterialMapper rawMaterialMapper) {
        this.repository = repository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @POST
    @Transactional
    public Response create(CreateRawMaterialCommand command) {
        RawMaterial rawMaterial = RawMaterial.create(command.name(), command.initialStock());

        this.repository.persist(rawMaterial);

        RawMaterialDto rawMaterialDto = this.rawMaterialMapper.toDto(rawMaterial);

        return Response.status(Response.Status.CREATED).entity(rawMaterialDto).build();
    }
}
