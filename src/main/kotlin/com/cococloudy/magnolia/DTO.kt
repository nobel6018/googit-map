package com.cococloudy.magnolia

import java.time.OffsetDateTime

data class AccountDTO(
    val id: Long? = null,
    val accountId: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = null,
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