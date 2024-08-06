package com.oneTech.Smartbank.dto;


import com.oneTech.Smartbank.enums.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;

}
