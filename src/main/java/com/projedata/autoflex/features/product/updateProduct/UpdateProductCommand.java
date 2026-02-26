package com.projedata.autoflex.features.product.updateProduct;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductCommand(
    String name,
    BigDecimal value,
    List<UpdateMaterialRequirementDto> materials
) {
    
}
