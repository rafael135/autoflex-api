package com.projedata.autoflex.infrastructure;

import java.math.BigDecimal;
import java.util.List;

import com.projedata.autoflex.domain.Product;
import com.projedata.autoflex.domain.RawMaterial;
import com.projedata.autoflex.infrastructure.repositories.ProductMaterialRepository;
import com.projedata.autoflex.infrastructure.repositories.ProductRepository;
import com.projedata.autoflex.infrastructure.repositories.RawMaterialRepository;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DatabaseSeeder {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMaterialRepository productMaterialRepository;
    public DatabaseSeeder(ProductRepository productRepository, ProductMaterialRepository productMaterialRepository, RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.productMaterialRepository = productMaterialRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }
    
    @Transactional
    public void seedDatabase(@Observes StartupEvent event) {

        // Prevent duplicate seeding
        if(productRepository.count() > 0 || rawMaterialRepository.count() > 0 || productMaterialRepository.count() > 0) {
            return;
        }


        RawMaterial screws = RawMaterial.create("Screw", 1000);
        RawMaterial woodBoard = RawMaterial.create("Wood Board", 50);
        RawMaterial steelPipe = RawMaterial.create("Steel Pipe", 30);
        
        this.rawMaterialRepository.persist(List.of(screws, woodBoard, steelPipe));

        Product chair = Product.create("Chair", new BigDecimal(200.0));
        chair.addMaterial(woodBoard, 1);
        chair.addMaterial(screws, 4);

        Product table = Product.create("Table", new BigDecimal(1000.0));
        table.addMaterial(woodBoard, 2);
        table.addMaterial(screws, 8);

        Product shelf = Product.create("Shelf", new BigDecimal(600.0));
        shelf.addMaterial(woodBoard, 5);
        shelf.addMaterial(screws, 10);

        this.productRepository.persist(List.of(chair, table, shelf));


    }
}
