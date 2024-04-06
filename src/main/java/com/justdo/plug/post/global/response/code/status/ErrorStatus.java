package com.justdo.plug.post.global.response.code.status;

import com.justdo.plug.post.global.response.code.BaseErrorCode;
import com.justdo.plug.post.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "사용하시려는 해시태그는 사용할 수 없습니다"),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다.");

    // ================================================================================================================= //

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
            .isSuccess(false)
            .code(code)
            .message(message)
            .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
            .httpStatus(httpStatus)
            .isSuccess(false)
            .code(code)
            .message(message)
            .build();
    }
}