package bg.softuni.serviceScheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers("/login", "/register", "/", "/error").permitAll()
                            .requestMatchers("/css/**", "/fonts/**", "/img/**", "/js/**").permitAll()
                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    form.loginPage("/login").permitAll()
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/", true)
                            .failureUrl("/login?error");
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true);
                }).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
