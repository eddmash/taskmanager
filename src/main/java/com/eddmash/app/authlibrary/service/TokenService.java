package com.eddmash.app.authlibrary.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eddmash.app.authentication.dto.TokenInfo;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authlibrary.dto.AuthUser;
import com.eddmash.app.authlibrary.exception.TokenIntrospection;
import com.eddmash.app.shared.config.AppConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService implements UserDetailsService {
    @Getter
    private final AppConfig appConfig;
    @Getter
    private final RestTemplate restTemplate;

    /**
     * validate token from api
     * 
     * @param jwt
     * @return
     */
    public UserDetails tokenIntrospection(String jwt) {
        String url = getAppConfig().getAuth().getAuthUrl() + getAppConfig().getAuth().getTokenIntrospectionPath();
        log.info("Introspecting token url [" + url + "]");
        log.debug("Introspecting token [" + jwt + "]");
        try {
            HttpEntity<TokenInfo> req = new HttpEntity<>(TokenInfo.builder().accessToken(jwt).build());
            User user = getRestTemplate().postForObject(url, req, User.class);
            return AuthUser.builder().user(user).build();
        } catch (Exception e) {
            log.error("Token introspection failed: {}", e.getMessage());
            throw new TokenIntrospection("Unable to validate token");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return tokenIntrospection(username);
    }
}
