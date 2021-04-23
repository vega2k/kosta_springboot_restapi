package com.myboot.restapi.accounts;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AccountAdapter extends User {
	private static final long serialVersionUID = 1L;
	
	private Account account;

	public AccountAdapter(Account account) {
		super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
		this.account = account;
	}

	private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		// User(java.lang.String username, java.lang.String password,
		// java.util.Collection<? extends GrantedAuthority> authorities)

		// Set<AccountRole> -> Stream<AccountRole> -> Stream<SimpleGrantedAuthority> ->
		// Collection<SimpleGrantedAuthority>
		return roles.stream().map(accountRole -> new SimpleGrantedAuthority(accountRole.name())).collect(toSet());
	}
	public Account getAccount() {
		return account;
	}
}