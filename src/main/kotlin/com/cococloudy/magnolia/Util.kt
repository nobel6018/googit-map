package com.cococloudy.magnolia

fun Long.Day(): Long {
    return this.times(3600 * 24)
}