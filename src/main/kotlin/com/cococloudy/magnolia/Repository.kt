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
    fun findByUserId(userId: String): Account?
}

@Repository
interface PlaceSearchHistoryRepository : CrudRepository<PlaceSearchHistory, Long> {
    fun findAllByAccountIdOrderByIdDesc(accountId: Long): List<PlaceSearchHistory>
}

@Repository
class QPlaceSearchHistoryRepository(
    private val query: JPAQueryFactory
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

    fun findUniqueKeywordAndLastCreatedAtOrderByLastCreatedAtDesc(accountId: Long): List<Tuple> {
        return query
            .select(qPlaceSearchHistory.keyword, qPlaceSearchHistory.createdAt.max())
            .from(qPlaceSearchHistory)
            .where(qPlaceSearchHistory.account.id.eq(accountId))
            .groupBy(qPlaceSearchHistory.keyword)
            .orderBy(qPlaceSearchHistory.createdAt.max().desc())
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