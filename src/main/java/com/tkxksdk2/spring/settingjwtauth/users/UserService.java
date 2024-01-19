package com.tkxksdk2.spring.settingjwtauth.users;

import com.tkxksdk2.spring.settingjwtauth.jwt.UserRefreshToken;
import com.tkxksdk2.spring.settingjwtauth.jwt.UserRefreshTokenRepository;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.CreateUserDto;
import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.TokenInfo;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class UserService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;



    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createUser(CreateUserDto createUserDto) throws BadRequestException {
       boolean exists = userRepository.findByUsername(createUserDto.getUsername()).isPresent();
       if (exists) throw new BadRequestException("User Already Exists");

       UserEntity user = new UserEntity(createUserDto.getUsername(),
                                        passwordEncoder.encode(createUserDto.getPassword()),
                                        createUserDto.getRoles());
       UserEntity newUser = userRepository.save(user);

       return newUser.getUsername();
    }
}
