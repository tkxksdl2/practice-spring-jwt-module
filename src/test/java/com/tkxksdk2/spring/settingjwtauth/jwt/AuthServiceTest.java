package com.tkxksdk2.spring.settingjwtauth.jwt;


import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.*;
import com.tkxksdk2.spring.settingjwtauth.users.UserEntity;
import com.tkxksdk2.spring.settingjwtauth.users.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {
    @MockBean
    private UserRefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuthService authService;
    private static TokenInfo testTokens;

    @BeforeAll
    static void beforeAll(@Autowired UserRepository users,
                          @Autowired PasswordEncoder passwordEncoder,
                          @Autowired AuthService authService) {
        UserEntity BasicUser =  // saving default user
                new UserEntity("TestUser", passwordEncoder.encode("1234"), new ArrayList<>());
        users.save(BasicUser);

        testTokens = authService.login(new LoginDto("TestUser", "1234"));
    }

    @Test
    @DisplayName("Login Success")

    void loginSuccess() {
        assertDoesNotThrow(() -> {
            TokenInfo token = authService.login(new LoginDto("TestUser", "1234"));
            assertEquals(token.getClass(), TokenInfo.class);
        });
    }

    @Test
    @DisplayName("Login Fail - User Not Exists")
    void loginFailsWithUserNotExists() {
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(new LoginDto("TestUse", "1234"));
        });
    }

    @Test
    @DisplayName("Login Fail - User Does Not Exists")
    void loginFailsWithBadPassword() {
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(new LoginDto("TestUser", "123"));
        });
    }


    @Test
    @DisplayName("Refresh Success")
    void refreshSuccess() throws InterruptedException {
        when(refreshTokenRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(new UserRefreshToken("TestUser", testTokens.getRefreshToken())));

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            RefreshDto refreshDto = new RefreshDto(testTokens.getRefreshToken());
            TokenInfo newTokens = authService.refresh(refreshDto);
            assertNotEquals(newTokens.getAccessToken(), testTokens.getAccessToken());
            assertEquals(newTokens.getRefreshToken(), testTokens.getRefreshToken());
        });
    }

    @Test
    @DisplayName("Refresh Fail - Refresh Token Not Found")
    void refreshFailWithTokenNotFound() {
        when(refreshTokenRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,() -> {
            RefreshDto refreshDto = new RefreshDto(testTokens.getRefreshToken());
            TokenInfo newTokens = authService.refresh(refreshDto);
        });
    }

    @Test
    @DisplayName("Refresh Fail - Refresh Token Not Equals")
    void refreshFailWithTokenNotEquals() {
        when(refreshTokenRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(new UserRefreshToken("TestUser", "FAKE_BAD_TOKEN")));

        assertThrows(RuntimeException.class,() -> {
            RefreshDto refreshDto = new RefreshDto(testTokens.getRefreshToken());
            TokenInfo newTokens = authService.refresh(refreshDto);
        });
    }
}
