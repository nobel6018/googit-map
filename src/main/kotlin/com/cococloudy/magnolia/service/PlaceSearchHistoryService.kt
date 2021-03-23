package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.KeywordAndCountDTO
import com.cococloudy.magnolia.PlaceSearchHistoryDTO
import com.cococloudy.magnolia.PlaceSearchHistoryRepository
import com.cococloudy.magnolia.QPlaceSearchHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceSearchHistoryService {

    @Autowired
    private lateinit var placeSearchHistoryRepository: PlaceSearchHistoryRepository

    @Autowired
    private lateinit var qPlaceSearchHistoryRepository: QPlaceSearchHistoryRepository

    fun getPlaceSearchHistories(accountId: Long): List<PlaceSearchHistoryDTO> {
        val placeSearchHistories = placeSearchHistoryRepository.findAllByAccountIdOrderByIdDesc(accountId)

        return placeSearchHistories.map { it.toDTO() }
    }

    fun getFrequentPlaceSearchKeywords(limit: Long): List<KeywordAndCountDTO> {
        return qPlaceSearchHistoryRepository.findKeywordAndCount(limit)
            .map {
                KeywordAndCountDTO(
                    keyword = it.get(0, String::class.java)!!,
                    count = it.get(1, Long::class.java)!!
                )
            }
    }
}