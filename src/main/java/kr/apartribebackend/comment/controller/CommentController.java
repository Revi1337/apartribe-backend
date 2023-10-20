package kr.apartribebackend.comment.controller;

import jakarta.validation.Valid;
import kr.apartribebackend.comment.dto.*;
import kr.apartribebackend.comment.service.CommentService;
import kr.apartribebackend.global.dto.APIResponse;
import kr.apartribebackend.global.dto.PageResponse;
import kr.apartribebackend.member.dto.MemberDto;
import kr.apartribebackend.member.principal.AuthenticatedMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/{apartCode}/board/{boardId}/comment")
    public ResponseEntity<APIResponse<SingleCommentResponse>> appendCommentToBoard(
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @PathVariable final String apartCode,
            @PathVariable final Long boardId,
            @Valid @RequestBody final AppendCommentReq appendCommentReq
    ) {
        final MemberDto memberDto = authenticatedMember.toDto();
        final CommentDto commentDto = appendCommentReq.toDto();
        final CommentDto savedCommentDto = commentService
                .appendCommentToBoard(apartCode, memberDto, boardId, commentDto);
        final SingleCommentResponse singleCommentResponse = SingleCommentResponse.from(savedCommentDto);
        final APIResponse<SingleCommentResponse> apiResponse = APIResponse.SUCCESS(singleCommentResponse);
        return ResponseEntity.status(CREATED).body(apiResponse);
    }

    @PostMapping("/api/{apartCode}/board/{boardId}/comment/reply")
    public ResponseEntity<APIResponse<SingleCommentResponse>> appendCommentReplyToBoard(
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @PathVariable final String apartCode,
            @PathVariable final Long boardId,
            @Valid @RequestBody final AppendCommentReplyReq appendCommentReplyReq
    ) {
        final Long parentId = appendCommentReplyReq.parentId();
        final CommentDto commentDto = appendCommentReplyReq.toDto();
        final CommentDto savedCommentDto = commentService
                .appendCommentReplyToBoard(apartCode, boardId, parentId, commentDto);
        final SingleCommentResponse singleCommentResponse = SingleCommentResponse.from(savedCommentDto);
        final APIResponse<SingleCommentResponse> apiResponse = APIResponse.SUCCESS(singleCommentResponse);
        return ResponseEntity.status(CREATED).body(apiResponse);
    }

    @GetMapping({"/api/board/{id}/comment", "/api/board/comment"})
    public APIResponse<PageResponse<CommentRes>> findCommentsByBoardId(
            @PathVariable final Optional<Long> id,
            @PageableDefault final Pageable pageable) {
        final Long boardId = id.orElse(0L);
        final Page<CommentRes> commentsByBoardId = commentService.findCommentsByBoardId(boardId, pageable);
        final PageResponse<CommentRes> pageResponse = PageResponse.from(commentsByBoardId);
        final APIResponse<PageResponse<CommentRes>> apiResponse = APIResponse.SUCCESS(pageResponse);
        return apiResponse;
    }

    @PutMapping("/api/{apartCode}/board/{boardId}/comment")
    public APIResponse<SingleCommentResponse> updateArticle(
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @PathVariable final String apartCode,
            @PathVariable final Long boardId,
            @Valid @RequestBody final UpdateCommentReq updateCommentReq
    ) {
        final CommentDto commentDto = updateCommentReq.toDto();
        final CommentDto updatedCommentDto = commentService.updateCommentForBoard(apartCode, boardId, commentDto);
        final SingleCommentResponse singleCommentResponse = SingleCommentResponse.from(updatedCommentDto);
        final APIResponse<SingleCommentResponse> apiResponse = APIResponse.SUCCESS(singleCommentResponse);
        return apiResponse;
    }

    @GetMapping("/api/{apartCode}/board/comment/best")
    public APIResponse<List<BestCommentResponse>> bestCommentUntilLastWeek(@PathVariable final String apartCode) {
        final List<BestCommentResponse> bestCommentResponses = commentService.bestCommentRankViaLastWeek(apartCode);
        final APIResponse<List<BestCommentResponse>> apiResponse = APIResponse.SUCCESS(bestCommentResponses);
        return apiResponse;
    }

}
