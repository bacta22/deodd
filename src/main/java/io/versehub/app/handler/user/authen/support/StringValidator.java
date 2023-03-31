package io.versehub.app.handler.user.authen.support;


import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class StringValidator {

    // nickname has from 6-35 characters, only contains a-z, 0-9
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9]{6,55}$";
    // username has from 8-40 characters, only contains a-z, A-Z, 0-9, ".", "_"
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._]{8,40}$";
    // password has from 8-256 characters, contains at least one lower character, one upper character, one special character
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-\\[{}\\]?><,!@#$%^&*()\"`~])(?=\\S+$).{8,256}$";

    private static final Pattern nicknamePattern = Pattern.compile(NICKNAME_REGEX);
    private static final Pattern usernamePattern = Pattern.compile(USERNAME_REGEX);
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_REGEX);

    public void validateNickname(String nickname) {
        Matcher matcher = nicknamePattern.matcher(nickname);
        if (!matcher.matches()) {
            throw new BefException(Errors.INVALID_NICKNAME);
        }
    }

    public void validateUsername(String username) {
        Matcher matcher = usernamePattern.matcher(username);
        if (!matcher.matches()) {
            throw new BefException(Errors.INVALID_USERNAME);
        }
    }

    public void validatePassword(String password) {
        Matcher matcher = passwordPattern.matcher(password);
        if (!matcher.matches()) {
            throw new BefException(Errors.INVALID_PASSWORD);
        }
    }
}
