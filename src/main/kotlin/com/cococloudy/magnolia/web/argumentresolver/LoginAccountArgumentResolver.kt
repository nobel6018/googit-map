package com.cococloudy.magnolia.web.argumentresolver

import com.cococloudy.magnolia.Account
import com.cococloudy.magnolia.AccountRepository
import com.cococloudy.magnolia.NotFoundException
import com.cococloudy.magnolia.security.JwtConfig
import com.cococloudy.magnolia.security.JwtService
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest

@Component
class LoginAccountArgumentResolver(
    private val jwtConfig: JwtConfig,
    private val jwtService: JwtService,
    private val accountRepository: AccountRepository,
) : HandlerMethodArgumentResolver {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasParameterAnnotation = parameter.hasParameterAnnotation(Login::class.java)
        val hasAccountType = Account::class.java.isAssignableFrom(parameter.parameterType)

        return hasParameterAnnotation && hasAccountType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.nativeRequest as HttpServletRequest
        val token = request.getHeader(jwtConfig.header).replace(jwtConfig.prefix, "")

        if (token == "") {
            return null
        }

        return try {
            val jwtParser = jwtService.getJwtParser()
            val parsedJwt = jwtParser.parseClaimsJws(token)

            val accountId = parsedJwt.body["sub"].toString().toLong()

            accountRepository.findByIdOrNull(accountId)
                ?: throw NotFoundException("Account", accountId)
        } catch (expiredException: ExpiredJwtException) {
            // Todo: ExpiredJwtException 그냥 던지고 예외 AdviceController에서 잡는 걸로 처리하기 - 그리고 try catch 없애기
            log.info("Token is expired")
            null
        }

    }

}