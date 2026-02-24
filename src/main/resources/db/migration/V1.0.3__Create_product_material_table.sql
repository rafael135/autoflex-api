CREATE TABLE product_material (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    product_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    required_quantity INT NOT NULL,
    CONSTRAINT fk_product_material_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_product_material_raw_material
        FOREIGN KEY (raw_material_id) REFERENCES raw_material(id),

    CONSTRAINT unique_product_material
        UNIQUE (product_id, raw_material_id)
);