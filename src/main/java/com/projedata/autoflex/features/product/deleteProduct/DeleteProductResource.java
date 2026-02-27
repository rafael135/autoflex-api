package com.projedata.autoflex.features.product.deleteProduct;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
public class DeleteProductResource {

    private final ProductRepository productRepository;

    public DeleteProductResource(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Product product = this.productRepository.findById(id);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        this.productRepository.delete(product);

        return Response.noContent().build();
    }
}
