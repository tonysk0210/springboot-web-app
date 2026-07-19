package com.company.myweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 註冊或更新使用者時，若 email 已被其他帳號使用會拋此例外
 * @ResponseStatus：REST 端點若拋出此例外，Spring 自動回 409 Conflict（無需另外處理）
 */
@ResponseStatus(HttpStatus.CONFLICT)  // 409 Conflict
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
