package com.tkxksdk2.spring.settingjwtauth.users;

import com.tkxksdk2.spring.settingjwtauth.users.dtos.CreateUserDto;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
