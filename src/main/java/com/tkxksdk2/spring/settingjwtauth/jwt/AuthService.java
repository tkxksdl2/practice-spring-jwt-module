package com.tkxksdk2.spring.settingjwtauth.jwt;


import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.jwt.dtos.RefreshDto;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.TokenInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private JwtEncoder jwtEncoder;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserRefreshTokenRepository refreshTokenRepository;
    private JwtDecoder refreshTokenDecoder;

    public AuthService(JwtEncoder jwtEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
                       UserRefreshTokenRepository refreshTokenRepository, JwtDecoder refreshTokenDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDecoder = refreshTokenDecoder;
    }
    @Transactional
    public TokenInfo login(LoginDto loginDto){
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken  = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = createToken("Access", makeDefaultClaim(authentication, 60 * 15));             // 15 min
        String refreshToken = createToken("Refresh", makeDefaultClaim(authentication, 60 * 60 * 24 * 15)); // 15 days

        saveUserRefreshToken(username, refreshToken);

        return new TokenInfo(accessToken, refreshToken);
    }

    private String createToken(String kid, JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).keyId(kid).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private void saveUserRefreshToken(String username, String refreshToken) {
        UserRefreshToken userRefreshToken = new UserRefreshToken(username, refreshToken);
        refreshTokenRepository.save(userRefreshToken);
    }

    private JwtClaimsSet makeDefaultClaim(Authentication authentication, int expiresSecondTime){
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresSecondTime))
                .subject(authentication.getName())
                .claim("scope",createScope(authentication))
                .build();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    public TokenInfo refresh(RefreshDto refreshToken) {
        String inputRefreshToken = refreshToken.getRefreshToken();
        Jwt decoded = refreshTokenDecoder.decode(inputRefreshToken);

        String username = decoded.getSubject();
        UserRefreshToken userRefreshToken = refreshTokenRepository.findByUsername(username)
                                            .orElseThrow(() -> new UsernameNotFoundException("RefreshToken Not Found"));

        if (!userRefreshToken.getRefreshToken().equals(inputRefreshToken)){
            throw new RuntimeException("RefreshToken Not Match");
        }

        JwtClaimsSet newAccessTokenClaim =  makeClaimFromRefreshToken(decoded);
        String newAccessToken = createToken("Access", newAccessTokenClaim);

        return new TokenInfo(newAccessToken, refreshToken.getRefreshToken());
    }

    public JwtClaimsSet makeClaimFromRefreshToken(Jwt decodedToken){
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60*15))
                .subject(decodedToken.getSubject())
                .claim("scope",decodedToken.getClaim("scope"))
                .build();
    }
}
