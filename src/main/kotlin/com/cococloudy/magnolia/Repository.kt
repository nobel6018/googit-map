package com.cococloudy.magnolia

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface AccountRepository : CrudRepository<Account, Long> {
    fun findByAccountId(accountId: String): Account?
}

@Repository
interface PlaceSearchHistoryRepository : CrudRepository<PlaceSearchHistory, Long> {
    fun findAllByAccountIdOrderByIdDesc(accountId: Long): List<PlaceSearchHistory>
}

@Repository
class QPlaceSearchHistoryRepository(
    val query: JPAQueryFactory
) {
    private val qPlaceSearchHistory = QPlaceSearchHistory.placeSearchHistory

    fun findKeywordAndCount(limit: Long): List<Tuple> {
        return query
            .select(qPlaceSearchHistory.keyword, qPlaceSearchHistory.keyword.count())
            .from(qPlaceSearchHistory)
            .groupBy(qPlaceSearchHistory.keyword)
            .orderBy(qPlaceSearchHistory.keyword.count().desc())
            .limit(limit)
            .fetch()
    }
}

@Repository
interface PlaceSearchCacheRepository : CrudRepository<PlaceSearchCache, Long> {
    fun findAllByKeywordAndCreatedEpochTimeAfter(keyword: String, createdEpochTime: Long): List<PlaceSearchCache>
    fun findAllByKeyword(keyword: String): List<PlaceSearchCache>

    @Transactional
    @Modifying
    @Query("DELETE FROM PlaceSearchCache cache WHERE cache.keyword=:keyword")
    fun deleteAllByKeyword(@Param("keyword") keyword: String)
}