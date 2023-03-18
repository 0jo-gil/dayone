package com.example.stock;

import com.example.stock.model.Company;
import com.example.stock.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class StockApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);

        System.out.println("Main -> " + Thread.currentThread().getName());
    }

}
