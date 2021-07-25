package com.cococloudy.magnolia

import java.time.OffsetDateTime
import javax.persistence.*

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    val id: Long? = null,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
    val userId: String,
    val password: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = null,
) {
    fun toDTO() = AccountDTO(
        id = id!!,
        userId = userId,
        createdAt = createdAt,
    )
}

enum class Role {
    USER, ADMIN
}

@Entity
data class PlaceSearchHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_search_history_id")
    val id: Long? = null,
    val accountId: Long,  // fk (not to constraint fk, deal with logic)
    val keyword: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = null,
) {
    fun toDTO() = PlaceSearchHistoryDTO(
        id = id!!,
        accountId = accountId,
        keyword = keyword,
        createdAt = createdAt,
    )
}

@Entity
data class PlaceSearchCache(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_search_cache_id")
    val id: Long? = null,
    val initial: Boolean,
    val nodeId: String,
    val nextNodeId: String,  // if end node, ""
    val keyword: String,
    val placeName: String,
    val phone: String,
    val roadAddressName: String,
    val placeUrl: String,
    var createdEpochTime: Long = System.currentTimeMillis(),  // Cache is internal logic. Works like TTL
) {
    fun toPlaceDTO() = PlaceDTO(
        placeName = placeName,
        phone = phone,
        roadAddressName = roadAddressName,
        placeUrl = placeUrl,
    )
}