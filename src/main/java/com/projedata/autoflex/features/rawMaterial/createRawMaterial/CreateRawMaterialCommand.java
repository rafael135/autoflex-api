package com.projedata.autoflex.features.rawMaterial.createRawMaterial;

public record CreateRawMaterialCommand(
    String name,
    Integer stockQuantity
) {}
