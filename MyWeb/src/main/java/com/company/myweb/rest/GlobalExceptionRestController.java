package com.company.myweb.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;

/**
 * REST 端點的全域例外處理器 — 攔到 REST controller 拋出的例外時，統一回 JSON/XML Response
 * 對照：MVC 例外由 config/GlobalExceptionHandler（@ControllerAdvice）處理，會回 HTML errorPage
 */
@Slf4j
@RestControllerAdvice("com.company.myweb.rest") // 只套用到 rest package 底下的 @RestController
public class GlobalExceptionRestController extends ResponseEntityExceptionHandler {

    // 覆寫父類方法 — 專門處理 @Valid / @Validated 驗證失敗拋出的 MethodArgumentNotValidException
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Response response = new Response(statusCode.toString(), methodArgumentNotValidException.getBindingResult().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // 全域例外處理（處理其餘未被特別分類的 Exception）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex) {
        log.error(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + ex.getMessage());

        // 回傳只帶 body + status 的 ResponseEntity，不加自訂 header
        Response response = new Response("500", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}
