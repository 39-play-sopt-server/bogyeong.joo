# 응답과 예외 처리

- 응답은 success, code, message, data 필드를 사용합니다.
- HTTP 상태는 요청 처리 결과의 종류를 나타내고, code는 구체적인 성공·실패 상황을 구분합니다. 클라이언트는 message 문자열 대신 code를 기준으로 분기합니다.
- 실패하거나 반환할 데이터가 없으면 data는 null입니다.
- ErrorCode와 SuccessCode는 HTTP 상태·코드·메시지를 제공하는 공통 인터페이스입니다. enum 상수 이름을 응답 코드로 사용하므로 이름 변경은 API 계약 변경으로 다룹니다.

## 책임과 위치

- global/response/ApiResponse: 공통 응답 본문 생성과 성공 응답의 ResponseEntity 생성.
- global/response/code: 공통 코드 인터페이스와 GlobalErrorCode.
- global/exception/BusinessException: 의도적으로 발생시키는 업무 오류의 ErrorCode 전달.
- global/exception/GlobalExceptionHandler: 업무 예외와 예상하지 못한 오류, Spring MVC 예외를 HTTP 응답으로 변환.
- member/exception/MemberErrorCode: 회원 관련 오류. MemberNotFoundException은 BusinessException을 상속합니다.
- post/exception/PostErrorCode: 게시글 입력·조회·커서 관련 오류. PostNotFoundException은 BusinessException을 상속합니다.
- member/response/code, post/response/code: 도메인별 성공 코드.
- 새 업무 오류는 해당 도메인의 ErrorCode enum에 추가합니다. 공통 BusinessException으로 전달하면 글로벌 핸들러에 개별 메서드를 추가하지 않아도 됩니다.

## 성공 코드

- MEMBER_CREATED: 201, 회원 생성.
- MEMBER_DELETED: 200, 회원 및 해당 회원의 게시글 삭제.
- POST_CREATED: 201, 게시글 작성.
- POST_LIST_FOUND: 200, 커서 목록 조회. 결과가 없어도 성공입니다.
- POST_FOUND: 200, 상세 조회.
- POST_UPDATED: 200, 게시글 수정.
- POST_DELETED: 200, 게시글 삭제.

## 오류 코드

- 회원: MEMBER_NOT_FOUND는 404, NAME_REQUIRED는 400입니다.
- 게시글: POST_NOT_FOUND는 404입니다. TITLE_REQUIRED, CONTENT_REQUIRED, CATEGORY_REQUIRED, AUTHOR_REQUIRED, INVALID_CURSOR, INVALID_PAGE_SIZE는 400입니다.
- 글로벌 요청 오류: JSON 구문·타입 오류와 파라미터 변환 오류 등은 INVALID_REQUEST로 처리합니다.
- 글로벌 HTTP 오류: RESOURCE_NOT_FOUND(404), METHOD_NOT_ALLOWED(405), NOT_ACCEPTABLE(406), UNSUPPORTED_MEDIA_TYPE(415)를 구분합니다. 그 외 Spring MVC 오류도 원래 HTTP 상태와 필요한 헤더를 유지합니다.
- 예상하지 못한 오류: INTERNAL_SERVER_ERROR(500)로 응답하고 서버 로그에 상세 예외를 기록합니다. 내부 예외 메시지는 응답에 노출하지 않습니다.
- 일반 IllegalArgumentException을 일괄 400으로 매핑하지 않습니다. 요청 검증 실패가 명확한 위치에서 BusinessException과 해당 도메인 코드를 사용합니다.
- PostCursor 안의 파싱 오류는 해당 위치에서 INVALID_CURSOR 업무 오류로 변환합니다. 서버 전체의 IllegalArgumentException 처리와는 구분합니다.

## Swagger 문서

