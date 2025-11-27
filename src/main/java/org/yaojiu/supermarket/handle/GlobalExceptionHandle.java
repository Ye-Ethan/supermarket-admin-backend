package org.yaojiu.supermarket.handle;

import lombok.extern.slf4j.Slf4j;
import org.yaojiu.supermarket.entity.Result; // 假设你的Result类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse; // SpringBoot 3 使用 jakarta，2 使用 javax
import org.yaojiu.supermarket.exception.BaseException;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {


    /**
     * 处理自定义业务异常 (BaseException)
     * 逻辑：如果 BaseException 里的 code 是标准的 HTTP 状态码 (如 401, 403, 404)，则设置 HTTP 状态。
     * 否则（如 20001 这种业务码），默认返回 HTTP 400 (Bad Request) 或 200 (OK)。
     */
    @ResponseBody
    @ExceptionHandler({BaseException.class})
    public Result commonExceptionHandle(BaseException e, HttpServletResponse response) {
        int code = e.getCode();
        log.info(e.getMessage());
        if (code >= 100 && code < 600) {
            response.setStatus(code);
        } else {
            response.setStatus(400);
        }
        return Result.fail().resetCode(code).resetMsg(e.getMessage());
    }

    /**
     * 处理参数校验异常 (BindException)
     * HTTP 状态码固定为：400 Bad Request
     */
    @ResponseBody
    @ExceptionHandler({BindException.class})
    public Result methodArgumentNotValidHandle(BindException e, HttpServletResponse response) {
        // 设置 HTTP 状态为 400
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        List<FieldError> fieldErrors = e.getFieldErrors();
        HashMap<String, String> errorMap = new HashMap<>();
        fieldErrors.forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return Result.fail()
                .resetMsg("参数错误")
                .resetData(errorMap)
                .resetCode(Result.FAIL_DATA_INVALID); // 假设这是你Result里的常量
    }

    /**
     * 处理所有未知的系统异常 (Exception)
     * HTTP 状态码固定为：500 Internal Server Error
     */
    @ResponseBody
    @ExceptionHandler({Exception.class})
    public Result exceptionHandle(Exception e, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        // 设置 HTTP 状态为 500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        return Result.fail()
                .resetMsg("服务器内部错误") // 对外提示尽量模糊，不要直接 e.getMessage()，防止泄露代码细节
                .resetCode(Result.ERROR_SERVER_ERROR); // 假设你有 500 的常量
    }
    @ResponseBody
    @ExceptionHandler({Error.class})
    public Result errorHandle(Exception e, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return Result.fail()
                .resetMsg("服务器内部错误") // 对外提示尽量模糊，不要直接 e.getMessage()，防止泄露代码细节
                .resetCode(Result.ERROR_SERVER_ERROR); // 假设你有 500 的常量
    }
}