package com.cococloudy.magnolia

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoPlaceDTO(
    val id: String,
    val placeName: String,
    val categoryName: String,
    val categoryGroupCode: String,
    val categoryGroupName: String,
    val phone: String,
    val addressName: String,
    val roadAddressName: String,
    val x: String,
    val y: String,
    val placeUrl: String,
    val distance: String?,
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoPlaceMeta(
    val totalCount: Long,
    val pageableCount: Long,
    val isEnd: Boolean,
    val sameName: KakaoSameNameDTO,
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoSameNameDTO(
    val region: List<Any>,
    val keyword: String,
    val selectedRegion: String
)

data class NaverPlaceDTO(
    var title: String,
    val link: String,
    val category: String,
    val description: String,
    val telephone: String,
    val address: String,
    val roadAddress: String,
    val mapx: String,
    val mapy: String,
)