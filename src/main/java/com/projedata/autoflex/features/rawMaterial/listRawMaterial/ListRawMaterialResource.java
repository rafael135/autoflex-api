package com.projedata.autoflex.features.rawMaterial.listRawMaterial;

import java.util.List;

import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.features.rawMaterial.RawMaterialDto;
import com.projedata.autoflex.features.rawMaterial.RawMaterialMapper;
import com.projedata.autoflex.features.shared.PaginatedDto;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
public class ListRawMaterialResource {
    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    public ListRawMaterialResource(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @GET
    public Response list(
        @QueryParam("page") @DefaultValue("0") @Min(0) Integer page,
        @QueryParam("itemsPerPage") @DefaultValue("10") @Min(1) @Max(100) Integer itemsPerPage
    ) {
        if(page > 0) {
            page -= 1;
        }

        PanacheQuery<RawMaterial> query = this.rawMaterialRepository.findAll(Sort.by("id").descending())
            .page(page, itemsPerPage);

        List<RawMaterial> rawMaterials = query
            .stream()
            .toList();

        List<RawMaterialDto> dtos = this.rawMaterialMapper
            .toDtoList(rawMaterials);

        PaginatedDto<RawMaterialDto> paginatedResponse = new PaginatedDto<RawMaterialDto>(
            dtos,
            (int)query.count(),
            query.pageCount(),
            page + 1
        );

        return Response.status(Response.Status.OK).entity(paginatedResponse).build();

    }
}
