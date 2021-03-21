package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.KeywordAndCountDTO
import com.cococloudy.magnolia.PlaceSearchHistoryDTO
import com.cococloudy.magnolia.extractAccountId
import com.cococloudy.magnolia.service.PlaceSearchHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/histories", produces = ["application/json"])
class PlaceSearchHistoryController {

    @Autowired
    private lateinit var placeSearchHistoryService: PlaceSearchHistoryService

    @GetMapping("/me/placeSearch")
    fun getMyPlaceSearchHistories(
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<List<PlaceSearchHistoryDTO>> {
        val accountId = request.extractAccountId()

        val myPlaceSearchHistories = placeSearchHistoryService.getPlaceSearchHistories(accountId)

        return ResponseEntity.ok(myPlaceSearchHistories)
    }

    @GetMapping("/placeSearch/top10")
    fun getTop10PlaceSearchHistories(
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<List<KeywordAndCountDTO>> {
        val frequentPlaceSearchKeywords = placeSearchHistoryService.getFrequentPlaceSearchKeywords(10)

        return ResponseEntity.ok(frequentPlaceSearchKeywords)
    }
}