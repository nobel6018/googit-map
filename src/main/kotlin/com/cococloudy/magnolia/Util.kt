package com.cococloudy.magnolia

import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import kotlin.random.Random

fun SecurityContextHolderAwareRequestWrapper.extractAccountId(): Long {
    return this.userPrincipal.name.toLong()
}

fun Long.Day(): Long {
    return this.times(3600 * 24)
}

fun randomString(length: Int = 11): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}