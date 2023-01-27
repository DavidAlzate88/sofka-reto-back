package com.reto.sofka.backend.controller;

import com.reto.sofka.backend.document.Product;
import com.reto.sofka.backend.repository.ProductDAO;
import com.reto.sofka.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> productList() {
        return Mono.just(
                ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productService.findAll())
        );
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createProduct(@Valid @RequestBody Mono<Product> monoProduct) {
        Map<String, Object> response = new HashMap<>();
        return monoProduct.flatMap(product -> productService.save(product).map(p -> {
                    response.put("product", p);
                    response.put("mensaje", "Product created successfully");
                    response.put("timestamp", new Date());

                    return ResponseEntity
                            .created(URI.create("/api/products/".concat(p.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                })
        ).onErrorResume(t -> Mono.just(t)
                .cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "The field: " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(list -> {
                    response.put("product", list);
                    response.put("timestamp", new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());

                    return Mono.just(ResponseEntity.badRequest().body(response));
                }));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> findProductById(@PathVariable String id) {
        return productService.findById(id)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@RequestBody Product product, @PathVariable String id) {
        return productService.findById(id)
                .flatMap(p -> {
                    p.setName(product.getName());
                    p.setInInventory(product.getInInventory());
                    p.setEnabled(product.getEnabled());
                    p.setMin(product.getMin());
                    p.setMax(product.getMax());
                    return productService.save(p);
                }).map(p -> ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
        return productService.findById(id)
                .flatMap(p -> productService
                        .delete(p)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                )
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
