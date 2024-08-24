package com.webapp.bankingportal.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.webapp.bankingportal.entity.UserRole;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.security.JwtTokenUtil;
import com.webapp.bankingportal.service.OTPService;
import com.webapp.bankingportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final OTPService otpService;
    private final UserRepository userRepository;


    public UserController(UserRepository userRepository,UserService userService, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
                          UserDetailsService userDetailsService, OTPService otpService) {
        this.userService =  userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.otpService = otpService;
    }

    @Autowired
    private UserRoleRepository userRoleRepository;
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);

        UserRole userRole = new UserRole();
        userRole.setUser(registeredUser);
        userRole.setRole("USER");
        // Save the user role
        userRoleRepository.save(userRole);
        UserResponse userResponse = new UserResponse();
        userResponse.setName(registeredUser.getName());
        userResponse.setEmail(registeredUser.getEmail());
        userResponse.setApproved(false);
        userResponse.setAccountNumber(registeredUser.getAccount().getAccountNumber());
        userResponse.setIFSC_code(registeredUser.getAccount().getIFSC_code());
        userResponse.setBranch(registeredUser.getAccount().getBranch());
        userResponse.setAccount_type(registeredUser.getAccount().getAccount_type());


        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user with the account number and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getAccountNumber(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Invalid credentials, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid account number or password");
        }




        // If authentication is successful and account is approved, generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getAccountNumber());
        System.out.println(userDetails);

        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);

        // Return the JWT token in the response
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpRequest otpRequest) {

    	 String accountNumber = otpRequest.getAccountNumber();

         // Fetch the user by account number to get the associated email
         User user = userService.getUserByAccountNumber(accountNumber);
         if (user == null) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for the given account number");
         }

         // Generate OTP and save it in the database
         String otp = otpService.generateOTP(accountNumber);


        // Send the OTP to the user's email address asynchronously
        CompletableFuture<Boolean> emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber, otp);

        // Wait for the email sending process to complete and handle the response
        try {
            boolean otpSent = emailSendingFuture.get(); // This will block until the email sending is complete

            if (otpSent) {
                // Return JSON response with success message
                return ResponseEntity.ok().body("{\"message\": \"OTP sent successfully\"}");
            } else {
                // Return JSON response with error message
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to send OTP\"}");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return JSON response with error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to send OTP\"}");
        }
    }

    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        String accountNumber = otpVerificationRequest.getAccountNumber();
        String otp = otpVerificationRequest.getOtp();
        
        System.out.println(accountNumber+"  "+otp);

        // Validate OTP against the stored OTP in the database
        boolean isValidOtp = otpService.validateOTP(accountNumber, otp);
        System.out.println(isValidOtp);

        if (isValidOtp) {
            // If OTP is valid, generate JWT token and perform user login
        	
            // If authentication successful, generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(accountNumber);
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            // Return the JWT token in the response
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            // Invalid OTP, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid OTP\"}");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody User user) {
        User updateUser = userService.updateUser(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setName(updateUser.getName());
        userResponse.setEmail(updateUser.getEmail());
        userResponse.setAccountNumber(updateUser.getAccount().getAccountNumber());
        userResponse.setIFSC_code(updateUser.getAccount().getIFSC_code());
        userResponse.setBranch(updateUser.getAccount().getBranch());
        userResponse.setAccount_type(updateUser.getAccount().getAccount_type());


        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/admin/approve/{userId}")
    public ResponseEntity<String> approveUser(@PathVariable Long userId, @RequestParam boolean isApproved) {
        userService.approveUser(userId, isApproved);
        return ResponseEntity.ok("User " + (isApproved ? "approved" : "disapproved") + " successfully");
    }

}
