package com.projedata.autoflex.features.production.getTotalProductionCapacity.dto;

import java.math.BigDecimal;
import java.util.List;

public record TotalProductionResponse(
    List<ProductDetailDto> products,
    BigDecimal totalProductionValue
) {}
