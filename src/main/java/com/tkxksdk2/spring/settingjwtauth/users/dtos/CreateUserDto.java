package com.tkxksdk2.spring.settingjwtauth.users.dtos;

import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.users.UserRoles;

import java.util.List;

public class CreateUserDto extends LoginDto {
    private List<UserRoles> roles;

    public CreateUserDto(String username, String password, List<UserRoles> roles) {
        super(username, password);
        this.roles = roles;
    }

    public List<UserRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRoles> roles) {
        this.roles = roles;
    }
}
