package com.tkxksdk2.spring.settingjwtauth.users;

import com.tkxksdk2.spring.settingjwtauth.users.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.TokenInfo;
import org.apache.coyote.BadRequestException;
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

    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/user/create")
    public String createUser(@RequestBody LoginDto loginDto) throws BadRequestException {
        return userService.createUser(loginDto);
    }

    @GetMapping("/isauthenticated")
    public String isAuthenticated() {
        return "You Authenticated";
    }

    @GetMapping("/")
    public String hello() {
        return "Hello!";
    }
}
