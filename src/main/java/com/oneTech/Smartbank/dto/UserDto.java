package com.oneTech.Smartbank.dto;

import com.oneTech.Smartbank.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private UserRole UserRole;
}
