package com.justdo.plug.post.domain.comment.controller;

import com.justdo.plug.post.domain.comment.dto.CommentRequest;
import com.justdo.plug.post.domain.comment.dto.CommentResponse;
import com.justdo.plug.post.domain.comment.dto.CommentResponse.CommentProc;
import com.justdo.plug.post.domain.comment.dto.CommentVO;
import com.justdo.plug.post.domain.comment.service.CommentService;
import com.justdo.plug.post.global.response.ApiResponse;
import com.justdo.plug.post.global.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post Comment 댓글 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "댓글 조회 페이지 - 게시글에 작성된 부모 댓글 조회 API", description = "Post의 Comment 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "포스트의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH),
            @Parameter(name = "page", description = "페이징 번호 page, Query String입니다.", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이징 크기 size, Query String입니다.", in = ParameterIn.QUERY)
    })
    @GetMapping("/{postId}")
    public ApiResponse<CommentResponse.CommentResult> getComments(@PathVariable Long postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt"));

        return ApiResponse.onSuccess(commentService.findComments(postId, pageRequest));
    }

    @Operation(summary = "댓글 조회 페이지 - 게시글에 작성된 특정 댓글의 자식 댓글 조회 API", description = "Post의 자식 Comment 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이징 번호 page, Query String입니다.", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이징 크기 size, Query String입니다.", in = ParameterIn.QUERY)
    })
    @GetMapping("/childs/{commentId}")
    public ApiResponse<CommentResponse.CommentResult> getChildComments(@PathVariable Long commentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt"));

        return ApiResponse.onSuccess(commentService.findChildComments(commentId, pageRequest));
    }

    @Operation(summary = "댓글 작성 페이지 - 게시글에 댓글 작성 API", description = "Post의 Comment를 작성합니다.")
    @Parameter(name = "postId", description = "포스트의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @PostMapping("/{postId}")
    public ApiResponse<CommentProc> post(HttpServletRequest request, @RequestBody
    CommentRequest.PostComment post, @PathVariable Long postId) {

        Long memberId = jwtProvider.getUserIdFromToken(request);
        CommentVO commentVO = CommentVO.of(memberId, post, postId);

        return ApiResponse.onSuccess(commentService.writeComment(commentVO));
    }

    @Operation(summary = "댓글 수정 페이지 - 게시글에 댓글 수정 API", description = "Post의 Comment를 수정합니다.")
    @Parameter(name = "commentId", description = "댓글의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @PatchMapping("/{commentId}")
    public ApiResponse<CommentProc> patch(@PathVariable Long commentId,
            @RequestBody String content) {

        return ApiResponse.onSuccess(commentService.patchComment(commentId, content));
    }

    @Operation(summary = "댓글 삭제 - 게시글에 댓글 수정 API", description = "Post의 Comment를 삭제합니다.")
    @Parameter(name = "commentId", description = "댓글의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @DeleteMapping("/{commentId}")
    public ApiResponse<CommentProc> delete(@PathVariable Long commentId) {

        return ApiResponse.onSuccess(commentService.deleteComment(commentId));
    }
}
