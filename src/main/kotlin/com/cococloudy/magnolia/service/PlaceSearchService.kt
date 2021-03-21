package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.KakaoLocalApiResponseDTO
import com.cococloudy.magnolia.NaverLocalApiResponseDTO
import com.cococloudy.magnolia.SearchHistoryRepository
import com.cococloudy.magnolia.WrongRequestException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

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
    private lateinit var searchHistoryRepository: SearchHistoryRepository

    @Autowired
    private lateinit var okHttpClient: OkHttpClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    fun searchPlace(accountId: Long, keyword: String): Any {
        val kakaoResponse = callKakaoLocalApi(keyword)
        val naverResponse = callNaverLocalApi(keyword)

        TODO()
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

        return objectMapper.readValue(response.body!!.string())
    }
}