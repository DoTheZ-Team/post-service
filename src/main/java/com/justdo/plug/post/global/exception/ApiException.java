package com.justdo.plug.post.global.exception;

import com.justdo.plug.post.global.response.code.ErrorReasonDto;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;

public class ApiException extends RuntimeException{

    private final ErrorStatus errorStatus;

    public ApiException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorReasonDto getErrorReason() {
        return this.errorStatus.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.errorStatus.getReasonHttpStatus();
    }
}
