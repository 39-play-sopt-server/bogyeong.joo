package org.example.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.global.apiPayload.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member", description = "인증된 회원 ID 조회·회원 삭제 API")
    @ApiResponse(
            responseCode = "500",
            description = "예상하지 못한 서버 오류: INTERNAL_SERVER_ERROR",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = MemberExamples.INTERNAL_SERVER_ERROR)))
public interface MemberControllerDocs {

    @Operation(summary = "인증된 회원 ID 조회")
    @ApiResponse(responseCode = "200", description = "토큰에 담긴 회원 ID", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "토큰이 없거나 유효하지 않음")
    ResponseEntity<Long> me(@Parameter(hidden = true) Long memberId);

    // 회원 삭제
    @Operation(
            summary = "회원 삭제",
            description = """
            회원과 해당 회원이 작성한 게시글을 함께 삭제합니다.
            """)
    @ApiResponse(
            responseCode = "200",
            description = """
            회원 삭제 성공: MEMBER_DELETED.<br>
            data는 null
            """,
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = MemberExamples.MEMBER_DELETED)))
    @ApiResponse(
            responseCode = "400",
            description = "회원 ID 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = MemberExamples.INVALID_REQUEST)))
    @ApiResponse(
            responseCode = "404",
            description = "회원이 없음: MEMBER_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = MemberExamples.MEMBER_NOT_FOUND)))
    ResponseEntity<BaseResponse<Void>> delete(
            @Parameter(description = "삭제할 회원 ID", example = "1", required = true) long id);
}
