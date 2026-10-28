package org.example.member.controller.docs;

final class MemberExamples {
    private MemberExamples() {
    }

    static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "서버 오류가 발생했습니다.",
              "data": null
            }
            """;

    static final String MEMBER_CREATED = """
            {
              "success": true,
              "code": "MEMBER_CREATED",
              "message": "회원이 생성되었습니다.",
              "data": {
                "id": 1,
                "name": "보경",
                "createdAt": "2026-10-28T12:00:00"
              }
            }
            """;

    static final String NAME_REQUIRED = """
            {
              "success": false,
              "code": "NAME_REQUIRED",
              "message": "이름은 비어 있을 수 없습니다.",
              "data": null
            }
            """;

    static final String MEMBER_DELETED = """
            {
              "success": true,
              "code": "MEMBER_DELETED",
              "message": "회원이 삭제되었습니다.",
              "data": null
            }
            """;

    static final String INVALID_REQUEST = """
            {
              "success": false,
              "code": "INVALID_REQUEST",
              "message": "요청 형식과 필드 값을 확인해주세요.",
              "data": null
            }
            """;

    static final String MEMBER_NOT_FOUND = """
            {
              "success": false,
              "code": "MEMBER_NOT_FOUND",
              "message": "존재하지 않는 회원입니다.",
              "data": null
            }
            """;
}
