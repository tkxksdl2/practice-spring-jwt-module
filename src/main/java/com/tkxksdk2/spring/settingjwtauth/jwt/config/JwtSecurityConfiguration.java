package com.tkxksdk2.spring.settingjwtauth.jwt.config;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
public class JwtSecurityConfiguration {

    @Bean
    SecurityFilterChain securityWebFilterChain(HttpSecurity http, JwtDecoder accessTokenDecoder) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.headers(headers -> headers.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::disable
        ));

        http.authorizeHttpRequests((authorize ->
                authorize.requestMatchers("/h2-console/**", "/","/auth/**").permitAll()
                        .anyRequest().authenticated()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(accessTokenDecoder)));

        return http.build();
    }


    @Bean
    public KeyPair accessKeyPair() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public KeyPair refreshKeyPair() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public RSAKey accessKey(KeyPair accessKeyPair) {
        return new RSAKey.Builder((RSAPublicKey) accessKeyPair.getPublic())
                .privateKey(accessKeyPair.getPrivate())
                .keyID("Access")
                .build();
    }

    @Bean
    public RSAKey refreshKey(KeyPair refreshKeyPair){
        return new RSAKey.Builder((RSAPublicKey) refreshKeyPair.getPublic())
                .privateKey(refreshKeyPair.getPrivate())
                .keyID("Refresh")
                .build();

    }


    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey accessKey, RSAKey refreshKey){
        var jwkSet = new JWKSet(List.of(accessKey, refreshKey));

        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder accessTokenDecoder(JWKSource<SecurityContext> jwkSource) throws JOSEException {
        JWKMatcher matcher = new JWKMatcher.Builder().keyID("Access").build();
        List<JWK> jwkList = jwkSource.get(new JWKSelector(matcher), null);

        return NimbusJwtDecoder.withPublicKey(jwkList.get(0).toRSAKey().toRSAPublicKey()).build();
    }

    @Bean
    public JwtDecoder refreshTokenDecoder(JWKSource<SecurityContext> jwkSource) throws JOSEException {
        JWKMatcher matcher = new JWKMatcher.Builder().keyID("Refresh").build();
        List<JWK> jwkList = jwkSource.get(new JWKSelector(matcher), null);

        return NimbusJwtDecoder.withPublicKey(jwkList.get(0).toRSAKey().toRSAPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
