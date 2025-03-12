// package com.azhag_swe.tech_tutorial.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SwaggerSecurityConfig {

//     @Bean
//     public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
//         http
//             // This chain applies only to swagger-related URLs and our custom login page.
//             .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/login.html", "/swagger-custom.js")
//             .authorizeHttpRequests(auth -> auth
//                 .anyRequest().authenticated()
//             )
//             // Configure form login with our custom login page.
//             .formLogin(form -> form
//                 .loginPage("/login.html")
//                 .defaultSuccessUrl("/swagger-ui/index.html", true)
//                 .permitAll()
//             )
//             .logout(logout -> logout.permitAll());
//         return http.build();
//     }
// }
