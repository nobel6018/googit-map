package com.cococloudy.magnolia.security

import com.cococloudy.magnolia.AccountRepository
import com.cococloudy.magnolia.NotFoundException
import com.cococloudy.magnolia.WrongRequestException
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Component
class JwtService {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var magnoliaKeyPair: MagnoliaKeyPair

    @Autowired
    private lateinit var jwtConfig: JwtConfig

    fun createAccessToken(accountId: Long): String {
        val account = accountRepository.findById(accountId).orElseThrow { NotFoundException("account", accountId) }
        val now = System.currentTimeMillis()
        val expiredIn = now + jwtConfig.expiration * 1000
        val jti = UUID.randomUUID().toString()

        val jws = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "RS256")
            .setIssuer("Magnolia")
            .setSubject(accountId.toString())
            .setExpiration(Date(expiredIn))
            .setIssuedAt(Date(now))
            .setId(jti)
            .claim("role", account.role)
            .claim("type", "accessToken")
            .signWith(magnoliaKeyPair.getPrivateKey())
            .compact()

        return jws
    }

    fun createRefreshToken(accountId: Long): String {
        val account = accountRepository.findById(accountId).orElseThrow { NotFoundException("account", accountId) }
        val now = System.currentTimeMillis()
        val expiredIn = now + jwtConfig.refreshTokenExpiration * 1000
        val jti = UUID.randomUUID().toString()

        val jws = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "RS256")
            .setIssuer("Magnolia")
            .setSubject(accountId.toString())
            .setExpiration(Date(expiredIn))
            .setIssuedAt(Date(now))
            .setId(jti)
            .claim("role", account.role)
            .claim("type", "refreshToken")
            .signWith(magnoliaKeyPair.getPrivateKey())
            .compact()

        return jws
    }

    fun checkRefreshTokenValid(refreshToken: String) {
        val jwtParser = getJwtParser()

        try {
            val parsedJwt = jwtParser.parseClaimsJws(refreshToken)

            if (parsedJwt.body["type"] != "refreshToken") {
                throw WrongRequestException("Passed token is not refresh token")
            }
            if (parsedJwt.body["sub"] == null) {
                throw WrongRequestException("There is no subject")
            }
        } catch (unsupportedJwt: UnsupportedJwtException) {
            throw WrongRequestException("Unsupported Jwt")
        } catch (malformedJwt: MalformedJwtException) {
            throw WrongRequestException("Malformed Jwt")
        } catch (wrongSignature: SignatureException) {
            throw WrongRequestException("Wrong Signature")
        } catch (expiredJwt: ExpiredJwtException) {
            throw WrongRequestException("Expired Jwt")
        } catch (e: Exception) {
            throw e
        }
    }

    fun getJwtParser(): JwtParser {
        val publicKey = getPublicKey()

        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
    }

    private fun getPrivateKey(): PrivateKey {
        return magnoliaKeyPair.getPrivateKey()
    }

    private fun getPublicKey(): PublicKey {
        return magnoliaKeyPair.getPublicKey()
    }

}

@Component
class MagnoliaKeyPair {

    fun getPrivateKey(): PrivateKey {
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("jwtRS256.key")
            ?: throw RuntimeException("jwtRS256.key file doesn't exist")
        val textBuilder = StringBuilder()
        BufferedReader(InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name()))).use { reader ->
            var c: Int
            while (reader.read().also { c = it } != -1) {
                textBuilder.append(c.toChar())
            }
        }

        var privateKey = textBuilder.toString()
        privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
        privateKey = privateKey.replace("-----END PRIVATE KEY-----", "")
        privateKey = privateKey.replace("\\n".toRegex(), "")

        val decodedPrivateKey = Base64.getDecoder().decode(privateKey)
        val keySpec = PKCS8EncodedKeySpec(decodedPrivateKey)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePrivate(keySpec)
    }

    fun getPublicKey(): PublicKey {
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("jwtRS256.key.pub")
            ?: throw RuntimeException("jwtRS256.key.pub file doesn't exist")
        val textBuilder = StringBuilder()
        BufferedReader(InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name()))).use { reader ->
            var c: Int
            while (reader.read().also { c = it } != -1) {
                textBuilder.append(c.toChar())
            }
        }

        var publicKey = textBuilder.toString()
        publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
        publicKey = publicKey.replace("-----END PUBLIC KEY-----", "")
        publicKey = publicKey.replace("\\n".toRegex(), "")
        val decodedPublicKey = Base64.getDecoder().decode(publicKey)

        val keySpec = X509EncodedKeySpec(decodedPublicKey)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(keySpec)
    }

}