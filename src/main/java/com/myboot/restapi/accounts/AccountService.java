package com.myboot.restapi.accounts;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	public Account saveAccount(Account account) {
		account.setPassword(this.passwordEncoder.encode(account.getPassword()));
		return this.accountRepository.save(account);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// orElseThrow(Supplier)
		// Supplier의 T get() 리턴타입은 T extends Throwable 의 하위클래스이어야 한다.
		// public <X extends Throwable> T orElseThrow​(Supplier<? extends X>
		// exceptionSupplier) throws X extends Throwable
		Account account = accountRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		// User(java.lang.String username, java.lang.String password,
		// java.util.Collection<? extends GrantedAuthority> authorities)
		return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
	}

	private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		// Set<AccountRole> -> Stream<AccountRole> -> Stream<SimpleGrantedAuthority> ->
		// Collection<SimpleGrantedAuthority>
		return roles.stream().map(accountRole -> new SimpleGrantedAuthority(accountRole.name())).collect(toSet());
	}

}