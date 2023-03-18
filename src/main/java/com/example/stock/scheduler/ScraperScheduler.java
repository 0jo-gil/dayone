package com.example.stock.scheduler;

import com.example.stock.model.Company;
import com.example.stock.model.ScrapedResult;
import com.example.stock.model.constants.CacheKey;
import com.example.stock.persist.CompanyRepository;
import com.example.stock.persist.DividendRepository;
import com.example.stock.persist.entity.CompanyEntity;
import com.example.stock.persist.entity.DividendEntity;
import com.example.stock.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final YahooFinanceScraper yahooFinanceScraper;

//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException{
//        Thread.sleep(10000);
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 1: " + LocalDateTime.now());
//    }
//    @Scheduled(fixedDelay = 1000)
//    public void test2() {
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 2: " + LocalDateTime.now());
//    }

    // spring batch 사용 방법 확인 -> 많은 수의 대용량 데이터 레코드를 처리해주는 ..

    //일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started!");
        // 지정된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();
        // 회사마다 배당금 정보를 새로 스크래핑
        for(var company : companies){
            log.info("scraping scheduler is started! - " + company.getName());

            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                                        new Company(company.getTicker(), company.getName())
            );

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값 저장
            scrapedResult.getDividendEntities().stream()
                    // 디비든 모델을 디비든 엔티티로 맵핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists){
                            this.dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });


            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
            }
        }


    }
}
