package org.example.global.exception;

import org.example.global.response.ApiResponse;
import org.example.global.response.code.ErrorCode;
import org.example.global.response.code.GlobalErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.failure(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("요청 처리 중 예상하지 못한 오류가 발생했습니다.", e);
        ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.failure(code));
    }

    // Spring MVC가 정한 상태와 Allow 등 응답 헤더는 유지하고 본문만 공통 형식으로 바꿉니다.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        GlobalErrorCode code = switch (status.value()) {
            case 404 -> GlobalErrorCode.RESOURCE_NOT_FOUND;
            case 405 -> GlobalErrorCode.METHOD_NOT_ALLOWED;
            case 406 -> GlobalErrorCode.NOT_ACCEPTABLE;
            case 415 -> GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> status.is5xxServerError()
                    ? GlobalErrorCode.INTERNAL_SERVER_ERROR : GlobalErrorCode.INVALID_REQUEST;
        };
        if (status.is5xxServerError()) {
            log.error("Spring MVC 요청 처리 중 오류가 발생했습니다.", e);
        }
        return super.handleExceptionInternal(e, ApiResponse.failure(code), headers, status, request);
    }
}
