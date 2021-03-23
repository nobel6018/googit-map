package com.cococloudy.magnolia

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

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