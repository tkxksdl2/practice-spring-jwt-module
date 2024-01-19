package com.tkxksdk2.spring.settingjwtauth.jwt;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserRefreshToken {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false,length= 1000)
    private String refreshToken;

    public UserRefreshToken() {}

    public UserRefreshToken(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
