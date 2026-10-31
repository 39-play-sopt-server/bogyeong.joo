package org.example.post.controller.docs;

final class PostExamples {
    private PostExamples() {
    }

    static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "서버 오류가 발생했습니다.",
              "data": null
            }
            """;

    static final String POST_CREATED = """
            {
              "success": true,
              "code": "POST_CREATED",
              "message": "게시글이 작성되었습니다.",
              "data": {
                "id": 1,
                "title": "SOPT 후기",
                "content": "SOPT는 너무 너무 너무 좋당",
                "category": "DAILY",
                "name": "철수",
                "createdAt": "2026-10-28T12:00:00",
                "updatedAt": null,
                "viewCount": 0
              }
            }
            """;

    static final String TITLE_REQUIRED = """
            {
              "success": false,
              "code": "TITLE_REQUIRED",
              "message": "제목은 비어 있을 수 없습니다.",
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

    static final String POST_LIST_FOUND = """
            {
              "success": true,
              "code": "POST_LIST_FOUND",
              "message": "게시글 목록을 조회했습니다.",
              "data": {
                "posts": [
                  {
                    "id": 1,
                    "title": "SOPT 후기",
                    "content": "SOPT는 너무 너무 너무 좋당.",
                    "category": "DAILY",
                    "name": "철수",
                    "createdAt": "2026-10-28T12:00:00",
                    "updatedAt": null,
                    "viewCount": 0
                  }
                ],
                "nextCursor": null,
                "hasNext": false
              }
            }
            """;

    static final String INVALID_PAGE_SIZE = """
            {
              "success": false,
              "code": "INVALID_PAGE_SIZE",
              "message": "size는 1 이상 100 이하여야 합니다.",
              "data": null
            }
            """;

    static final String POST_FOUND = """
            {
              "success": true,
              "code": "POST_FOUND",
              "message": "게시글을 조회했습니다.",
              "data": {
                "id": 1,
                "title": "SOPT 후기",
                "content": "SOPT는 너무 너무 너무 좋당.",
                "category": "DAILY",
                "name": "철수",
                "createdAt": "2026-10-28T12:00:00",
                "updatedAt": null,
                "viewCount": 1
              }
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

    static final String POST_NOT_FOUND = """
            {
              "success": false,
              "code": "POST_NOT_FOUND",
              "message": "존재하지 않는 게시글입니다.",
              "data": null
            }
            """;

    static final String POST_UPDATED = """
            {
              "success": true,
              "code": "POST_UPDATED",
              "message": "게시글이 수정되었습니다.",
              "data": {
                "id": 1,
                "title": "SOPT 후기 수정본",
                "content": "SOPT는 너무 너무 너무 너무 좋당.",
                "category": "INFORMATION",
                "name": "철수",
                "createdAt": "2026-10-28T12:00:00",
                "updatedAt": "2026-10-28T13:00:00",
                "viewCount": 0
              }
            }
            """;

    static final String CONTENT_REQUIRED = """
            {
              "success": false,
              "code": "CONTENT_REQUIRED",
              "message": "본문은 비어 있을 수 없습니다.",
              "data": null
            }
            """;

    static final String POST_DELETED = """
            {
              "success": true,
              "code": "POST_DELETED",
              "message": "게시글이 삭제되었습니다.",
              "data": null
            }
            """;
}
