package org.yaojiu.supermarket.exception;

import org.yaojiu.supermarket.entity.Result;

public class InvalidDataException extends BaseException {
    public InvalidDataException() {
        super(Result.FAIL_DATA_INVALID, "无效的数据信息");
    }
}
