package com.saveetha.kanchi_wave_hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .build();

        // http.csrf().disable()
        //     .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        // return http.build();
    }

    // @Bean
    // public MultipartConfigElement multipartConfigElement() {
    //     MultipartConfigFactory factory = new MultipartConfigFactory();
    //     factory.setMaxFileSize(DataSize.ofMegabytes(10));
    //     factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    //     return factory.createMultipartConfig();
    // }


}