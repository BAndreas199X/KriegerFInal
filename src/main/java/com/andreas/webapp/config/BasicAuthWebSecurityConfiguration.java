package com.andreas.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Configuration for password protecting the application
 */
@Configuration
public class BasicAuthWebSecurityConfiguration
{
  private static final String ENCODED_PASSWORD = 
		  "$2a$10$K0dbF4DiGB6/vkP7rhKR6.brzre2V8cNVTEyLKp5eRX3/Lcu8RxL.";

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
      httpSecurity.csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth.requestMatchers("/health", "/api/v1/user/new")
    		  .permitAll().requestMatchers("/**").authenticated())
      		  .httpBasic(Customizer.withDefaults());
      return httpSecurity.build();
  }
  
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
	  
	  UserDetails user = User
              .withUsername("PowerfulPowerUser")
              .password(ENCODED_PASSWORD)
              .roles("USER")
              .build();
      return new InMemoryUserDetailsManager(user);
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
}