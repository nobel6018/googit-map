package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.extractAccountId
import com.cococloudy.magnolia.service.PlaceSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search", produces = ["application/json"])
class PlaceSearchController {

    @Autowired
    private lateinit var placeSearchService: PlaceSearchService

    @GetMapping("/place")
    fun searchPlace(
        @RequestParam keyword: String,
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<Any> {
        val accountId = request.extractAccountId()

        val response = placeSearchService.searchPlace(accountId, keyword)

        return ResponseEntity.ok(response)
    }
}