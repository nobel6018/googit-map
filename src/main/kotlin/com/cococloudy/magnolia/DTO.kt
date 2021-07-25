package com.cococloudy.magnolia

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.OffsetDateTime

data class AccountDTO(
    val id: Long?,
    val accountId: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
)

data class AuthInfoDTO(
    val accountId: String,
    val password: String
)

data class AccessTokenDTO(
    val accessToken: String
)

data class RefreshTokenDTO(
    val refreshToken: String
)

data class AccessAndRefreshTokenDTO(
    val accessToken: String,
    val refreshToken: String
)

data class PlaceSearchHistoryDTO(
    val id: Long?,
    val accountId: Long,
    val keyword: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
)

data class PlaceSearchResultDTO(
    val places: List<PlaceDTO>,
    val totalCount: Int,
)

data class PlaceDTO(
    val placeName: String,
    val phone: String,
    val roadAddressName: String,
    val placeUrl: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoLocalApiResponseDTO(
    val documents: List<KakaoPlaceDTO>,
    val meta: KakaoPlaceMeta
)

data class NaverLocalApiResponseDTO(
    val lastBuildDate: String,
    val total: Long,
    val start: Long,
    val display: Long,
    val items: List<NaverPlaceDTO>
)

data class KeywordAndCountDTO(
    val keyword: String,
    val count: Long
)

data class KeywordAndLastSearchedAtDTO(
    val keyword: String,
    val lastCreatedAt: OffsetDateTime
)