package com.webapp.bankingportal.service;

import com.webapp.bankingportal.exception.UserValidation;
import com.webapp.bankingportal.util.LoggedinUser;
import net.bytebuddy.asm.Advice;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    private EmailService emailService;



    public UserServiceImpl(UserRepository userRepository, EmailService emailService,AccountService accountService,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.emailService = emailService;
        this.passwordEncoder =  passwordEncoder;
    }

    @Override
    public User getUserByAccountNumber(String account_no) {
        return userRepository.findByAccountAccountNumber(account_no);
    }

    @Override
    public User registerUser(User user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save the user details
        User savedUser = userRepository.save(user);

        // Create an account for the user
        Account account = accountService.createAccount(savedUser);

        savedUser.setAccount(account);
        userRepository.save(savedUser);

        System.out.println(savedUser.getAccount().getAccountNumber());
        System.out.println(account.getUser().getName());


        return savedUser;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);

    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findByAccountAccountNumber(LoggedinUser.getAccountNumber());
        if(user.getEmail() != null){
            if(user.getEmail().isEmpty())
                throw new UserValidation("Email can't be empty");
            else
                existingUser.setEmail(user.getEmail());
        }
        if(user.getName() != null){
            if(user.getName().isEmpty())
                throw new UserValidation("Name can't be empty");
            else
                existingUser.setName(user.getName());
        }
        if(user.getPhone_number() != null){
            if(user.getPhone_number().isEmpty())
                throw new UserValidation("Phone number can't be empty");
            else
                existingUser.setPhone_number(user.getPhone_number());
        }
        if(user.getAddress() != null){
            existingUser.setAddress(user.getAddress());
        }
        return userRepository.save(existingUser);
    }


    @Override
    public List<User> findUnapprovedUsers() {
        return userRepository.findByApprovedFalse();
    }

    @Override
    public void approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(true);
        userRepository.save(user);
        // Send an approval email if required
    }

    @Override
    public void rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Send a rejection email
        // emailService.sendRejectionEmail(user.getEmail(), user.getName());

        // Delete the user
        userRepository.delete(user);
    }
/*
    private void sendRejectionEmail(User user) {
        // Implement email sending logic here
        // Example using JavaMailSender:
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Account Rejected");
        message.setText("Dear " + user.getName() + ",\n\nYour account has been rejected.\n\nBest regards,\nYour Company");

        javaMailSender.send(message);
    }*/


}