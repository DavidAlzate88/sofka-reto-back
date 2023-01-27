package com.reto.sofka.backend.controller;

import com.reto.sofka.backend.document.Buy;
import com.reto.sofka.backend.service.BuyService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/buys")
public class BuyController {
    private final BuyService buyService;

    public BuyController(BuyService buyService) {
        this.buyService = buyService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Buy>>> buyList() {
        return Mono.just(
                ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(buyService.findAll())
        );
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createBuys(@Valid @RequestBody Mono<Buy> monoBuys) {
        Map<String, Object> response = new HashMap<>();
        return monoBuys.flatMap(buy -> {
            buy.setDate(new Date());
            return buyService.save(buy).map(b -> {
                response.put("buy", b);
                response.put("mensaje", "Buy created successfully");
                response.put("timestamp", new Date());

                    return ResponseEntity
                            .created(URI.create("/api/buys/".concat(b.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
            });
        }).onErrorResume(t -> Mono.just(t)
                .cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "The field: " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(list -> {
                    response.put("buy", list);
                    response.put("timestamp", new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());

                    return Mono.just(ResponseEntity.badRequest().body(response));
                }));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Buy>> findBuyById(@PathVariable String id) {
        return buyService.findById(id)
                .map(b -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(b))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Buy>> updateBuy(@RequestBody Buy buy, @PathVariable String id) {
        return buyService.findById(id)
                .flatMap(b -> {
                    b.setClientName(buy.getClientName());
                    b.setIdType(buy.getIdType());
                    b.setIdentification(buy.getIdentification());
                    b.setDate(new Date());
                    b.setProducts(buy.getProducts());
                    return buyService.save(b);
                }).map(b -> ResponseEntity.created(URI.create("/api/buys/".concat(b.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(b))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBuy(@PathVariable String id) {
        return buyService.findById(id)
                .flatMap(b -> buyService
                        .delete(b)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                )
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
