package com.app.novaes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientRepository;
import com.app.novaes.employee.Employee;
import com.app.novaes.employee.EmployeeRepository;
import com.app.novaes.user.*;
import com.app.novaes.util.JpaPersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PersistentLoginRepository persistentLoginRepository;

    
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChan(HttpSecurity http) throws Exception {
    	return http
    			.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/js/**",
                                "/css/**",
                                "/img/**",
                                "/icones/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/directory", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .tokenRepository(jpaPersistentTokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 365)
                        .userDetailsService(userDetailsService())
                )
                .build();
    }
    
    
    @Bean
    @Order(1)
    public SecurityFilterChain mobileSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
        		.securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/client" , "/employee").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET , "/api/user" , "/api/contract").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("login")
                        .successHandler((request, response, authentication) -> {
                            Long userId = ((User) authentication.getPrincipal()).getId();
                            response.setStatus(200);
                            response.getWriter().write(userId.toString());
                        })
                        .failureHandler((request, response, exception) -> response.setStatus(400))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204))
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .tokenRepository(jpaPersistentTokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 365) // 1 ano
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> response.setStatus(401))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(403))
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Client client = clientRepository.findByLogin(username);
            if (client != null) {
                return client;
            }
            Employee employee = employeeRepository.findByLogin(username);
            if (employee != null) {
                return employee;
            }
            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_EMPLOYEE \n ROLE_EMPLOYEE > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JpaPersistentTokenRepository jpaPersistentTokenRepository() {
        return new JpaPersistentTokenRepository(persistentLoginRepository);
    }
}
