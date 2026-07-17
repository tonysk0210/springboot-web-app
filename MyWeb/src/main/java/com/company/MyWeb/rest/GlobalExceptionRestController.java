package com.company.MyWeb.rest;

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

import static com.company.MyWeb.constant.ProjectConstant.ANSI_GREEN;

@Slf4j
@RestControllerAdvice("com.company.myweb.rest") //applied to com.company.myweb.rest
public class GlobalExceptionRestController extends ResponseEntityExceptionHandler {

    //this handler specifically handles throws MethodArgumentNotValidException when a method parameter annotated with @Valid or @Validated fails validation by extending ResponseEntityExceptionHandler class
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Response response = new Response(statusCode.toString(), methodArgumentNotValidException.getBindingResult().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); //code 400
    }

    //This handler handles global Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex) {
        log.error(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + ex.getMessage());

        //this returns a ResponseEntity wrapping Response object with only body and status WITHOUT a header.
        Response response = new Response("500", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); //code 500 (Internal Server Error)
    }
}
