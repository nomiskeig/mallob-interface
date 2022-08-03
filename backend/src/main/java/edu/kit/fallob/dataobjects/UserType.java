package edu.kit.fallob.dataobjects;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {

    ADMIN,
    NORMAL_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
