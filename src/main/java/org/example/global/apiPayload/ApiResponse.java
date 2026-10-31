package org.example.global.apiPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.global.apiPayload.code.ErrorCode;
import org.example.global.apiPayload.code.SuccessCode;
import org.springframework.http.ResponseEntity;

@Schema(description = "공통 응답. HTTP 상태와 별도로 code로 결과를 구분합니다.")
public class ApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "성공 또는 오류 식별 코드", example = "POST_CREATED")
    private final String code;
    @Schema(description = "결과 안내 메시지", example = "게시글이 작성되었습니다.")
    private final String message;
    @Schema(description = "응답 데이터. 실패하거나 반환할 데이터가 없으면 null입니다.", nullable = true)
    private final T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), data);
    }

    public static ApiResponse<Void> failure(ErrorCode code) {
        return new ApiResponse<>(false, code.getCode(), code.getMessage(), null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(SuccessCode code, T data) {
        return ResponseEntity.status(code.getStatus()).body(success(code, data));
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
