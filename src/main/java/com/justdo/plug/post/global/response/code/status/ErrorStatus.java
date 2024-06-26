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

    // 댓글
    _COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4001", "해당 댓글을 찾을 수 없습니다."),
    _COMMENT_OVER_DEPTH(HttpStatus.BAD_REQUEST, "COMMENT4002", "해당 서비스는 대댓글까지만 작성할 수 있습니다."),

    //해시태그
    _NO_HASHTAGS(HttpStatus.NOT_FOUND, "HASHTAGS404", "해당 사용자가 사용한 해시태그를 찾을 수 없습니다."),

    // 해시태그
    _HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "HASHTAG404", "해당 해시태그를 찾을 수 없습니다."),

    // JWT 관련
    _JWT_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT404", "Header에 JWT가 존재하지 않습니다."),

    // Category 관련
    _CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404", "해당 카테고리가 존재하지 않습니다."),

    // Photo 관련
    _PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, "Photo404", "해당 이미지 경로가 존재하지 않습니다.");


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
