package com.webapp.bankingportal.service;

import java.util.Collections;

import com.webapp.bankingportal.controller.UserController;
import com.webapp.bankingportal.exception.UserNotApprovedException;
import com.webapp.bankingportal.exception.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        User user = userRepository.findByAccountAccountNumber(accountNumber);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid account number");
        }

        if (!user.isApproved()) {
            throw new UserNotApprovedException("User is not approved yet.");
        }

        // Return a UserDetails object that wraps the User entity
        return new org.springframework.security.core.userdetails.User(
                user.getAccount().getAccountNumber(),  // Use account number as the username
                user.getPassword(),
                Collections.emptyList()
        );
    }
}