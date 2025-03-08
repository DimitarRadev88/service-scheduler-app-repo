package bg.softuni.serviceScheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
//            requests.requestMatchers("/login", "/register", "/", "/home", "/logout", "/vehicles/add", "/vehicles/add/**", "/insurances/add").permitAll();
                    requests.requestMatchers("/css/**", "/fonts/**", "/img/**", "/js/**").permitAll();
                    requests.anyRequest().permitAll();
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/");
                }).build();
    }


}
