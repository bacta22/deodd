package io.versehub.app.handler.user.authen.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum Errors {
    UNKNOWN(1000, "vh.id.error.unkown", "An unknown error"),
    DUPLICATE_USERNAME(1, "vh.id.duplicate_username", "username already exists"),
    WRONG_PASSWORD(2,  "vh.id.wrong_password","wrong password"),
    WRONG_USERNAME(3, "vh.id.incorrect_account","incorrect account"),
    INVALID_SESSION(4, "vh.id.invalid_session","invalid_session"),
    INVALID_EMAIL(5,"vh.id.invalid_email", "invalid email"),
    WRONG_EMAIL(6, "vh.id.not_exist_email","email does not exist"),
    INVALID_TOKEN(7,"vh.id.invalid_token", "invalid token"),
    EXPIRED_TOKEN(8,"vh.id.token_expired", "token has expired"),
    INPUT_VALIDATE(9,"vh.id.invalid_request_param", "invalid request param"),
    INVALID_GRANT_TYPE(10, "vh.id.invalid_grant_type","invalid grant_type"),
    NONCE_NOT_EXISTS(11,"vh.id.not_exist_nonce", "nonce does not exist"),
    INVALID_NONCE(12, "vh.id.invalid_nonce","invalid nonce"),
    INVALID_SIGNATURE(13,"vh.id.invalid_signature", "invalid signature"),
    ACCOUNT_NOT_EXISTS(14, "vh.id.not_exist_account","account does not exist"),
    ALREADY_HAS_USER(15,"vh.id.already_has_user", "account already has user"),
    ALREADY_HAS_WALLET(16,"vh.id.already_has_wallet", "account already has wallet"),
    WALLET_ALREADY_LINKED(17, "vh.id.wallet_already_linked","wallet already linked"),
    FILE_NOT_FOUND(18, "vh.id.file_not_found", "file not found"),

    ALREADY_HAS_CHARACTER(100,"vh.id.already_has_character", "account already has character"),
    CHARACTER_NOT_EXISTS(101,"vh.id.character_not_exist", "character is not exist"),
    INVALID_NICKNAME(102, "vh.id.invalid_nickname","invalid nickname"),
    DUPLICATE_NICKNAME(103,"vh.id.nickname_already_exists", "nickname already exists"),
    WRONG_AVATAR_FORMAT(104,"vh.id.wrong_avatar_format", "wrong avatar schema format"),

    INVALID_USERNAME(200,"vh.id.invalid_nickname", "invalid nickname"),
    INVALID_PASSWORD(201, "vh.id.invalid_password","invalid password"),

    SUCCESS(0, "vh.id.success", "Success");

    private final int code;
    private final String errorCode;
    private final String errorMessage;
}
