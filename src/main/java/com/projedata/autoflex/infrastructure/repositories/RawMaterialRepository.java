package com.projedata.autoflex.infrastructure.repositories;

import java.util.HashMap;
import java.util.List;

import com.projedata.autoflex.domain.RawMaterial;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {
    public HashMap<Long, Integer> getTotalMaterialsInStock() {
        List<RawMaterial> materials = this.findAll().stream().toList();
        HashMap<Long, Integer> totalMaterials = new HashMap<>();

        for (RawMaterial material : materials) {
            Long materialId = material.id;
            Integer quantity = material.stockQuantity;

            totalMaterials.put(materialId, quantity);
        }

        return totalMaterials;
    }
}
