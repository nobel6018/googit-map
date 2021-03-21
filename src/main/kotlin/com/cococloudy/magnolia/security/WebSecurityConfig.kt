package com.cococloudy.magnolia.security

import com.cococloudy.magnolia.config.JwtTokenAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletResponse

@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var jwtConfig: JwtConfig

    @Autowired
    private lateinit var jwtService: JwtService

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _, rsp, _ -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED) }
            .and()
            .addFilterBefore(
                JwtTokenAuthenticationFilter(jwtConfig, jwtService.getJwtParser()),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authorizeRequests()
            .requestMatchers(RequestMatcher { CorsUtils.isPreFlightRequest(it) }).permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/v3/api-docs/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/health/ping").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/account/isAccountIdExist").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/account/signUp").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/account/login").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/refreshToken").permitAll()
            .antMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
            .anyRequest()
            .authenticated()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("http://localhost:3000")
        configuration.addAllowedOrigin("http://127.0.0.1:3000")
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")
        configuration.addExposedHeader("Authorization")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}