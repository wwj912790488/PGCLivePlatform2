package com.arcvideo.pgcliveplatformserver.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class WebPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        String digest = DigestUtils.md5Hex(rawPassword.toString());
        digest = digest.toLowerCase();
        return digest;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String digest = DigestUtils.md5Hex(rawPassword.toString());
        return (digest.compareToIgnoreCase(encodedPassword) == 0);
    }
}
