package org.yaojiu.supermarket.exception;

import org.yaojiu.supermarket.entity.Result;

public class NeedLoginException extends BaseException {
    public NeedLoginException() {
        super(Result.FAIL_NEED_LOGIN,"需要登陆");
    }
}
