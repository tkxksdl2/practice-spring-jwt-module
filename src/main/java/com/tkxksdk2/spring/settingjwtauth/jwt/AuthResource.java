package com.tkxksdk2.spring.settingjwtauth.jwt;


import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.RefreshDto;
import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.TokenInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthResource {

    private AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public TokenInfo login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/auth/refresh")
    public TokenInfo refresh(@RequestBody RefreshDto refreshToken){
        return authService.refresh(refreshToken);
    }
}