- 문서 인터페이스에서 Operation과 Tag로 기능을 설명합니다.
- 상태별 ApiResponse로 성공·입력 오류·리소스 없음·서버 오류를 설명하고 JSON 예시를 제공합니다.
- 성공 응답 스키마는 각 메서드의 제네릭 반환 타입을 사용합니다. 실패 응답은 공통 응답 형식과 오류 예시를 표시합니다.
- Parameter로 ID 예시, 커서 전달 방식, size 기본값·범위, 카테고리 값을 설명합니다.
- 요청 record의 Schema로 필드 설명·예시·필수 여부를 표시합니다. Schema는 문서 정보이며 실제 검증은 도메인과 서비스에서 수행합니다.
- ApiResponse와 PostListResponse에도 필드 설명을 추가했습니다. nextCursor=null과 hasNext=false의 의미를 문서화합니다.
- Swagger UI는 /swagger-ui/index.html, OpenAPI JSON은 /v3/api-docs에서 확인할 수 있습니다.

## 게시글 제목 검색

- GET /api/v1/posts?title=세미나 로 제목에 '세미나'가 포함된 게시글을 조회합니다. JPQL LIKE로 부분 일치를 검색합니다.
- title은 선택 파라미터입니다. 앞뒤 공백을 제거하고, 생략하거나 공백만 전달하면 제목 조건을 적용하지 않습니다.
- %, _는 와일드카드로 확장하지 않고 실제 문자로 검색합니다. 대소문자 구분은 DB의 collation 설정에 따릅니다.
- category, size, cursor와 함께 사용할 수 있습니다. 검색어나 카테고리를 바꾸면 cursor를 생략해 첫 목록부터 조회합니다.
- 응답 구조와 POST_LIST_FOUND 코드는 기존 목록 조회와 같습니다. 일치하는 글이 없으면 posts=[], nextCursor=null, hasNext=false입니다.
- 검색 시 조회수는 증가하지 않습니다. LIKE 앞뒤에 %를 붙이므로 일반 B-tree 인덱스로 검색 범위를 효율적으로 좁히기 어렵고, 이번 변경에는 검색 인덱스 최적화를 포함하지 않습니다.

## 기존 응답과 달라진 점

- 공통 성공 코드 SUCCESS를 작업별 성공 코드로 변경했습니다.
- INVALID_INPUT으로 묶던 입력 오류를 도메인별 코드와 INVALID_REQUEST로 분리했습니다.
- 회원·게시글 없음 메시지는 코드에 정의한 고정 메시지를 사용합니다. 기존 메시지의 ID 표기는 제거했습니다.
- 기존 URL, HTTP 메서드, 요청 필드, success/code/message/data 구조는 유지합니다.
- 응답의 Post 엔티티를 DTO로 바꾸는 작업은 이 변경에 포함하지 않습니다.

## 참고한 Swagger 구성과의 차이

- 정헌님 feat/9/homework2의 PostApi는 주로 Operation, Tag와 일부 ID Parameter를 사용합니다. 상태별 응답 설명은 확인한 인터페이스에 없습니다.
- 해령님 main의 PostApi는 상태별 ApiResponse 설명과 ID 예시를 사용하며, 요청 record의 Schema로 필드 예시를 제공합니다.
- 이 프로젝트에는 두 방식에 더해 응답 JSON 예시, 공통 서버 오류, 필수 요청 필드, 커서 응답과 파라미터 설명을 보강했습니다.
- 참고: [정헌님 PostApi](https://github.com/39-play-sopt-server/jeongheon.lee/blob/ff1df9fdd9ae95cfbb7b69015874321ee8b7c940/src/main/java/org/sopt/post/presentation/docs/PostApi.java), [해령님 PostApi](https://github.com/39-play-sopt-server/haeryeong.lee/blob/d56dcb05e3de3186b1aa7f912a192f1712788146/src/main/java/org/sopt/server/adapter/in/PostApi.java), [해령님 요청 Schema](https://github.com/39-play-sopt-server/haeryeong.lee/blob/d56dcb05e3de3186b1aa7f912a192f1712788146/src/main/java/org/sopt/server/adapter/in/PostCreateRequest.java).
