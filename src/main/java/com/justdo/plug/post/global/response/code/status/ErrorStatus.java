package com.justdo.plug.post.global.response.code.status;

import com.justdo.plug.post.global.response.code.BaseErrorCode;
import com.justdo.plug.post.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 게시글 관련 응답
    _POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST404", "해당 게시글을 찾을 수 없습니다."),

    //블로그
    _BLOG_NOT_FOUND(HttpStatus.NOT_FOUND, "BLOG404", "해당 블로그를 찾을 수 없습니다."),

    // 해시태그
    _HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "HASHTAG404", "해당 해시태그를 찾을 수 없습니다.");

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
