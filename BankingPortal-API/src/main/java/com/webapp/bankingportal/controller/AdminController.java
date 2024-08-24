package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.AccountDoesNotExists;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailService emailService;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isApproved()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already approved.");
        }

        // Mark user as approved
        user.setApproved(true);
        userRepository.save(user);

        // Create account for the user
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(user.getAccount().getAccountNumber());
        accountRepository.save(account);

        // Send approval email with account number
        emailService.sendEmail(user.getEmail(), account.getAccountNumber(),user.getEmail());

        return ResponseEntity.ok("User approved and account created.");
    }


}