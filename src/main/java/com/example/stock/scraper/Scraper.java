package com.example.stock.scraper;

import com.example.stock.model.Company;
import com.example.stock.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
