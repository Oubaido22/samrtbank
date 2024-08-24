package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.UserRole;
import com.webapp.bankingportal.exception.UserValidation;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.UserRoleRepository;
import com.webapp.bankingportal.util.LoggedinUser;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
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
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final UserRoleRepository userRoleRepository;
    private final SystemMetricsAutoConfiguration systemMetricsAutoConfiguration;


    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, AccountService accountService, AccountRepository accountRepository, EmailService emailService, PasswordEncoder passwordEncoder, SystemMetricsAutoConfiguration systemMetricsAutoConfiguration) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository; // Inject AccountRepository
        this.emailService = emailService; // I
        this.passwordEncoder =  passwordEncoder;
        this.systemMetricsAutoConfiguration = systemMetricsAutoConfiguration;
    }
    
    @Override
    public User getUserByAccountNumber(String account_no) {
    	return userRepository.findByAccountAccountNumber(account_no);
    }

    @Override
    public User registerUser(User user) {
        
    	 String encodedPassword = passwordEncoder.encode(user.getPassword());
         user.setPassword(encodedPassword);
         user.setApproved(false);


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
    public List<User> getPendingUsers() {

        System.out.println(userRepository.findByApproved(false));


        return userRepository.findByApproved(false);
    }


    @Override
    public void approveUser(Long userId, boolean isApproved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(isApproved);

        // If approved, create an account
        if (isApproved) {
            Account newAccount = new Account();
            newAccount.setAccountNumber(user.getAccount().getAccountNumber());
            newAccount.setUser(user);
            accountRepository.save(newAccount);

            // Send approval email
            emailService.sendEmail(user.getEmail(), newAccount.getAccountNumber(),"this is an approval email");
        }

        userRepository.save(user);
    }


}
