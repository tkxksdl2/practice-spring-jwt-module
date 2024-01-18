package com.tkxksdk2.spring.settingjwtauth;


import com.tkxksdk2.spring.settingjwtauth.users.UserService;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.LoginDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeRunner implements CommandLineRunner {
    public CodeRunner(UserService userService) {
        this.userService = userService;
    }

    private UserService userService;


    @Override
    public void run(String... args) throws Exception {
        userService.createUser(new LoginDto("tkxksdl2", "1234"));

    }
}
