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
 * Updated to include AWS S3 file upload endpoints
 * 
 * ROLES SUPPORTED:
 * - CLIENTE: Regular users who can browse, purchase, rate, comment
 * - ADMINISTRADOR: Admin users with full system access including file uploads
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
         * Based on complete analysis of all implemented controllers including file
         * upload system
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
                                                // ============ PUBLIC ENDPOINTS ============
                                                // No authentication required
                                                .requestMatchers(
                                                                // Swagger/OpenAPI Documentation
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/api-docs/**",
                                                                "/actuator/**",

                                                                // Authentication endpoints
                                                                "/api/v1/auth/**",

                                                                // Public catalog browsing
                                                                "/api/v1/catalog/**",

                                                                // Public artist and genre information
                                                                "/api/v1/catalogs/artists",
                                                                "/api/v1/catalogs/genres",
                                                                "/api/v1/catalogs/currencies",
                                                                "/api/v1/catalogs/countries",

                                                                // Public content access
                                                                "/api/v1/presales/public/**",
                                                                "/api/v1/events/public/**")
                                                .permitAll()

                                                // ============ ADMIN-ONLY ENDPOINTS ============
                                                // Administrative functions requiring ADMIN role
                                                .requestMatchers(
                                                                // Admin catalog management
                                                                "/api/v1/admin/catalog/**",
                                                                "/api/v1/admin/catalogs/**",

                                                                // File upload system - NEW ENDPOINTS
                                                                "/api/v1/admin/upload-url",
                                                                "/api/v1/admin/upload/validate/**",

                                                                // Admin user management
                                                                "/api/v1/admin/users/**",

                                                                // Admin reports and analytics
                                                                "/api/v1/admin/reports/**",

                                                                // Admin promotion management
                                                                "/api/v1/admin/promotions/**",

                                                                // Admin presales management
                                                                "/api/v1/admin/presales/**",

                                                                // Admin event management
                                                                "/api/v1/admin/events/**",

                                                                // Admin order management
                                                                "/api/v1/admin/orders/**",

                                                                // Admin notification management
                                                                "/api/v1/admin/notifications/**")
                                                .hasRole("ADMINISTRADOR")

                                                // ============ CLIENT ENDPOINTS ============
                                                // Regular user functions requiring authentication
                                                .requestMatchers(
                                                                // User profile management
                                                                "/api/v1/users/**",

                                                                // Shopping cart operations
                                                                "/api/v1/cart/**",

                                                                // Order placement and tracking
                                                                "/api/v1/orders/**",

                                                                // Wishlist management
                                                                "/api/v1/wishlist/**",

                                                                // User notifications
                                                                "/api/v1/notifications/**",

                                                                // Event participation
                                                                "/api/v1/events/register/**",
                                                                "/api/v1/events/unregister/**",
                                                                "/api/v1/events/my-registrations",

                                                                // Presales participation
                                                                "/api/v1/presales/register/**",
                                                                "/api/v1/presales/my-registrations",

                                                                // User comments and ratings
                                                                "/api/v1/catalog/articles/*/comments",
                                                                "/api/v1/catalog/articles/*/ratings",

                                                                // Purchase history
                                                                "/api/v1/users/orders/**",
                                                                "/api/v1/users/purchases/**")
                                                .hasAnyRole("CLIENTE", "ADMINISTRADOR")

                                                // ============ SPECIAL ENDPOINTS ============
                                                // Two-factor authentication (authenticated users only)
                                                .requestMatchers(
                                                                "/api/v1/auth/2fa/**")
                                                .authenticated()

                                                // Password reset confirmation (public but with token validation)
                                                .requestMatchers(
                                                                "/api/v1/auth/reset-password/confirm/**")
                                                .permitAll()

                                                // All other requests require authentication
                                                .anyRequest().authenticated());

                // Add JWT authentication filter
                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}