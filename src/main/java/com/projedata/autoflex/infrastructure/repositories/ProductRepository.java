package com.projedata.autoflex.infrastructure.repositories;

import com.projedata.autoflex.domain.Product;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    

    public PanacheQuery<Product> findAllDescending() {
        return this.findAll(Sort.by("id").descending());
    }
}
