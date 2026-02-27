package com.projedata.autoflex.features.production.getTotalProductionCapacity.services;

import java.util.HashMap;
import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.features.production.getTotalProductionCapacity.dto.TotalProductionResponse;

public interface IProductionCalculationStrategy {
    
    TotalProductionResponse CalculateProduction(List<Product> products, HashMap<Long, Integer> availableMaterials);
}
