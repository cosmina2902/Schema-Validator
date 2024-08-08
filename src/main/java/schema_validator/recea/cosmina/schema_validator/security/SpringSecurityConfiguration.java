package schema_validator.recea.cosmina.schema_validator.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import schema_validator.recea.cosmina.schema_validator.entity.Users;
import schema_validator.recea.cosmina.schema_validator.service.UserAndSchemaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SpringSecurityConfiguration {

    //private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests( (authorize) -> {
                    authorize.requestMatchers(HttpMethod.POST, "api/users").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.POST, "api/users/**").hasAnyRole("USER", "ADMIN");
                    authorize.requestMatchers(HttpMethod.POST, "api/json-schema/**").hasAnyRole("USER", "ADMIN");
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorize.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults());

        return http.build();

    }

    @Bean
    public InMemoryUserDetailsManager createUserDetailsManager(UserAndSchemaService userAndSchemaService) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Set<Users> jsonUsers = userAndSchemaService.getUsers();
        List<UserDetails> newUsers = new ArrayList<>();

        for (Users user : jsonUsers) {
            UserDetails userDetails = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
            newUsers.add(userDetails);
        }

        UserDetails adminDetails = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();
        newUsers.add(adminDetails);

        return new InMemoryUserDetailsManager(newUsers);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
