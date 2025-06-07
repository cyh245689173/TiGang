package com.copico.common.exception;

import com.copico.common.base.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author 陈玉皓
 * @date 2025/6/7 11:17
 * @description: 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    public RestResult<?> doBiz(BizException exception) {
        log.error("业务异常", exception);
        return RestResult.fail(exception.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public RestResult<?> doBiz(IllegalArgumentException
                                       exception) {
        log.error("参数检査结果异常", exception);
        return RestResult.fail(exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public RestResult<?> doArgumentNotValid(MethodArgumentNotValidException exception) {
        log.error("参数不匹配", exception);
        return RestResult.fail(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }
}