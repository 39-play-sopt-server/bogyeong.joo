package org.example.post.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.example.post.controller.dto.CreatePostRequest;
import org.example.post.controller.dto.UpdatePostRequest;
import org.example.post.controller.dto.PostListResponse;
import org.example.post.entity.enums.Category;
import org.example.post.entity.Post;
import java.util.List;

@Tag(name = "Post", description = "게시글 생성·조회·수정·삭제 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "예상하지 못한 서버 오류: INTERNAL_SERVER_ERROR",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.INTERNAL_SERVER_ERROR)))
public interface PostControllerDocs {

    // 게시글 작성
    @Operation(
            summary = "게시글 작성",
            description = """
            제목, 본문, 카테고리와 기존 회원 ID를 받아 게시글을 작성합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "게시글 작성 성공: POST_CREATED",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PostExamples.POST_CREATED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = """
            입력 누락: TITLE_REQUIRED, CONTENT_REQUIRED, CATEGORY_REQUIRED, AUTHOR_REQUIRED<br>
            JSON·필드 타입 오류: INVALID_REQUEST
            """,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.TITLE_REQUIRED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "작성자 회원이 없음: MEMBER_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.MEMBER_NOT_FOUND)))
    ResponseEntity<ApiResponse<Post>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "제목·본문·카테고리·회원 ID는 필수입니다.", required = true) CreatePostRequest request);

    // 게시글 목록 조회
    @Operation(
            summary = "게시글 목록 조회",
            description = """
            계시글 목록을 반환합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = """
            목록 조회 성공: POST_LIST_FOUND.<br>
            결과가 없으면 posts=[], nextCursor=null, hasNext=false
            """,
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PostExamples.POST_LIST_FOUND)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = """
            커서 오류: INVALID_CURSOR<br>
            조회 크기 오류: INVALID_PAGE_SIZE<br>
            타입·카테고리 값 오류: INVALID_REQUEST
            """,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.INVALID_PAGE_SIZE)))
    ResponseEntity<ApiResponse<PostListResponse>> findAll(
            @Parameter(name = "cursor", description = """
            이전 응답의 nextCursor.<br>
            첫 조회에서는 생략합니다.
            """) String cursor,
            @Parameter(name = "size", description = """
            조회 개수.<br>
            기본값 10, 범위 1~100
            """, schema = @Schema(defaultValue = "10", minimum = "1", maximum = "100")) int size,
            @Parameter(name = "category", description = """
            생략하면 전체 카테고리 조회.<br>
            DAILY(일상), QUESTION(질문), INFORMATION(정보)
            """, example = "DAILY") Category category
    );

    // 게시글 검색
    @Operation(
            summary = "게시글 검색",
            description = """
            게시글을 검색합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "검색 성공: POST_SEARCHED. data에 일치하는 게시글 목록 반환",
            useReturnTypeSchema = true)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "공백 검색어: SEARCH_KEYWORD_REQUIRED / 회원 ID 타입·카테고리 값 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<List<Post>>> search(
            @Parameter(description = "제목에 포함된 검색어. 생략하면 제목 조건 제외", example = "세미나") String keyword,
            @Parameter(description = "작성자 회원 ID. 생략하면 작성자 조건 제외", example = "1") Long memberId,
            @Parameter(description = "카테고리. 생략하면 카테고리 조건 제외", example = "DAILY") Category category);

    // 게시글 단건 조회
    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "상세 조회 성공: POST_FOUND",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PostExamples.POST_FOUND)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "게시글 ID 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.INVALID_REQUEST)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "게시글이 없음: POST_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.POST_NOT_FOUND)))
    ResponseEntity<ApiResponse<Post>> findById(
            @Parameter(description = "게시글 ID", example = "1", required = true) long id);

    // 게시글 수정
    @Operation(
            summary = "게시글 수정",
            description = """
            제목, 본문, 카테고리를 모두 전달해 수정합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공: POST_UPDATED",
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PostExamples.POST_UPDATED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = """
            입력 누락: TITLE_REQUIRED, CONTENT_REQUIRED, CATEGORY_REQUIRED<br>
            요청 형식 오류: INVALID_REQUEST
            """,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.CONTENT_REQUIRED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "게시글이 없음: POST_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.POST_NOT_FOUND)))
    ResponseEntity<ApiResponse<Post>> update(
            @Parameter(description = "게시글 ID", example = "1", required = true) long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "제목·본문·카테고리를 모두 전달합니다.", required = true) UpdatePostRequest request);

    // 게시글 삭제
    @Operation(
            summary = "게시글 삭제",
            description = """
            게시글을 삭제합니다.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = """
            삭제 성공: POST_DELETED.<br>
            data는 null
            """,
            useReturnTypeSchema = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PostExamples.POST_DELETED)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "게시글 ID 타입 오류: INVALID_REQUEST",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.INVALID_REQUEST)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "게시글이 없음: POST_NOT_FOUND",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = PostExamples.POST_NOT_FOUND)))
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "게시글 ID", example = "1", required = true) long id);
}
