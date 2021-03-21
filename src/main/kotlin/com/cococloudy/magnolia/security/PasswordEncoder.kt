package com.cococloudy.magnolia.security

import org.apache.commons.logging.LogFactory
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.regex.Pattern

/**
 * @param strength the log rounds to use, between 4 and 31
 * @param random the secure random instance to use
 */
@Component
class BCryptPasswordEncoder
@JvmOverloads constructor(private val strength: Int = -1, private val random: SecureRandom? = null) :
    PasswordEncoder {
    private val BCRYPT_PATTERN = Pattern
        .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}")
    private val logger = LogFactory.getLog(javaClass)
    private val MIN_LOG_ROUNDS = 4
    private val MAX_LOG_ROUNDS = 31

    init {
        if (strength != -1 && (strength < MIN_LOG_ROUNDS || strength > MAX_LOG_ROUNDS)) {
            throw IllegalArgumentException("Bad strength")
        }
    }

    override fun encode(rawPassword: CharSequence): String {
        val salt: String = if (strength > 0) {
            if (random != null) {
                BCrypt.gensalt(strength, random)
            } else {
                BCrypt.gensalt(strength)
            }
        } else {
            BCrypt.gensalt()
        }
        return BCrypt.hashpw(rawPassword.toString(), salt)
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String?): Boolean {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            logger.warn("Empty encoded password")
            return false
        }

        if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            logger.warn("Encoded password does not look like BCrypt")
            return false
        }

        return BCrypt.checkpw(rawPassword.toString(), encodedPassword)
    }
}