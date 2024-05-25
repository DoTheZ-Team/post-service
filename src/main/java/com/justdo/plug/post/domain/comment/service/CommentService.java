package com.justdo.plug.post.domain.comment.service;

import com.justdo.plug.post.domain.blog.BlogClient;
import com.justdo.plug.post.domain.blog.BlogDto.BlogInfo;
import com.justdo.plug.post.domain.comment.Comment;
import com.justdo.plug.post.domain.comment.dto.CommentRequest;
import com.justdo.plug.post.domain.comment.dto.CommentResponse;
import com.justdo.plug.post.domain.comment.dto.CommentResponse.CommentItemList;
import com.justdo.plug.post.domain.comment.dto.CommentResponse.CommentProc;
import com.justdo.plug.post.domain.comment.dto.CommentVO;
import com.justdo.plug.post.domain.comment.repository.CommentRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.service.PostService;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final BlogClient blogClient;

    @Transactional
    public CommentProc writeComment(CommentVO commentVO) {
        Comment comment = commentRepository.save(createComment(commentVO));

        return CommentResponse.toCommentProc(comment);
    }

    private Comment createComment(CommentVO commentVO) {
        Long memberId = commentVO.memberId();
        Post post = postService.getPost(commentVO.postId());
        String content = commentVO.content();
        Long parentId = commentVO.parentId();

        if (parentId != null) {
            Comment parentComment = getParentComment(parentId);
            return CommentRequest.toEntity(memberId, post, content, parentComment);
        }
        return CommentRequest.toEntity(memberId, post, content, null);
    }

    public Comment getParentComment(Long parentId) {

        Comment parentComment = commentRepository.findById(parentId).orElseThrow(
                () -> new ApiException(ErrorStatus._COMMENT_NOT_FOUND)
        );

        if (parentComment.getParentComment() != null) {
            throw new ApiException(ErrorStatus._COMMENT_OVER_DEPTH);
        }
        return parentComment;
    }

    @Transactional
    public CommentProc patchComment(Long commentId, String content) {
        Comment comment = getComment(commentId);
        comment.update(content);
        return CommentResponse.toCommentProc(comment);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new ApiException(ErrorStatus._COMMENT_NOT_FOUND)
        );
    }

    @Transactional
    public CommentProc deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);

        return CommentResponse.toCommentProc(comment);
    }

    public CommentItemList findComments(Long postId, PageRequest pageRequest) {

        Page<Comment> commentPage = commentRepository.findAllByPostIdAndParentCommentIsNull(
                postId, pageRequest);

        List<Long> memberIdList = commentPage.stream()
                .map(Comment::getMemberId)
                .toList();

        List<BlogInfo> blogInfoList = findBlogInfoList(memberIdList);

        return CommentResponse.toCommentItemList(commentPage, blogInfoList, 0L);
    }

    private List<Long> getCommentIds(Page<Comment> commentPage) {

        return commentPage.stream()
                .map(Comment::getId)
                .toList();
    }

    private List<BlogInfo> findBlogInfoList(List<Long> memberIdList) {

        return blogClient.findBlogInfoToComment(memberIdList);
    }

    /**
     * Child Comment 조회
     */
    public CommentItemList findChildComments(Long parentId, PageRequest pageRequest) {

        Page<Comment> commentPage = commentRepository.findAllByParentCommentId(parentId,
                pageRequest);

        List<Long> memberIdList = commentPage.stream()
                .map(Comment::getMemberId)
                .toList();
        List<BlogInfo> blogInfoList = findBlogInfoList(memberIdList);

        return CommentResponse.toCommentItemList(commentPage, blogInfoList, parentId);
    }
}
