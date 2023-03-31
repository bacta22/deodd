package io.versehub.app.handler.user.authen.metamask;

public class MetamaskSignatureException extends RuntimeException {
    private static final long serialVersionUID = -4024711715289302950L;

    public MetamaskSignatureException(String message) {
        super(message);
    }
}
