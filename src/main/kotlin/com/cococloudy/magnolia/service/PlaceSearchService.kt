package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PlaceSearchService {

    @Value("\${kakao.restApiKey}")
    private lateinit var kakaoRestApiKey: String

    @Value("\${kakao.localApi.url}")
    private lateinit var kakaoLocalApiUrl: String

    @Value("\${naver.clientId}")
    private lateinit var naverClientId: String

    @Value("\${naver.clientSecret}")
    private lateinit var naverClientSecret: String

    @Value("\${naver.localApi.url}")
    private lateinit var naverLocalApiUrl: String

    @Autowired
    private lateinit var placeSearchHistoryRepository: PlaceSearchHistoryRepository

    @Autowired
    private lateinit var okHttpClient: OkHttpClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Transactional
    fun searchPlaceWithLogging(accountId: Long, keyword: String): PlaceSearchResultDTO {
        createPlaceSearchHistory(accountId, keyword)

        return searchPlace(keyword)
    }

    @Transactional
    fun createPlaceSearchHistory(accountId: Long, keyword: String) {
        val searchHistory = PlaceSearchHistory(
            accountId = accountId,
            keyword = keyword
        )

        placeSearchHistoryRepository.save(searchHistory)
    }

    private fun searchPlace(keyword: String): PlaceSearchResultDTO {
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