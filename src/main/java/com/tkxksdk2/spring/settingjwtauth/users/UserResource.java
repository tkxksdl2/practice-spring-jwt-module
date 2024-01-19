package com.tkxksdk2.spring.settingjwtauth.users;

import com.tkxksdk2.spring.settingjwtauth.users.dtos.CreateUserDto;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResource {
    private  UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello!";
    }

    @PostMapping("/user/create")
    public String createUser(@RequestBody CreateUserDto createUserDto) throws BadRequestException {
        return userService.createUser(createUserDto);
    }

    @GetMapping("/is-authenticated")
    public String isAuthenticated(Authentication auth) {
        return "You Authenticated";
    }

}
