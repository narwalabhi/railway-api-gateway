package com.narwal.railwayapigateway.security;

import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
                //TODO Add search antMatchers
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
//                //TODO divide controllers base on roles in services
                .antMatchers("/user/admin/**").hasRole("ADMIN")
                .antMatchers("/train/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/train/admin/**").hasRole("ADMIN")
                .antMatchers("/booking/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/booking/admin/**").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(new JwtAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }
}
