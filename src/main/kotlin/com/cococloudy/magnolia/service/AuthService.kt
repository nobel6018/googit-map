package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.AccessAndRefreshTokenDTO
import com.cococloudy.magnolia.AccessTokenDTO
import com.cococloudy.magnolia.Day
import com.cococloudy.magnolia.security.JwtService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class AuthService(
    private val jwtService: JwtService,
) {

    fun createAccessAndRefreshToken(accountId: Long): AccessAndRefreshTokenDTO {
        return AccessAndRefreshTokenDTO(
            accessToken = jwtService.createAccessToken(accountId),
            refreshToken = jwtService.createRefreshToken(accountId)
        )
    }

    fun refreshToken(refreshToken: String): Any {
        jwtService.checkRefreshTokenValid(refreshToken)

        val jwtParser = jwtService.getJwtParser()
        val body = jwtParser.parseClaimsJws(refreshToken).body
        val isOverThreeDays = (body["exp"].toString().toLong() - System.currentTimeMillis() / 1000) > 3L.Day()
        val accountId = body["sub"].toString().toLong()

        return if (isOverThreeDays) {
            refreshTokenGraterThanThreeDays(accountId)
        } else {
            refreshTokenLessOrEqualThreeDays(accountId)
        }
    }

    private fun refreshTokenGraterThanThreeDays(accountId: Long): AccessTokenDTO {
        return AccessTokenDTO(jwtService.createAccessToken(accountId))
    }

    private fun refreshTokenLessOrEqualThreeDays(accountId: Long): AccessAndRefreshTokenDTO {
        return AccessAndRefreshTokenDTO(
            jwtService.createAccessToken(accountId),
            jwtService.createRefreshToken(accountId)
        )
    }
}