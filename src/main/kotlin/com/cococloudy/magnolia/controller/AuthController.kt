package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.RefreshTokenDTO
import com.cococloudy.magnolia.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/auth", produces = ["application/json"])
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/refreshToken")
    fun refreshToken(
        @RequestBody refreshToken: RefreshTokenDTO,
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<Any> {
        val accessTokenOrAccessAndRefreshToken = authService.refreshToken(refreshToken.refreshToken)

        return ResponseEntity.ok(accessTokenOrAccessAndRefreshToken)
    }

}
