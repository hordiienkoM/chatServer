package com.hordiienko.myChat.security;

import org.springframework.security.core.userdetails.UserDetails;


public interface StoredUserDetails extends UserDetails {

    String getId();
    boolean isLocked();

    @Override
    default boolean isAccountNonExpired() {
        return this.isLocked();
    }

    @Override
    default boolean isAccountNonLocked() {
        return true;
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }
}
