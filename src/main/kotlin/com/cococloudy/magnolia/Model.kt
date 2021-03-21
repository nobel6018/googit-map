package com.cococloudy.magnolia

import java.time.OffsetDateTime
import javax.persistence.*

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
    val accountId: String,
    val password: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = null,
) {
    fun toDTO() = AccountDTO(
        id = id,
        accountId = accountId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

enum class Role {
    USER, ADMIN
}

