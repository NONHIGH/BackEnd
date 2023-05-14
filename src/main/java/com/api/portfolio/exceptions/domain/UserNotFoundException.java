package com.api.portfolio.exceptions.domain;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String e){
        super(e);
    }
    
}
