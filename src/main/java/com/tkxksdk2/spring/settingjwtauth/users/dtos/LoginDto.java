package com.tkxksdk2.spring.settingjwtauth.users.dtos;


public record LoginDto(String username, String password) {
    @Override
    public String toString() {
        return "LoginDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
