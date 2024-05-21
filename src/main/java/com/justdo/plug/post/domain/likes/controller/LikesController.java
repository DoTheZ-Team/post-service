package com.justdo.plug.post.domain.likes.controller;

import com.justdo.plug.post.domain.likes.service.LikesService;
import com.justdo.plug.post.global.response.ApiResponse;
import com.justdo.plug.post.global.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like 게시글 좋아요 관련 API입니다.")
@RestController
@RequestMapping("/posts/")
@RequiredArgsConstructor
public class LikesController {

    private final JwtProvider jwtProvider;
    private final LikesService likesService;

    // 게시글 좋아요 등록
    @PostMapping("likes/{postId}")
    @Operation(summary = "특정게시글 좋아요 삭제/생성 요청", description = "해당 게시글에 대해 좋아요를 누르지 않았다면 좋아요 생성, 이미 눌렀다면 좋아요 삭제를 합니다")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> LikePost(@PathVariable Long postId, HttpServletRequest request){


        Long memberId = jwtProvider.getUserIdFromToken(request);
        return ApiResponse.onSuccess(likesService.postLike(postId, memberId));

    }
    /*
    // 게시글 좋아요 취소
    @DeleteMapping("likes/{postId}")
    @Operation(summary = "특정게시글 좋아요 취소 요청", description = "해당 게시글에 대해 좋아요를 취소합니다")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> LikeCancelPost(@PathVariable Long postId, HttpServletRequest request){

        Long memberId = jwtProvider.getUserIdFromToken(request);
        return ApiResponse.onSuccess(likesService.postLikeCancel(postId, memberId));

    }
    */

}
