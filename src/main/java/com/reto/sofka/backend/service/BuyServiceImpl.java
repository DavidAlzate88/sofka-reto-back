package com.reto.sofka.backend.service;

import com.reto.sofka.backend.document.Buy;
import com.reto.sofka.backend.repository.BuyDAO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BuyServiceImpl implements BuyService {

    private final BuyDAO buyDAO;

    public BuyServiceImpl(BuyDAO buyDAO) {
        this.buyDAO = buyDAO;
    }

    @Override
    public Flux<Buy> findAll() {
        return buyDAO.findAll();
    }

    @Override
    public Mono<Buy> findById(String id) {
        return buyDAO.findById(id);
    }

    @Override
    public Mono<Buy> save(Buy buy) {
        return buyDAO.save(buy);
    }

    @Override
    public Mono<Void> delete(Buy buy) {
        return buyDAO.delete(buy);
    }
}
