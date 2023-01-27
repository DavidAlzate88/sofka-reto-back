package com.reto.sofka.backend.service;

import com.reto.sofka.backend.document.Buy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BuyService {
    Flux<Buy> findAll();
    Mono<Buy> findById(String id);
    Mono<Buy> save(Buy buy);
    Mono<Void> delete(Buy buy);
}
