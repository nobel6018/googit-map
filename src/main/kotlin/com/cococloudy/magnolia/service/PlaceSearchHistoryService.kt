package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.KeywordAndCountDTO
import com.cococloudy.magnolia.PlaceSearchHistoryDTO
import com.cococloudy.magnolia.PlaceSearchHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceSearchHistoryService {

    @Autowired
    private lateinit var placeSearchHistoryRepository: PlaceSearchHistoryRepository

    fun getPlaceSearchHistories(accountId: Long): List<PlaceSearchHistoryDTO> {
        val placeSearchHistories = placeSearchHistoryRepository.findAllByAccountIdOrderByIdDesc(accountId)

        return placeSearchHistories.map { it.toDTO() }
    }

    fun getFrequentPlaceSearchKeywords(limit: Long): List<KeywordAndCountDTO> {
        return placeSearchHistoryRepository.findTopNByFrequencyOrderByFrequency(limit)
    }
}