package org.yaojiu.supermarket.exception;

public class TokenInvalidException extends BaseException {
    public TokenInvalidException() {
        super(401,"Token已过期");
    }
}
