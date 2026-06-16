package com.proyecto3.crud.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.proyecto3.crud.security.JwtAuthenticationFilter;

/**
 * Configuración central de seguridad:
 * <ul>
 *   <li>API stateless protegida con JWT.</li>
 *   <li>CORS habilitado mediante objeto de configuración.</li>
 *   <li>Reglas de autorización por rol:
 *       <ul>
 *         <li>GET de productos/categorías: cualquier usuario autenticado.</li>
 *         <li>POST/PUT/DELETE de productos/categorías: solo SUPER-ADMIN-ROLE.</li>
 *       </ul>
 *   </li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private static final String SUPER_ADMIN = "SUPER-ADMIN-ROLE";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Autenticación: pública
                        .requestMatchers("/auth/**").permitAll()
                        // Consulta de productos y categorías: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**")
                        .authenticated()
                        // Registro, actualización y borrado: solo SUPER-ADMIN-ROLE
                        .requestMatchers(HttpMethod.POST, "/api/productos/**", "/api/categorias/**")
                        .hasAuthority(SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**", "/api/categorias/**")
                        .hasAuthority(SUPER_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**", "/api/categorias/**")
                        .hasAuthority(SUPER_ADMIN)
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
