package com.cococloudy.magnolia

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CrudRepository<Account, Long> {
    fun findByAccountId(accountId: String): Account?
}

@Repository
interface PlaceSearchHistoryRepository : CrudRepository<PlaceSearchHistory, Long> {
    fun findAllByAccountIdOrderByIdDesc(accountId: Long): List<PlaceSearchHistory>

    @Query(
        "SELECT keyword, count(*) as count FROM place_search_history GROUP BY keyword ORDER BY count DESC LIMIT ?1 ;",
        nativeQuery = true
    )
    fun findTopNByFrequencyOrderByFrequency(limit: Long): List<KeywordAndCountDTO>
}