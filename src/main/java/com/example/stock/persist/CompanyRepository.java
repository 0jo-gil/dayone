package com.example.stock.persist;

import com.example.stock.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name); // null point exception

    Optional<CompanyEntity> findByTicker(String ticker);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
