package com.projedata.autoflex.features.product.updateProduct;

import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.ProductMaterial;
import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.features.product.ProductDto;
import com.projedata.autoflex.features.product.ProductMapper;
import com.projedata.autoflex.infrastructure.repositories.ProductMaterialRepository;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.PUT;

@Path("/api/products")
public class UpdateProductResource {
    private final ProductRepository productRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;

    public UpdateProductResource(ProductRepository productRepository, ProductMaterialRepository productMaterialRepository, RawMaterialRepository rawMaterialRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMaterialRepository = productMaterialRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, UpdateProductCommand command) {
        Product existentProduct = this.productRepository.findById(id);

        if (existentProduct == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        existentProduct.setName(command.name());
        existentProduct.setValue(command.value());

        List<ProductMaterial> productMaterialsRemoved = existentProduct.materials.stream()
            .filter(pm -> command.materials().stream().noneMatch(rm -> rm.rawMaterialId().equals(pm.rawMaterial.id)))
            .toList();

        for (UpdateMaterialRequirementDto material : command.materials()) {
            ProductMaterial pm = this.productMaterialRepository
                .find("product.id = ?1 and rawMaterial.id = ?2", id, material.rawMaterialId())
                .firstResult();

            if (pm != null) {
                pm.setRequiredQuantity(material.quantity());
                continue;
            }

            RawMaterial rawMaterial = this.rawMaterialRepository.findById(material.rawMaterialId());

            if (rawMaterial == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Raw material with ID " + material.rawMaterialId() + " not found.")
                    .build();
            }

            existentProduct.addMaterial(rawMaterial, material.quantity());
        }

        for (ProductMaterial pm : productMaterialsRemoved) {
            existentProduct.removeMaterial(pm);
        }

        this.productRepository.persist(existentProduct);

        ProductDto productDto = this.productMapper.toDto(existentProduct);

        return Response.status(Response.Status.OK)
            .entity(productDto)
            .build();
    }
}
