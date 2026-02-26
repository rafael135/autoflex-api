package com.projedata.autoflex.features.product.createProduct;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
    String name,
    BigDecimal value,
    List<CreateMaterialRequirementDto> materials
) {}
