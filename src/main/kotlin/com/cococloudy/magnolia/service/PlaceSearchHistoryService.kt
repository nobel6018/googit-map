package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.KeywordAndCountDTO
import com.cococloudy.magnolia.KeywordAndLastSearchedAtDTO
import com.cococloudy.magnolia.PlaceSearchHistoryRepository
import com.cococloudy.magnolia.QPlaceSearchHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class PlaceSearchHistoryService {

    @Autowired
    private lateinit var placeSearchHistoryRepository: PlaceSearchHistoryRepository

    @Autowired
    private lateinit var qPlaceSearchHistoryRepository: QPlaceSearchHistoryRepository

    fun getPlaceSearchHistories(accountId: Long, uniqueKeyword: Boolean): List<Any> {
        return if (!uniqueKeyword) {
            val placeSearchHistories = placeSearchHistoryRepository.findAllByAccountIdOrderByIdDesc(accountId)

            placeSearchHistories.map { it.toDTO() }
        } else {
            return qPlaceSearchHistoryRepository.findUniqueKeywordAndLastCreatedAtOrderByLastCreatedAtDesc(accountId)
                .map {
                    KeywordAndLastSearchedAtDTO(
                        keyword = it.get(0, String::class.java)!!,
                        lastCreatedAt = it.get(1, OffsetDateTime::class.java)!!
                    )
                }
        }
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