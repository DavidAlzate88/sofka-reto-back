package com.reto.sofka.backend.repository;

import com.reto.sofka.backend.document.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDAO extends ReactiveMongoRepository<Product, String> {
}
