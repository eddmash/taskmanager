package com.eddmash.app.authlibrary.exception;

import org.springframework.security.access.AccessDeniedException;

public class TokenIntrospection extends AccessDeniedException {

    public TokenIntrospection(String msg){
        super(msg);
    }
}
