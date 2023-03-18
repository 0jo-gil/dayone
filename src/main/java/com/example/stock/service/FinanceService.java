package com.example.stock.service;

import com.example.stock.exception.impl.NoCompanyException;
import com.example.stock.model.Company;
import com.example.stock.model.Dividend;
import com.example.stock.model.ScrapedResult;
import com.example.stock.model.constants.CacheKey;
import com.example.stock.persist.CompanyRepository;
import com.example.stock.persist.DividendRepository;
import com.example.stock.persist.entity.CompanyEntity;
import com.example.stock.persist.entity.DividendEntity;
import com.example.stock.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는지 생각~

    // 자주 변경되는 데이터 인가?
    // Cacheable 은 캐시 데이터가 없을경우 메소드를 실행시키고 캐싱을 하고 개싱된 데이터가 있을경우 캐시에 있는 데이터를 가져온다.
    @Cacheable(key = "#companyName" , value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company -> " + companyName);
        // 1. 회사명 기준 회사정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());


        // 3. 결과 조합 후 반환
//      List<Dividend> dividends = new ArrayList<>();
//
//      for(var entitiy : dividendEntities){
//          dividends.add(
//                  Dividend.builder()
//                          .date(entitiy.getDate())
//                          .dividend(entitiy.getDividend())
//                          .build());
//      }

        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());


        return new ScrapedResult(
                new Company(company.getTicker(), company.getName()),
                dividends
        );
    }
}
