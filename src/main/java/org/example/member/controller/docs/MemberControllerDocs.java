package org.example.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.example.member.controller.dto.CreateMemberRequest;
import org.example.member.entity.Member;

@Tag(name = "Member", description = "회원 생성·삭제 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "예상하지 못한 서버 오류: INTERNAL_SERVER_ERROR",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = MemberExamples.INTERNAL_SERVER_ERROR)))
public interface MemberControllerDocs {

    // 회원 생성
    @Operation(
            summary = "회원 생성",
            description = """
            이름을 받아 회원을 생성합니다.<br>
            동명이인을 허용하며 응답의 회원 ID로 게시글 작성자를 지정합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "회원 생성 성공: MEMBER_CREATED",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = MemberExamples.MEMBER_CREATED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = """
            이름 누락·공백: NAME_REQUIRED<br>
            JSON·필드 타입 오류: INVALID_REQUEST
            """,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = MemberExamples.NAME_REQUIRED)))
    ResponseEntity<ApiResponse<Member>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 이름을 전달합니다.", required = true) CreateMemberRequest request);

    // 회원 삭제
    @Operation(
            summary = "회원 삭제",
            description = """
            회원과 해당 회원이 작성한 게시글을 하나의 트랜잭션에서 함께 삭제합니다.<br>
            같은 이름의 다른 회원과 그 게시글은 삭제하지 않습니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = """
            회원 삭제 성공: MEMBER_DELETED.<br>
            data는 null
            """,
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = MemberExamples.MEMBER_DELETED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "회원 ID 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = MemberExamples.INVALID_REQUEST)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "회원이 없음: MEMBER_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = MemberExamples.MEMBER_NOT_FOUND)))
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "삭제할 회원 ID", example = "1", required = true) long id);
}
