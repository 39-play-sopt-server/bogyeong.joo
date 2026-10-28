package org.example.member.controller;

import org.example.member.controller.docs.MemberControllerDocs;
import org.example.member.controller.dto.CreateMemberRequest;
import org.example.member.domain.Member;
import org.example.global.response.ApiResponse;
import org.example.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/members")
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원이 생성되었습니다.", member));
    }

    // 회원 삭제
    @Override
    @DeleteMapping(path = "/{memberId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "memberId") long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("회원이 삭제되었습니다.", null));
    }
}
