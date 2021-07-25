package com.cococloudy.magnolia.config

import com.cococloudy.magnolia.security.JwtConfig
import com.cococloudy.magnolia.security.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.OffsetDateTime
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtTokenAuthenticationFilter(
    private val jwtConfig: JwtConfig,
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

        val header = request.getHeader(jwtConfig.header)

        if (header == null || !header.startsWith(jwtConfig.prefix)) {
            chain.doFilter(request, response)
            return
        }

        val token = header.replace(jwtConfig.prefix, "")

        try {
            val jwtParser = jwtService.getJwtParser()
            val parsedJwt = jwtParser.parseClaimsJws(token)

            val userId = parsedJwt.body["sub"]
            if (userId != null) {
                val authorities = parsedJwt.body["role"]
                val auth = UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    listOf(SimpleGrantedAuthority(authorities!!.toString()))
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (expiredException: ExpiredJwtException) {
            if (request.requestURI == "/api/v1/refreshToken") {
                chain.doFilter(request, response)
                return
            }

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.addHeader("Content-Type", "application/json")
            response.writer.write(
                convertObjectToJson(
                    AccessTokenExpiredCustomException(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                        message = "Access token is expired",
                        path = request.requestURI
                    )
                )
            )
            return
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
        }
        chain.doFilter(request, response)
    }

    private fun convertObjectToJson(obj: Any): String {
        val objectMapper = ObjectMapper()

        return objectMapper.writeValueAsString(obj)
    }
}

data class AccessTokenExpiredCustomException(
    val timestamp: String = OffsetDateTime.now().toString(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)