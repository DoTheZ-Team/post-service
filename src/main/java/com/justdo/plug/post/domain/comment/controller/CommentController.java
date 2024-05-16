package com.justdo.plug.post.domain.comment.controller;

import com.justdo.plug.post.domain.comment.dto.CommentRequest;
import com.justdo.plug.post.domain.comment.dto.CommentResponse.CommentProc;
import com.justdo.plug.post.domain.comment.dto.CommentVO;
import com.justdo.plug.post.domain.comment.service.CommentService;
import com.justdo.plug.post.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post Comment 댓글 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 페이지 - 게시글에 댓글 작성 API", description = "Post의 Comment를 작성합니다.")
    @Parameter(name = "postId", description = "포스트의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @PostMapping("/{postId}")
    public ApiResponse<CommentProc> post(HttpServletRequest request, @RequestBody
    CommentRequest.PostComment post, @PathVariable Long postId) {

        // TODO: JwtProvider 추가 후 수정
        Long memberId = 1L;
        CommentVO commentVO = CommentVO.of(memberId, post, postId);

        return ApiResponse.onSuccess(commentService.writeComment(commentVO));
    }

    @Operation(summary = "댓글 페이지 - 게시글에 댓글 수정 API", description = "Post의 Comment를 수정합니다.")
    @Parameter(name = "commentId", description = "댓글의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @PatchMapping("/{commentId}")
    public ApiResponse<CommentProc> patch(HttpServletRequest request, @PathVariable Long commentId,
            @RequestBody String content) {

        return ApiResponse.onSuccess(commentService.patchComment(commentId, content));
    }


}
