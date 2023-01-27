package com.reto.sofka.backend.service;

import com.reto.sofka.backend.document.Product;
import com.reto.sofka.backend.repository.ProductDAO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Flux<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public Mono<Product> findById(String id) {
        return productDAO.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productDAO.delete(product);
    }
}
