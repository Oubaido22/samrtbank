package com.oneTech.Smartbank.services.auth;


import com.oneTech.Smartbank.dto.SignupRequest;
import com.oneTech.Smartbank.dto.UserDto;
import com.oneTech.Smartbank.entity.User;
import com.oneTech.Smartbank.enums.UserRole;
import com.oneTech.Smartbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;

    @Override
    public UserDto createCustomer(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setPassword(signupRequest.getPassword());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setUserRole(UserRole.CUSTOMER);
        user.setAddress(signupRequest.getAddress());
        User createdUser =userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        return userDto;
    }

    @Override
    public Boolean hasCustomerWithEmail(String email) {

        return userRepository.findFirstByEmail(email).isPresent();
    }




}
