package edu.kit.fallob.springConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


/**
 * @author Kaloyan Enev
 * @version 1.0
 * This class sets up Spring's WebSecurity (defines the Admin authorities)
 */
@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    private static final String ADMIN = "ADMIN";

    /**
     * Configures the AuthenticationManager
     * @param auth Spring's AuthenticationManagerBuilder
     * @throws Exception an exception in the build
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * Configures the http security (defines the endpoints, which are available only for admins)
     * Defines for which endpoints the user must be authorized
     * @param http httpSecurity that is to be edited
     * @throws Exception an exception in the build
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        // dont authenticate this particular request
        http.authorizeRequests().antMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll();
        http.authorizeRequests().antMatchers(POST, "/api/v1/jobs/cancel/global", "/api/v1/system/mallob/start",
                "/api/v1/system/mallob/stop", "/api/v1/system/mallob/restart").hasAnyAuthority(ADMIN)
                .and().exceptionHandling().accessDeniedHandler(jwtAuthenticationEntryPoint);
        http.authorizeRequests().antMatchers(GET, "/api/v1/jobs/info/global", "/api/v1/system/mallobInfo").
                hasAnyAuthority(ADMIN).and().exceptionHandling().accessDeniedHandler(jwtAuthenticationEntryPoint);
        // all other requests need to be authenticated
        http.cors().and().authorizeRequests().anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Add a filters to validate the tokens with every request
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
     }

    /**
     * Initalizes the AuthenticationManager Bean for Spring
     * @return an AuthenticationManager to Spring
     * @throws Exception an exception in the build
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
