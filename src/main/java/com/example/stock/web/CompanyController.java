package com.example.stock.web;

import com.example.stock.model.Company;
import com.example.stock.model.constants.CacheKey;
import com.example.stock.persist.entity.CompanyEntity;
import com.example.stock.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
            @RequestParam String keyword
    ) {
//        var result = this.companyService.autocomplete(keyword);
        // trie 적용하면 db가 아닌 메모리에 추가 저장 ,
        // like query 문 db에 부하를 줄 가능성이 있다.
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    /**
     * 회사 및 배당금 정보 추가
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(
            @RequestBody Company request
            ){
        String ticker = request.getTicker().trim();

        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(
            @PathVariable String ticker
    ){
        String companyName = this.companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
