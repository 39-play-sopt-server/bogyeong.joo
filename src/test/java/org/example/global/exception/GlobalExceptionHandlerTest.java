package org.example.global.exception;

import org.example.global.response.ApiResponse;
import org.example.member.controller.MemberController;
import org.example.member.exception.MemberErrorCode;
import org.example.member.service.MemberService;
import org.example.post.controller.PostController;
import org.example.post.exception.PostNotFoundException;
import org.example.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    private MockMvc mvc;
    private MemberService memberService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        postService = mock(PostService.class);
        mvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService),
                        new PostController(postService), new UnexpectedErrorController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void domainErrorsKeepTheirCodesAndStatus() throws Exception {
        when(memberService.create(" ")).thenThrow(new BusinessException(MemberErrorCode.NAME_REQUIRED));
        mvc.perform(post("/api/v1/members").contentType("application/json").content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("NAME_REQUIRED"))
                .andExpect(jsonPath("$.data").isEmpty());
        when(postService.getDetail(1L)).thenThrow(new PostNotFoundException());
        mvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POST_NOT_FOUND"));
    }

    @Test
    void internalIllegalArgumentIsServerErrorAndDoesNotExposeMessage() throws Exception {
        mvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }

    @Test
    void mvcInputErrorsUseGlobalCode() throws Exception {
        mvc.perform(post("/api/v1/members").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(get("/api/v1/posts/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(get("/api/v1/posts").param("category", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        verifyNoInteractions(memberService, postService);
    }

    @Test
    void mvcStatusAndRequiredHeadersArePreserved() throws Exception {
        mvc.perform(post("/test/unexpected"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET"))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
        mvc.perform(post("/api/v1/members").contentType("text/plain").content("name"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @RestController
    static class UnexpectedErrorController {
        @GetMapping("/test/unexpected")
        public ApiResponse<Void> unexpected() {
            throw new IllegalArgumentException("서버 내부의 비공개 상세 정보");
        }
    }
}
