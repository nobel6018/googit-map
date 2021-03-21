package com.cococloudy.magnolia.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtConfig {

    @Value("\${security.jwt.header}")
    lateinit var header: String

    @Value("\${security.jwt.prefix}")
    lateinit var prefix: String

    @Value("\${security.jwt.expiration:#{15*60}}")
    var expiration: Int = 0  // 15 min

    @Value("\${security.jwt.refreshTokenExpiration:#{24*60*60*14}}")
    var refreshTokenExpiration: Int = 0  // 2 weeks

}