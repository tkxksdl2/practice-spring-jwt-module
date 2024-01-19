package com.tkxksdk2.spring.settingjwtauth;


import com.tkxksdk2.spring.settingjwtauth.users.UserRoles;
import com.tkxksdk2.spring.settingjwtauth.users.UserService;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.CreateUserDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CodeRunner implements CommandLineRunner {
    public CodeRunner(UserService userService) {
        this.userService = userService;
    }

    private UserService userService;


    @Override
    public void run(String... args) throws Exception {
        List<UserRoles> userRoles = new ArrayList<UserRoles>();
        userRoles.add(UserRoles.MEMBER);
        userRoles.add(UserRoles.ADMIN);
        userService.createUser(new CreateUserDto("tkxksdl2", "1234", userRoles));
    }
}
