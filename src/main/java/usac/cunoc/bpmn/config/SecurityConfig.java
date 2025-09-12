package usac.cunoc.bpmn.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import usac.cunoc.bpmn.security.JwtAuthenticationEntryPoint;
import usac.cunoc.bpmn.security.JwtRequestFilter;

/**
 * Enhanced Security configuration for BPMN API
 * Based on complete analysis of implemented controllers and business
 * requirements
 * 
 * ROLES SUPPORTED:
 * - CLIENTE: Regular users who can browse, purchase, rate, comment
 * - ADMINISTRADOR: Admin users with full system access
 * 
 * SECURITY LAYERS:
 * 1. Public endpoints: No authentication required
 * 2. Authenticated endpoints: Valid JWT token required
 * 3. Role-based endpoints: Specific roles required
 * 4. Method-level security: @PreAuthorize annotations in controllers
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserDetailsService userDetailsService;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtRequestFilter jwtRequestFilter;
        private final CorsConfigurationSource corsConfigurationSource;

        /**
         * Password encoder with strong encryption (BCrypt with strength 12)
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

        /**
         * Authentication provider configuration
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        /**
         * Authentication manager configuration
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        /**
         * Main security filter chain with comprehensive endpoint protection
         * Based on complete analysis of all implemented controllers
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz

                                                // =============================================
                                                // PUBLIC ENDPOINTS (No authentication required)
                                                // =============================================

                                                // Authentication endpoints - must be public for login/register
                                                .requestMatchers(
                                                                "/api/v1/auth/register",
                                                                "/api/v1/auth/login",
                                                                "/api/v1/auth/verify-email",
                                                                "/api/v1/auth/forgot-password",
                                                                "/api/v1/auth/reset-password",
                                                                "/api/v1/auth/refresh-token")
                                                .permitAll()

                                                // Public catalog browsing (customer-facing)
                                                .requestMatchers(
                                                                "/api/v1/catalog/**", // All catalog browsing endpoints
                                                                "/api/v1/catalogs/**") // Master data (genres, artists)
                                                .permitAll()

                                                // Public event viewing (but not registration)
                                                .requestMatchers(
                                                                "/api/v1/events", // List events
                                                                "/api/v1/events/{id}") // View event details
                                                .permitAll()

                                                // Documentation and monitoring
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/api-docs/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                "/actuator/health",
                                                                "/actuator/info",
                                                                "/error")
                                                .permitAll()

                                                // =============================================
                                                // AUTHENTICATED USER ENDPOINTS
                                                // =============================================
                                                // These endpoints require valid JWT but work for any authenticated user

                                                // User profile management
                                                .requestMatchers("/api/v1/users/**")
                                                .authenticated()

                                                // Shopping cart operations
                                                .requestMatchers("/api/v1/cart", "/api/v1/cart/**")
                                                .authenticated()

                                                // Order management
                                                .requestMatchers("/api/v1/orders/**")
                                                .authenticated()

                                                // Wishlist operations
                                                .requestMatchers("/api/v1/wishlist/**")
                                                .authenticated()

                                                // Article ratings and reviews
                                                .requestMatchers("/api/v1/articles/*/ratings/**")
                                                .authenticated()

                                                // Event registration and participation
                                                .requestMatchers(
                                                                "/api/v1/events/*/register",
                                                                "/api/v1/events/*/unregister",
                                                                "/api/v1/events/*/participants",
                                                                "/api/v1/events/*/chat/**")
                                                .authenticated()

                                                // User notifications
                                                .requestMatchers("/api/v1/notifications/**")
                                                .authenticated()

                                                // Preorder audio access
                                                .requestMatchers("/api/v1/preorder-audios/**")
                                                .authenticated()

                                                // =============================================
                                                // ADMINISTRATOR-ONLY ENDPOINTS
                                                // =============================================
                                                // These endpoints require ADMINISTRADOR role

                                                // Admin catalog management
                                                .requestMatchers("/api/v1/admin/catalog/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin catalog master data management
                                                .requestMatchers("/api/v1/admin/catalogs/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin event management
                                                .requestMatchers("/api/v1/admin/events/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin promotion management
                                                .requestMatchers("/api/v1/admin/promotions/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin user management
                                                .requestMatchers("/api/v1/admin/users/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin reports and analytics
                                                .requestMatchers("/api/v1/admin/reports/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Admin stock and inventory management
                                                .requestMatchers("/api/v1/admin/stock/**")
                                                .hasRole("ADMINISTRADOR")

                                                // All other admin endpoints
                                                .requestMatchers("/api/v1/admin/**")
                                                .hasRole("ADMINISTRADOR")

                                                // =============================================
                                                // FALLBACK SECURITY
                                                // =============================================
                                                // All other endpoints require authentication
                                                .anyRequest().authenticated())

                                // Set authentication provider and JWT filter
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
