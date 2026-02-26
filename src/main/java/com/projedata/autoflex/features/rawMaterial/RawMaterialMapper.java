package com.projedata.autoflex.features.rawMaterial;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.projedata.autoflex.domain.RawMaterial;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RawMaterialMapper {
    /**
     * Converts a RawMaterial entity to a RawMaterialDto. This method takes a RawMaterial entity as input and maps its properties to a RawMaterialDto, which is a data transfer object used for transferring raw material data between different layers of the application.
     * @param entity the RawMaterial entity to be converted to a RawMaterialDto
     * @return a RawMaterialDto containing the mapped properties from the RawMaterial entity
     */
    RawMaterialDto toDto(RawMaterial entity);

    /**
     * Converts a list of RawMaterial entities to a list of RawMaterialDto. This method takes a list of RawMaterial entities as input and maps each entity to a RawMaterialDto, returning a list of RawMaterialDto objects that represent the raw material data in a format suitable for transfer between different layers of the application.
     * @param entities the list of RawMaterial entities to be converted to a list of RawMaterialDto
     * @return a list of RawMaterialDto containing the mapped properties from the list of RawMaterial entities
     */
    List<RawMaterialDto> toDtoList(List<RawMaterial> entities);
}
