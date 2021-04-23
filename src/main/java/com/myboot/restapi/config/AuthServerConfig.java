package com.myboot.restapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.myboot.restapi.accounts.AccountService;
import com.myboot.restapi.common.AppProperties;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AppProperties appProperties;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AccountService accountService;

	@Autowired
	TokenStore tokenStore;

	/*
	 * Authorization Server의 EndPoint 설정
	 * 1. 인증을 처리하는 AuthenticationManager 설정 ( authenticationManager() )
	 * 2. DB에서 읽은 인증정보(username,password)를 User 객체에 담아서 반환해주는 UserDetailsService 설정
	 * 3. Access Token을 저장해주는 TokenStore를 설정 (현재는 InMemory 에 token을 저장한다)  
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
				.userDetailsService(accountService)
				.tokenStore(tokenStore);
	}

	// OAuth2 인증 서버 보안(Password) 정보를 설정
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(passwordEncoder);
	}

	/*
	 * 1. Client ID ( withClient() ) 와 Secret ID( secret())를 설정하고 
	 * 2. Grant Type 설정 - Password와 Refresh_Token ( authorizedGrantTypes() )
	 * 3. Scope 설정 - read / write (  scopes() )
	 * 4. access token의 유효시간 설정 ( accessTokenValiditySeconds() 600초 10분 )
	 * 5. refresh token의 유효시간 설정 ( refreshTokenValiditySeconds() 3600초 1시간 )
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient(appProperties.getClientId())
				.secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
				.authorizedGrantTypes("password", "refresh_token")
				.scopes("read", "write")
				.accessTokenValiditySeconds(10 * 60).refreshTokenValiditySeconds(6 * 10 * 60);
	}

}
