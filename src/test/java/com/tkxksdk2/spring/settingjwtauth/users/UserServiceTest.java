package com.tkxksdk2.spring.settingjwtauth.users;


import com.tkxksdk2.spring.settingjwtauth.users.dtos.CreateUserDto;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static UserEntity testUserEntity = new UserEntity("testName", "1234", new ArrayList<>());

    @Test
    @DisplayName("유저 생성 실패 - 유저 중복")
    void createUserFailByDuplicate(){
        Optional<UserEntity> testUser = Optional.of(testUserEntity);
        when(userRepository.findByUsername(anyString())).thenReturn(testUser);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(new CreateUserDto("testName", "1234" ,new ArrayList<>()));
        });
        assertEquals(exception.getMessage(), "User Already Exists");
    }

    @Test
    @DisplayName("유저 생성 성공")
    void createUserSuccess(){
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(userRepository.save(any())).thenReturn(testUserEntity);

        assertDoesNotThrow(() -> {
            String username = userService.createUser(new CreateUserDto("testName", "1234" ,new ArrayList<>()));
            assertEquals(username, testUserEntity.getUsername());
        });
    }
}
