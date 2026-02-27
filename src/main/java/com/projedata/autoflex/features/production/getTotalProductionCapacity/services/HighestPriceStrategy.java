package com.projedata.autoflex.features.production.getTotalProductionCapacity.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.ProductMaterial;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.dto.ProductDetailDto;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.dto.TotalProductionResponse;

public class HighestPriceStrategy implements IProductionCalculationStrategy {

    @Override
    public TotalProductionResponse CalculateProduction(
        List<Product> products,
        HashMap<Long, Integer> availableMaterials
    ) {
        products.sort((p1, p2) -> p2.value.compareTo(p1.value));

        List<ProductDetailDto> productDetails = new ArrayList<>();

        BigDecimal totalProductionValue = BigDecimal.ZERO.setScale(2);

        for (Product product : products) {
            Integer maxProductionCapacity = Integer.MAX_VALUE;

            for (ProductMaterial material : product.materials) {
                Integer availableQuantity = availableMaterials.getOrDefault(material.rawMaterial.id, 0);
                Integer possibleProduction = availableQuantity / material.requiredQuantity;

                if (possibleProduction < maxProductionCapacity) {
                    maxProductionCapacity = possibleProduction;
                }
            }

            if (maxProductionCapacity > 0) {
                for (ProductMaterial material : product.materials) {
                    Integer usedQuantity = maxProductionCapacity * material.requiredQuantity;
                    Integer remainingQuantity = availableMaterials.get(material.rawMaterial.id) - usedQuantity;
                    availableMaterials.put(material.rawMaterial.id, remainingQuantity);
                }

                totalProductionValue = totalProductionValue
                    .add(
                        product.value.multiply(
                            BigDecimal.valueOf(maxProductionCapacity)
                        )
                    );

                productDetails.add(new ProductDetailDto(
                    product.id,
                    product.name,
                    maxProductionCapacity,
                    product.value.multiply(BigDecimal.valueOf(maxProductionCapacity))
                ));
            }
        }

        return new TotalProductionResponse(
            productDetails,
            totalProductionValue
        );
    }
}
