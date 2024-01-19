package com.tkxksdk2.spring.settingjwtauth.jwt;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, String> {

    public Optional<UserRefreshToken> findByUsername(String username);
}
