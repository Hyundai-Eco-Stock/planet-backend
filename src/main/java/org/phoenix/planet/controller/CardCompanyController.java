package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.card.response.CardCompanyListResponse;
import org.phoenix.planet.service.card.CardCompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/card-companies")
public class CardCompanyController {

    private final CardCompanyService cardCompanyService;

    @GetMapping
    public ResponseEntity<List<CardCompanyListResponse>> searchAllCardCompanyList() {

        List<CardCompanyListResponse> cardCompanyList = cardCompanyService.searchAll();
        return ResponseEntity.ok(cardCompanyList);
    }
}
