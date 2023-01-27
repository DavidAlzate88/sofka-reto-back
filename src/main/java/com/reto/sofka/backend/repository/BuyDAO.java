package com.reto.sofka.backend.repository;

import com.reto.sofka.backend.document.Buy;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BuyDAO extends ReactiveMongoRepository<Buy, String> {
}
