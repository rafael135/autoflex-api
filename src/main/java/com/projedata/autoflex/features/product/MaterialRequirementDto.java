package com.projedata.autoflex.features.product;

public record MaterialRequirementDto(
    Long rawMaterialId,
    String name,
    Integer quantity
) {}
