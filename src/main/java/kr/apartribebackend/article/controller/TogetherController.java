package kr.apartribebackend.article.controller;

import jakarta.validation.Valid;
import kr.apartribebackend.global.annotation.ApartUser;
import kr.apartribebackend.likes.dto.BoardLikedRes;
import kr.apartribebackend.article.dto.together.*;
import kr.apartribebackend.article.service.TogetherService;
import kr.apartribebackend.global.dto.APIResponse;
import kr.apartribebackend.global.dto.PageResponse;
import kr.apartribebackend.member.dto.MemberDto;
import kr.apartribebackend.member.principal.AuthenticatedMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@RequiredArgsConstructor
@RestController
public class TogetherController {

    private final TogetherService togetherService;

    @GetMapping("/api/{apartId}/together/{togetherId}")
    public APIResponse<SingleTogetherWithLikedResponse> findSingleTogether(
            @PathVariable final String apartId,
            @PathVariable final Long togetherId,
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember
    ) {
        final SingleTogetherWithLikedResponse singleTogetherWithLikedResponse = togetherService
                .findSingleTogetherById(authenticatedMember.toDto(), apartId, togetherId);
        final APIResponse<SingleTogetherWithLikedResponse> apiResponse = APIResponse.SUCCESS(singleTogetherWithLikedResponse);
        return apiResponse;
    }

    @GetMapping("/api/{apartId}/together")
    public APIResponse<PageResponse<TogetherResponse>> findMultipleTogethers(
            @PathVariable final String apartId,
            @RequestParam(required = false, defaultValue = "") final String category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        final Page<TogetherResponse> togetherResponses =
                togetherService.findMultipleTogethersByCategory(apartId, category, pageable);
        final PageResponse<TogetherResponse> pageResponse = PageResponse.from(togetherResponses);
        final APIResponse<PageResponse<TogetherResponse>> apiResponse = APIResponse.SUCCESS(pageResponse);
        return apiResponse;
    }

    @ApartUser
    @PostMapping("/api/{apartId}/together")
    public ResponseEntity<Void> appendTogether(
            @PathVariable final String apartId,
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @Valid @RequestBody final AppendTogetherReq appendTogetherReq
    ) {
        togetherService.appendTogether(
                apartId,
                appendTogetherReq.category(),
                authenticatedMember.toDto(),
                appendTogetherReq.toDto()
        );
        return ResponseEntity.status(CREATED).build();
    }

    @ApartUser
    @PostMapping(value = "/api/{apartId}/together/attach", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> attachmentToAWS(
            @PathVariable final String apartId,
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @Valid @RequestPart final AppendTogetherReq togetherInfo,
            @RequestPart(required = false) final List<MultipartFile> file) throws IOException {
        final String category = togetherInfo.category();
        final MemberDto memberDto = authenticatedMember.toDto();
        final TogetherDto togetherDto = togetherInfo.toDto();
        if (file != null)
            togetherService.appendTogether(apartId, category, memberDto, togetherDto, file);
        else
            togetherService.appendTogether(apartId, category, memberDto, togetherDto);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/api/{apartId}/together/{togetherId}/like")
    public APIResponse<BoardLikedRes> updateLikeByBoardId(
            @PathVariable final String apartId,
            @PathVariable final Long togetherId,
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember
    ) {
        final BoardLikedRes boardLikedRes = togetherService
                .updateLikeByTogetherId(authenticatedMember.toDto(), apartId, togetherId);
        final APIResponse<BoardLikedRes> apiResponse = APIResponse.SUCCESS(boardLikedRes);
        return apiResponse;
    }

    @ApartUser
    @PutMapping("/api/{apartId}/together/{togetherId}")
    public APIResponse<SingleTogetherResponse> updateTogether(
            @PathVariable final String apartId,
            @PathVariable final Long togetherId,
            @AuthenticationPrincipal final AuthenticatedMember authenticatedMember,
            @Valid @RequestBody final UpdateTogetherReq updateTogetherReq
    ) {
        final SingleTogetherResponse singleTogetherResponse = togetherService.updateTogether(
                apartId,
                togetherId,
                updateTogetherReq.category(),
                updateTogetherReq.toDto(),
                authenticatedMember.toDto()
        );
        final APIResponse<SingleTogetherResponse> apiResponse = APIResponse.SUCCESS(singleTogetherResponse);
        return apiResponse;
    }

}


//    @GetMapping("/api/{apartId}/together/{togetherId}")
//    public APIResponse<SingleTogetherResponse> findSingleTogether(
//            @PathVariable final String apartId,
//            @PathVariable final Long togetherId,
//    ) {
//        final SingleTogetherResponse singleArticleById = togetherService.findSingleTogetherById(apartId, togetherId);
//        final APIResponse<SingleTogetherResponse> apiResponse = APIResponse.SUCCESS(singleArticleById);
//        return apiResponse;
//    }
