package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/actuator/**", "/h2-console/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.35.2") // 동적 IP: 8 -> 32 -> 124 -> 145 -> 2(정적)
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable(); // h2-console 보기 위해
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
