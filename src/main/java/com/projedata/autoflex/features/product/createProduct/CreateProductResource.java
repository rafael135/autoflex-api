package com.projedata.autoflex.features.product.createProduct;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.features.product.ProductDto;
import com.projedata.autoflex.features.product.ProductMapper;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreateProductResource {
    
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;

    public CreateProductResource(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMapper = productMapper;
    }


    @POST
    @Transactional
    public Response create(CreateProductCommand command) {

        Product product = Product.create(command.name(), command.value());

        if(command.materials() != null && !command.materials().isEmpty()) {
            for (CreateMaterialRequirementDto material : command.materials()) {
                
                RawMaterial rawMaterial = this.rawMaterialRepository.findById(material.rawMaterialId());

                if (rawMaterial == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity("Raw material with ID " + material.rawMaterialId() + " not found.")
                        .build();
                }

                product.addMaterial(rawMaterial, material.quantity());
            }
        }

        this.productRepository.persist(product);

        ProductDto productDto = this.productMapper.toDto(product);

        return Response.status(Response.Status.CREATED)
            .entity(productDto)
            .build();
    }
}
