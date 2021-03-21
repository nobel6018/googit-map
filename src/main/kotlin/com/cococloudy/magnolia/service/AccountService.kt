package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.*
import com.cococloudy.magnolia.security.BCryptPasswordEncoder
import com.cococloudy.magnolia.security.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AccountService {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var encoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtService: JwtService

    fun isAccountIdExist(accountId: String): Boolean {
        return accountRepository.findByAccountId(accountId) != null
    }

    @Transactional
    fun createAccount(accountId: String, password: String, role: Role): AccountDTO {
        val account = Account(
            accountId = accountId,
            password = encoder.encode(password),
            role = role,
        )
        val createdAccount = accountRepository.save(account)

        return createdAccount.toDTO()
    }

    fun loginByIdAndPassword(accountId: String, password: String): AccessAndRefreshTokenDTO {
        val account = accountRepository.findByAccountId(accountId)
            ?: throw NotFoundException("Account", accountId)

        if (!encoder.matches(password, account.password)) {
            throw WrongRequestException("Account id and password doesn't match")
        }

        return AccessAndRefreshTokenDTO(
            jwtService.createAccessToken(account.id!!),
            jwtService.createRefreshToken(account.id)
        )
    }
}