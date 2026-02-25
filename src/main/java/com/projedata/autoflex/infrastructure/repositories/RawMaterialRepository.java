package com.projedata.autoflex.infrastructure.repositories;

import com.projedata.autoflex.domain.RawMaterial;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {
    
}
