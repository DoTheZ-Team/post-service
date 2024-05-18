package com.justdo.plug.post.domain.likes.controller;

import com.justdo.plug.post.domain.likes.service.LikesService;
import com.justdo.plug.post.global.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like API")
@RestController
@RequestMapping("/posts/")
@RequiredArgsConstructor
public class LikesController {

    private final JwtProvider jwtProvider;
    private final LikesService likesService;

    // 게시글 좋아요 등록
    @PostMapping("likes/{postId}")
    @Operation(summary = "특정게시글 좋아요 요창", description = "memberId는 JWT토큰 파싱 예정")
    public String LikePost(@PathVariable Long postId, HttpServletRequest request){


        Long memberId = jwtProvider.getUserIdFromToken(request);
        return likesService.postLike(postId, memberId);

    }

    // 게시글 좋아요 취소
    @DeleteMapping("likes/{postId}")
    @Operation(summary = "특정게시글 좋아요 취소 요청", description = "memberId는 JWT토큰 파싱 예정")
    public String LikeCancelPost(@PathVariable Long postId, HttpServletRequest request){

        Long memberId = jwtProvider.getUserIdFromToken(request);
        return likesService.postLikeCancel(postId, memberId);

    }
}
