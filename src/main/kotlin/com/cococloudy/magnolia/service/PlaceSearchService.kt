package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class PlaceSearchService(
    @Value("\${kakao.restApiKey}")
    private val kakaoRestApiKey: String,

    @Value("\${kakao.localApi.url}")
    private val kakaoLocalApiUrl: String,

    @Value("\${naver.clientId}")
    private val naverClientId: String,

    @Value("\${naver.clientSecret}")
    private val naverClientSecret: String,

    @Value("\${naver.localApi.url}")
    private val naverLocalApiUrl: String,

    private val accountRepository: AccountRepository,
    private val placeSearchHistoryRepository: PlaceSearchHistoryRepository,
    private val placeSearchCacheRepository: PlaceSearchCacheRepository,
    private val okHttpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,
) {

    val CACHE_VALID_MILLISECOND = 86_400_000L  // 24 hour

    @Transactional
    fun searchPlaceWithLogging(accountId: Long, keyword: String, forceRefresh: Boolean): PlaceSearchResultDTO {
        createPlaceSearchHistory(accountId, keyword)

        if (forceRefresh) {
            return searchPlacesAndCacheResults(keyword)
        }

        val cachedPlaceSearchResult = getPlaceSearchResultFromCache(keyword)

        return if (cachedPlaceSearchResult.totalCount > 0) {
            cachedPlaceSearchResult
        } else {
            searchPlacesAndCacheResults(keyword)
        }
    }

    @Transactional
    fun createPlaceSearchHistory(accountId: Long, keyword: String) {
        val account = accountRepository.findByIdOrNull(accountId) ?: throw NotFoundException("Account", accountId)
        val searchHistory = PlaceSearchHistory.createPlaceSearchHistory(account, keyword)

        placeSearchHistoryRepository.save(searchHistory)
    }

    private fun getPlaceSearchResultFromCache(keyword: String): PlaceSearchResultDTO {
        val placeSearchCaches = placeSearchCacheRepository.findAllByKeywordAndCreatedEpochTimeAfter(
            keyword,
            System.currentTimeMillis() - CACHE_VALID_MILLISECOND
        )

        if (placeSearchCaches.isEmpty()) {
            return PlaceSearchResultDTO(places = listOf(), totalCount = 0)
        }

        val sortedPlaceCaches = sortPlaceSearchCaches(placeSearchCaches)
        val places = sortedPlaceCaches.map { it.toPlaceDTO() }

        return PlaceSearchResultDTO(
            places = places,
            totalCount = places.size
        )
    }

    private fun sortPlaceSearchCaches(placeSearchCaches: List<PlaceSearchCache>): List<PlaceSearchCache> {
        val sortedPlaceSearchCaches = mutableListOf<PlaceSearchCache>()
        var placeSearchCache = placeSearchCaches.find { it.initial == true }
            ?: return listOf()

        sortedPlaceSearchCaches.add(placeSearchCache)

        if (placeSearchCaches.size > 1) {
            for (i in 2..placeSearchCaches.size) {
                placeSearchCache = placeSearchCaches.find { it.nodeId == placeSearchCache.nextNodeId }
                    ?: throw NotFoundException("PlaceSearchCache", "nextNodeId: ${placeSearchCache.nextNodeId}")
                sortedPlaceSearchCaches.add(placeSearchCache)
            }
        }

        return sortedPlaceSearchCaches
    }

    @Transactional
    fun searchPlacesAndCacheResults(keyword: String): PlaceSearchResultDTO {
        val placeSearchResult = searchPlaces(keyword)
        val places = placeSearchResult.places

        val placeSearchCaches = placeSearchCacheRepository.findAllByKeyword(keyword)
        val cachedPlaces = sortPlaceSearchCaches(placeSearchCaches).map { it.toPlaceDTO() }

        if (places == cachedPlaces) {
            val createdEpochTime = System.currentTimeMillis()
            placeSearchCaches.forEach { it.createdEpochTime = createdEpochTime }
            placeSearchCacheRepository.saveAll(placeSearchCaches)
        } else {
            placeSearchCacheRepository.deleteAllByKeyword(keyword)

            val nodeIds = (1..15).map { randomString() }
            val createdEpochTime = System.currentTimeMillis()
            val creatingPlaceSearchCaches = places.mapIndexed { index, place ->
                val isLastNode = places.size == (index + 1)
                val nextNodeId = if (isLastNode) "" else nodeIds[index + 1]

                PlaceSearchCache(
                    initial = (index == 0),
                    nodeId = nodeIds[index],
                    nextNodeId = nextNodeId,
                    keyword = keyword,
                    placeName = place.placeName,
                    phone = place.phone,
                    roadAddressName = place.roadAddressName,
                    placeUrl = place.placeUrl,
                    createdEpochTime = createdEpochTime,
                )
            }
            placeSearchCacheRepository.saveAll(creatingPlaceSearchCaches)
        }

        return placeSearchResult
    }

    private fun searchPlaces(keyword: String): PlaceSearchResultDTO {
        val kakaoPlaces = callKakaoLocalApi(keyword).documents
        val naverPlaces = callNaverLocalApi(keyword).items.toMutableList()

        val tempPlaceNames = mutableListOf<KakaoPlaceDTO>()

        val sortedPlaces = mutableListOf<PlaceDTO>()

        var matched: Boolean
        for (kakaoPlace in kakaoPlaces) {
            matched = false
            for ((j, naverPlace) in naverPlaces.withIndex()) {
                if (kakaoPlace.placeName.replace(" ", "") == naverPlace.title.replace(" ", "")) {
                    val place = PlaceDTO(
                        placeName = kakaoPlace.placeName,
                        phone = kakaoPlace.phone,
                        roadAddressName = kakaoPlace.roadAddressName,
                        placeUrl = kakaoPlace.placeUrl,
                    )
                    sortedPlaces.add(place)

                    naverPlaces.removeAt(j)
                    matched = true
                    break
                }
            }
            if (!matched) {
                tempPlaceNames.add(kakaoPlace)
            }
        }

        tempPlaceNames.forEach {
            val place = PlaceDTO(
                placeName = it.placeName,
                phone = it.phone,
                roadAddressName = it.roadAddressName,
                placeUrl = it.placeUrl,
            )
            sortedPlaces.add(place)
        }
        naverPlaces.forEach {
            val place = PlaceDTO(
                placeName = it.title,
                phone = it.telephone,
                roadAddressName = it.roadAddress,
                placeUrl = it.link,
            )
            sortedPlaces.add(place)
        }

        return PlaceSearchResultDTO(
            places = sortedPlaces,
            totalCount = sortedPlaces.size
        )
    }

    private fun callKakaoLocalApi(keyword: String): KakaoLocalApiResponseDTO {
        val request = Request.Builder()
            .url("${kakaoLocalApiUrl}?page=1&size=10&query=${keyword}")
            .header("Content-Type", "application/json")
            .header("Authorization", "KakaoAK $kakaoRestApiKey")
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw WrongRequestException("Kakao Local API call failed, response: ${response.body!!.string()}")
        }

        return objectMapper.readValue(response.body!!.string())
    }

    private fun callNaverLocalApi(keyword: String): NaverLocalApiResponseDTO {
        val request = Request.Builder()
            .url("${naverLocalApiUrl}?query=${keyword}&display=5")  // display parameter max value is 5
            .header("Content-Type", "application/json")
            .header("X-Naver-Client-Id", naverClientId)
            .header("X-Naver-Client-Secret", naverClientSecret)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw WrongRequestException("Naver Local API call failed, response: ${response.body!!.string()}")
        }

        val naverLocalApiResponse = objectMapper.readValue<NaverLocalApiResponseDTO>(response.body!!.string())
        naverLocalApiResponse.items.forEach { it.title = it.title.replace("<b>", "").replace("</b>", "") }

        return naverLocalApiResponse
    }
}