package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.*
import com.cococloudy.magnolia.service.AccountService
import com.cococloudy.magnolia.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(produces = ["application/json"])
class AccountController(
    private val authService: AuthService,
    private val accountService: AccountService,
) {

    @GetMapping("/api/v1/account/isUserIdExist")
    fun isAccountIdExist(
        @RequestParam userId: String
    ): ResponseEntity<Boolean> {
        val isUserIdExist = accountService.isUserIdExist(userId)

        return ResponseEntity.ok(isUserIdExist)
    }

    @PostMapping("/api/v1/signUp")
    fun singUpAsUserRole(
        @RequestBody authInfo: AuthInfoDTO,
    ): ResponseEntity<AccessAndRefreshTokenDTO> {
        if (accountService.isUserIdExist(authInfo.accountId)) {
            throw WrongRequestException("AccountId ${authInfo.accountId} already exists")
        }
        accountService.throwIfPasswordIsWeak(authInfo.password)

        val createdAccount = accountService.createAccount(authInfo.accountId, authInfo.password, Role.USER)

        val accessAndRefreshToken = authService.createAccessAndRefreshToken(createdAccount.id)

        return ResponseEntity.ok(accessAndRefreshToken)
    }

    @PostMapping("/api/v1/login")
    fun login(
        @RequestBody authInfo: AuthInfoDTO,
    ): ResponseEntity<AccessAndRefreshTokenDTO> {
        val accessAndRefreshToken = accountService.loginByIdAndPassword(authInfo.accountId, authInfo.password)

        return ResponseEntity.ok(accessAndRefreshToken)
    }

    @PostMapping("/api/v1/refreshToken")
    fun refreshToken(
        @RequestBody refreshToken: RefreshTokenDTO,
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<AccessAndOptionalRefreshTokenDTO> {
        val accessAndOptionalRefreshToken = authService.refreshToken(refreshToken.refreshToken)

        return ResponseEntity.ok(accessAndOptionalRefreshToken)
    }
}