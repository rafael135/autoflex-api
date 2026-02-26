package com.projedata.autoflex.features.rawMaterial.updateRawMaterial;

public record UpdateRawMaterialCommand(
    String name,
    Integer stockQuantity
) {
    
}
