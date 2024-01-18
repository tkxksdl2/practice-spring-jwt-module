package com.tkxksdk2.spring.settingjwtauth.users;

import com.tkxksdk2.spring.settingjwtauth.users.dtos.LoginDto;
import com.tkxksdk2.spring.settingjwtauth.users.dtos.TokenInfo;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class UserService {
    private JwtEncoder jwtEncoder;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public UserService(JwtEncoder jwtEncoder,
                       AuthenticationManagerBuilder authenticationManagerBuilder,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokenInfo login(LoginDto loginDto){
        String username = loginDto.username();
        String password = loginDto.password();
        UsernamePasswordAuthenticationToken authenticationToken  = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = createToken("Access", makeDefaultClaim(authentication));
        String refreshToken = createToken("Refresh", makeDefaultClaim(authentication));

        return new TokenInfo(accessToken, refreshToken);
    }

    private String createToken(String kid, JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).keyId(kid).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private JwtClaimsSet makeDefaultClaim(Authentication authentication){
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60*15))
                .subject(authentication.getName())
//                .claim("scope",createScope(authentication))
                .build();
    }

    public String createUser(LoginDto loginDto) throws BadRequestException {
       boolean exists = userRepository.findByUsername(loginDto.username()).isPresent();
       if (exists) throw new BadRequestException("User Already Exists");

       UserEntity user = new UserEntity(loginDto.username(), passwordEncoder.encode(loginDto.password()), new ArrayList<>() );
       UserEntity newUser = userRepository.save(user);

       return newUser.getUsername();
    }
}
