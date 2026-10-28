package org.example.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.member.controller.dto.CreateMemberRequest;
import org.example.member.domain.Member;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member", description = "회원 domain")
public interface MemberControllerDocs {

    @Operation(
            summary = "회원 생성",
            description = "이름을 받아 회원을 생성합니다.")
    ResponseEntity<ApiResponse<Member>> create(CreateMemberRequest request);

    @Operation(
            summary = "회원 삭제",
            description = "회원을 삭제합니다.")
    ResponseEntity<ApiResponse<Void>> delete(long id);
}
