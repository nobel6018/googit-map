package com.cococloudy.magnolia

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