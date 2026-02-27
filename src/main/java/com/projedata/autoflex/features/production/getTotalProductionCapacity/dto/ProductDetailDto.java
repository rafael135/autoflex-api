package com.projedata.autoflex.features.production.getTotalProductionCapacity.dto;

import java.math.BigDecimal;

public record ProductDetailDto(
    Long id,
    String name,
    Integer maxProductionCapacity,
    BigDecimal totalValue
) {
    
}
