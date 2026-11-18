package org.example.member.controller;

import org.example.member.controller.docs.MemberControllerDocs;
import org.example.global.apiPayload.BaseResponse;
import org.example.member.service.MemberService;
import org.example.member.response.code.MemberSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/members", produces = "application/json")
public class MemberController implements MemberControllerDocs {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Long> me(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberId);
    }

    // 회원 삭제
    @Override
    @DeleteMapping(path = "/{memberId}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable(name = "memberId") long id) {
        service.delete(id);
        return BaseResponse.toResponseEntity(MemberSuccessCode.MEMBER_DELETED, null);
    }
}
