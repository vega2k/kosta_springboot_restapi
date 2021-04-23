package com.myboot.restapi.config;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.myboot.restapi.accounts.Account;
import com.myboot.restapi.accounts.AccountRole;
import com.myboot.restapi.accounts.AccountService;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			@Autowired
			AccountService accountService;

			@Override
			public void run(ApplicationArguments args) throws Exception {
				Account account = Account.builder()
									.email("user@email.com")
									.password("user")
									.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
									.build();
				accountService.saveAccount(account);
			}
		};
	}
}
