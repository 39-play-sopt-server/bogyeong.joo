package org.example.auth.controller.docs;

final class AuthExamples {
    private AuthExamples() {
    }

    static final String SIGNUP_REQUEST = """
            {
              "name": "철수",
              "email": "chulsu@example.com",
              "password": "sopt1234!"
            }
            """;

    static final String LOGIN_REQUEST = """
            {
              "email": "chulsu@example.com",
              "password": "sopt1234!"
            }
            """;

    static final String SIGNUP_SUCCESS = """
            {
              "success": true,
              "code": "MEMBER_CREATED",
              "message": "회원이 생성되었습니다.",
              "data": {
                "id": 1
              }
            }
            """;

    static final String LOGIN_SUCCESS = """
            {
              "success": true,
              "code": "LOGIN_SUCCESS",
              "message": "로그인에 성공했습니다.",
              "data": {
                "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.example-signature"
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

    static final String NAME_TOO_LONG = """
            {
              "success": false,
              "code": "NAME_TOO_LONG",
              "message": "이름은 50자 이하여야 합니다.",
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

    static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "서버 오류가 발생했습니다.",
              "data": null
            }
            """;
}
