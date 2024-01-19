package com.tkxksdk2.spring.settingjwtauth.jwt.dtos;

public class RefreshDto {

    private String refreshToken;

    public RefreshDto() {
    }

    public RefreshDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
