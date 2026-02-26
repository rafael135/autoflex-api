package com.projedata.autoflex.features.product;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.ProductMaterial;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ProductMapper {
    
    ProductDto toDto(Product entity);

    List<ProductDto> toDtoList(List<Product> entities);

    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "name", source = "rawMaterial.name")
    @Mapping(target = "quantity", source = "requiredQuantity")
    MaterialRequirementDto toMaterialRequirementDto(ProductMaterial productMaterial);
}