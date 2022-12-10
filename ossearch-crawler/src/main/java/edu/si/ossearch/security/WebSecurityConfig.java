package edu.si.ossearch.security;

import edu.si.ossearch.security.jwt.AuthEntryPointJwt;
import edu.si.ossearch.security.jwt.AuthTokenFilter;
import edu.si.ossearch.security.services.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.*;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Value("${spring.ldap.embedded.enabled}")
	private boolean embeddedLdap = false;

	@Value("${ossearch.allowedOrigins}")
	private String allowedOrigins;

	@Value("${ossearch.embedded-vuejs}")
	private boolean embedded_vuejs;

	@Value("${spring.security.enabled:true}")
	private boolean securityEnabled;

	@Value("${spring.ldap.urls}")
	private String ldapUrls;
	@Value("${spring.ldap.port}")
	private Integer ldapPort;
	@Value("${spring.ldap.base}")
	private String ldapBase;
	@Value("${spring.ldap.username}")
	private String ldapUsername;
	@Value("${spring.ldap.password}")
	private String ldapPassword;

	public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		if (embeddedLdap) {
			log.warn(">>>> using embedded LDAP");
			// authenticate using embedded ldap for testing
			auth.ldapAuthentication()
					.userDetailsContextMapper(userDetailsContextMapper())
					.userDnPatterns("uid={0},ou=people")
					.groupSearchBase("ou=groups")
					.contextSource()
					.url("ldap://localhost:8389/dc=springframework,dc=org")
					.and()
					.passwordCompare()
					.passwordEncoder(PASSWORD_ENCODER)
					.passwordAttribute("userPassword");
		} else {
			log.warn(">>>> using LDAP");
			// authenticate using ldap bind
			auth.ldapAuthentication()
					.userDetailsContextMapper(userDetailsContextMapper())
					.contextSource()
					.url(ldapUrls).port(ldapPort).root(ldapBase)
					.managerDn(ldapUsername)
					.managerPassword(ldapPassword)
					.and()
					.userSearchFilter("(&(sAMAccountName={0}))")
					.userSearchBase("OU=Smithsonian,DC=US,DC=SINET,DC=SI,DC=EDU")
					.groupSearchBase("OU=Smithsonian,DC=US,DC=SINET,DC=SI,DC=EDU")
					.groupSearchFilter("member={0}");
		}

		// fallback and authenticate using database
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PASSWORD_ENCODER;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		List<String> AUTH_WHITELIST = new ArrayList<>();

		String[] swagger = new String[]{
				// -- Swagger UI v2
				"/v2/api-docs",
				"/swagger-resources",
				"/swagger-resources/**",
				"/configuration/ui",
				"/configuration/security",
				"/swagger-ui.html",
				"/webjars/**",
				// -- Swagger UI v3 (OpenAPI)
				"/v3/api-docs/**",
				"/swagger-ui/**",
				// other public endpoints of your API may be appended to this array
				"/actuator/**",
				"/admin/**",
				"/search/**"
		};

		AUTH_WHITELIST.addAll(Arrays.asList(swagger));

		if (embedded_vuejs) {
			String[] vuejs = new String[]{"/", "/index.html", "/favicon.ico", "/static/**",};

			AUTH_WHITELIST.addAll(Arrays.asList(vuejs));
		}

		if (securityEnabled) {
			log.warn(">>>> security enabled");
			log.warn(">>>> auth whitelist {}", AUTH_WHITELIST);
			http.cors().and().csrf().disable()
					.exceptionHandling()
						.authenticationEntryPoint(unauthorizedHandler)
					.and()
						.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
						.authorizeRequests()
							.antMatchers("/api/auth/**").permitAll()
							.antMatchers("/api/reports/**").permitAll()
							.antMatchers("/api/**").authenticated()
							.antMatchers(AUTH_WHITELIST.toArray(new String[AUTH_WHITELIST.size()])).permitAll()
							.antMatchers("/**").permitAll()
							.anyRequest().authenticated();
		} else {
			log.warn(">>>> security disabled");
			log.warn(">>>> auth whitelist {}", AUTH_WHITELIST);
			http.cors().and().csrf().disable()
					.exceptionHandling()
						.authenticationEntryPoint(unauthorizedHandler)
					.and()
						.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
						.authorizeRequests()
							.antMatchers("/api/auth/**").permitAll()
							.antMatchers("/api/reports/**").permitAll()
							.antMatchers("/api/**").permitAll()
							.antMatchers(AUTH_WHITELIST.toArray(new String[AUTH_WHITELIST.size()])).permitAll()
							.antMatchers("/**").permitAll()
							.anyRequest().authenticated();
		}

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/**
	 * Get user authorizations from database during ldap user authentication
	 * see: https://stackoverflow.com/questions/61711828/how-to-implement-spring-security-with-ldap-and-role-based-granted-authorities-fr
	 *
	 * @return
	 */
	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new LdapUserDetailsMapper() {
			@Override
			public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
				UserDetails details = userDetailsService.loadUserByUsername(username);
				return details;
			}
		};
	}

	// Fix the CORS errors
	@Bean
	public FilterRegistrationBean simpleCorsFilter() {
		log.warn(">>>> allowed origins: {}", allowedOrigins);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		// *** URL below needs to match the Vue client URL and port ***
		config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		config.setExposedHeaders(Collections.singletonList("*"));
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}
}
