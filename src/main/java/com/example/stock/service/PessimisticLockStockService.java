package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * PessimisticLock
     * 장점: 1. 충돌이 빈번하게 일어난다면 Optimistic Lock보다 성능이 좋을 수 있음
     *         2. Lock을 통하여 업데이트를 제어하기에 데이터의 정합성이 보장된다.
     * 단점:  1. 별도의 Lock을 잡기때문에 성능 감소가 있을 수 있음.
     */
    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        stock.decrease(quantity);

        stockRepository.save(stock);
    }

}
