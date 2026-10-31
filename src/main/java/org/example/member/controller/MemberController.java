package org.example.member.controller;

import org.example.member.controller.docs.MemberControllerDocs;
import org.example.member.controller.dto.CreateMemberRequest;
import org.example.member.entity.Member;
import org.example.global.apiPayload.ApiResponse;
import org.example.member.service.MemberService;
import org.example.member.response.code.MemberSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/members", produces = "application/json")
public class MemberController implements MemberControllerDocs {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    // 회원 생성
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Member>> create(@RequestBody CreateMemberRequest request) {
        Member member = service.create(request.name());
        return ApiResponse.toResponseEntity(MemberSuccessCode.MEMBER_CREATED, member);
    }

    // 회원 삭제
    @Override
    @DeleteMapping(path = "/{memberId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "memberId") long id) {
        service.delete(id);
        return ApiResponse.toResponseEntity(MemberSuccessCode.MEMBER_DELETED, null);
    }
}
