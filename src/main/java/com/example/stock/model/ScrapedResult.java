package com.example.stock.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor // 모든필드 초기화 생성자 어노테이션
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividendEntities;
    public ScrapedResult(){
        this.dividendEntities = new ArrayList<>();
    }
}
