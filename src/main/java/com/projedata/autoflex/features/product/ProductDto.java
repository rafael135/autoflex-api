package com.projedata.autoflex.features.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
    Long id,
    String name,
    BigDecimal value,
    List<MaterialRequirementDto> materials
) {}
