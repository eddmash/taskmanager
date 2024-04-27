package com.eddmash.app.authentication.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.eddmash.app.authentication.dto.LoginDto;
import com.eddmash.app.authentication.dto.SignUpDto;
import com.eddmash.app.authentication.dto.TokenInfo;
import com.eddmash.app.authentication.exception.AuthenticationException;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.repository.UserRepository;
import com.eddmash.app.shared.dto.GenericResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
        @Getter
        private final UserRepository userRepository;
        @Getter
        private final PasswordEncoder passwordEncoder;
        @Getter
        private final JwtService jwtService;

        public GenericResponse<User> signup(SignUpDto request) {

                userRepository.findByEmail(request.getEmail())
                                .ifPresent(u -> {
                                        log.error("Email already registerd. %s", request.getEmail());
                                        throw new AuthenticationException("Email already registerd.");
                                });

                User user = User.builder().name(request.getName())
                                .email(request.getEmail())
                                .password(getPasswordEncoder().encode(request.getPassword()))
                                .active(1)
                                .dateCreated(LocalDateTime.now())
                                .dateModified(LocalDateTime.now())
                                .insertedBy(0L)
                                .updatedBy(0L)
                                .build();
                getUserRepository().save(user);
                return GenericResponse.<User>builder()
                                .data(user).message("user created")
                                .status(HttpStatus.OK.value())
                                .build();
        }

        /**
         * Obtain token if credentials are valid.
         * 
         * @param request
         * @return
         */
        public TokenInfo login(LoginDto request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));
                if (!getPasswordEncoder().matches(request.getPassword(), user.getPassword())) {
                        throw new AuthenticationException("Invalid email or password.");
                }
                String jwt = getJwtService().generateToken(user);
                return TokenInfo.builder()
                                .accessToken(jwt)
                                .build();
        }

        public User tokenInfo(TokenInfo request) {
                try {
                        String email = getJwtService().extractUserName(request.getAccessToken());
                        if (StringUtils.hasText(email) && getJwtService().isTokenValid(request.getAccessToken())) {
                                User user = getUserRepository().findByEmail(email)
                                                .orElseThrow(() -> {
                                                        throw new AuthenticationException("Invalid token");
                                                });
                                return user;
                        }
                } catch (Exception e) {
                        log.error(e.getMessage());
                }

                throw new AuthenticationException("Invalid token");
        }
}
