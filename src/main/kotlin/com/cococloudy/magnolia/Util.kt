package com.cococloudy.magnolia

import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper

fun SecurityContextHolderAwareRequestWrapper.extractAccountId(): Long {
    return this.userPrincipal.name.toLong()
}

fun Long.Day(): Long {
    return this.times(3600 * 24)
}