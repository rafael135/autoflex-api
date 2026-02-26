package com.projedata.autoflex.features.product.listProducts;

import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.features.product.ProductDto;
import com.projedata.autoflex.features.product.ProductMapper;
import com.projedata.autoflex.features.shared.PaginatedDto;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
public class ListProductsResource {
    private final ProductRepository repository;
    private final ProductMapper productMapper;

    public ListProductsResource(ProductRepository repository, ProductMapper productMapper) {
        this.repository = repository;
        this.productMapper = productMapper;
    }

    @GET
    public Response list(
        @QueryParam("page") @DefaultValue("0") @Min(0) Integer page,
        @QueryParam("itemsPerPage") @DefaultValue("10") @Min(1) @Max(100) Integer itemsPerPage
    ) {
        if(page > 0) {
            page -= 1;
        }

        PanacheQuery<Product> query = this.repository.findAllDescending()
            .page(page, itemsPerPage);

        List<Product> products = query
            .stream()
            .toList();

        List<ProductDto> productDtos = this.productMapper.toDtoList(products);
        
        PaginatedDto<ProductDto> paginatedDtos = new PaginatedDto<ProductDto>(
            productDtos,
            (int)query.count(),
            query.pageCount(),
            page + 1
        );

        return Response.status(Response.Status.OK)
            .entity(paginatedDtos)
            .build();
    }
}
