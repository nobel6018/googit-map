package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.AccessAndRefreshTokenDTO
import com.cococloudy.magnolia.AuthInfoDTO
import com.cococloudy.magnolia.Role
import com.cococloudy.magnolia.WrongRequestException
import com.cococloudy.magnolia.service.AccountService
import com.cococloudy.magnolia.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/account", produces = ["application/json"])
class AccountController(
    val authService: AuthService,
    val accountService: AccountService,
) {

    @GetMapping("/isAccountIdExist")
    fun isAccountIdExist(
        @RequestParam accountId: String
    ): ResponseEntity<Boolean> {
        val isAccountIdExist = accountService.isAccountIdExist(accountId)

        return ResponseEntity.ok(isAccountIdExist)
    }

    @PostMapping("/signUp")
    fun singUpAsUserRole(
        @RequestBody authInfo: AuthInfoDTO,
    ): ResponseEntity<AccessAndRefreshTokenDTO> {
        if (accountService.isAccountIdExist(authInfo.accountId)) {
            throw WrongRequestException("AccountId ${authInfo.accountId} already exists")
        }
        accountService.throwIfPasswordIsWeak(authInfo.password)

        val createdAccount = accountService.createAccount(authInfo.accountId, authInfo.password, Role.USER)

        val accessAndRefreshToken = authService.createAccessAndRefreshToken(createdAccount.id!!)

        return ResponseEntity.ok(accessAndRefreshToken)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody authInfo: AuthInfoDTO,
    ): ResponseEntity<AccessAndRefreshTokenDTO> {
        val accessAndRefreshToken = accountService.loginByIdAndPassword(authInfo.accountId, authInfo.password)

        return ResponseEntity.ok(accessAndRefreshToken)
    }
}