package com.eddmash.app.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eddmash.app.authentication.dto.LoginDto;
import com.eddmash.app.authentication.dto.SignUpDto;
import com.eddmash.app.authentication.dto.TokenInfo;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.service.AuthenticationService;
import com.eddmash.app.shared.dto.GenericResponse;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    @Getter
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse<User>> signup(@Valid @RequestBody SignUpDto signUpDto) {
        GenericResponse<User> resp = getAuthenticationService().signup(signUpDto);
        return new ResponseEntity<>(resp, new LinkedMultiValueMap<>(), resp.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@Valid @RequestBody LoginDto request) {
        TokenInfo resp = getAuthenticationService().login(request);
        return ResponseEntity.ok(resp);

    }

    @PostMapping("/tokenInfo")
    public ResponseEntity<User> tokenInfo(@Valid @RequestBody TokenInfo request) {
        User resp = getAuthenticationService().tokenInfo(request);
        return ResponseEntity.ok(resp);

    }
}
