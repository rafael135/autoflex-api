package com.projedata.autoflex.infrastructure.repositories;

import java.util.List;

import com.projedata.autoflex.domain.ProductMaterial;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMaterialRepository implements PanacheRepository<ProductMaterial> {
    public List<ProductMaterial> findByProductId(Long productId) {
        return this.find("product.id", productId).list();
    }
}
