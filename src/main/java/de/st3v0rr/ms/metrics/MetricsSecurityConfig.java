package de.st3v0rr.ms.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
public class MetricsSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String managementUserName;
    private final String managementUserPassword;
    private final String managementPath;

    @Autowired
    public MetricsSecurityConfig(@Value("#{environment.MANAGEMENT_USER_NAME}") final String managementUserName,
                                 @Value("#{environment.MANAGEMENT_USER_PASSWORD}") final String managementPassword,
                                 @Value("${management.context-path}") final String managementPath) {
        this.managementUserName = managementUserName;
        this.managementUserPassword = managementPassword;
        this.managementPath = managementPath;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatcher(
                        new OrRequestMatcher(
                                new AntPathRequestMatcher(managementPath + "/**"))
                )
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(new BasicAuthenticationFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(managementUserName).password(managementUserPassword).roles("BASIC_AUTH_AUTHENTICATED");
    }
}