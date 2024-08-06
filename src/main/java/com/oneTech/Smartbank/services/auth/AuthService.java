package com.oneTech.Smartbank.services.auth;

import com.oneTech.Smartbank.dto.SignupRequest;
import com.oneTech.Smartbank.dto.UserDto;

public interface AuthService {

        UserDto createCustomer(SignupRequest signupRequest);
        Boolean hasCustomerWithEmail(String email );

}
