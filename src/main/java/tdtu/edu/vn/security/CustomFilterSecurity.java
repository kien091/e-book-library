package tdtu.edu.vn.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class CustomFilterSecurity {
    CustomFilterJwt customFilterJwt;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Cho phép tất cả các phương thức HTTP
        configuration.setAllowedHeaders(Arrays.asList("*")); // Cho phép tất cả các header
        configuration.setAllowCredentials(true);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
                .configurationSource(corsConfigurationSource())
                // chống tấn công bằng việc copy token
                .and().csrf().disable()
                // Không sử dụng session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Bắt đầu cấu hình cho request
                .authorizeHttpRequests()
                .antMatchers("/login",
                        "/register",
                        "/home/**",
                        "/verifyOTP/**",
                        "/verify-reset-password-token/**",
                        "/forgot-password/**",
                        "/reset-password/**",
                        "/read/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/orders/**").permitAll()
                // Tất cả các request khác đều cần xác thực
                .anyRequest().authenticated()
                .and()
                // Disable form login (Đăng nhập qua url /login)
                .httpBasic();

        http.addFilterBefore(customFilterJwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}