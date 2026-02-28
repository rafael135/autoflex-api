package com.projedata.autoflex.features.production.getTotalProductionCapacity;

import java.util.HashMap;
import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.dto.TotalProductionResponse;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.services.HighestEfficiencyStrategy;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.services.HighestPriceStrategy;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.services.IProductionCalculationStrategy;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/production")
public class GetTotalProductionCapacityHandler {
    
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    @Inject
    public GetTotalProductionCapacityHandler(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalProductionCapacity(
         @QueryParam("strategy") @Min(0) @Max(1) @DefaultValue("0") Integer strategy
    ) {
        List<Product> allProducts = this.productRepository.listAll();
        HashMap<Long, Integer> availableMaterials = this.rawMaterialRepository
            .getTotalMaterialsInStock();

        IProductionCalculationStrategy calculationStrategy;
        if (strategy != null && strategy == 1) {
            calculationStrategy = new HighestEfficiencyStrategy();
        } else {
            calculationStrategy = new HighestPriceStrategy();
        }

        TotalProductionResponse response = calculationStrategy.CalculateProduction(allProducts, availableMaterials);

        return Response.status(Response.Status.OK)
            .entity(response)
            .build();

    }
}
