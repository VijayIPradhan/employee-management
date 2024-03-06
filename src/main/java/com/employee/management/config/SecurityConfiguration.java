//package com.employee.management.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private CustomOAuth2UserService oauthUserService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint().userService(oauthUserService);
//    }
//}