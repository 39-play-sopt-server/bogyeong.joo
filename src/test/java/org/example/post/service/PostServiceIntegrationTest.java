package org.example.post.service;

import org.example.post.domain.Category;
import org.example.post.domain.Post;
import org.example.member.service.MemberService;
import org.example.post.exception.PostNotFoundException;
import org.example.post.repository.PostRepository;
import org.example.member.repository.MemberRepository;
import org.example.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import org.example.member.controller.MemberController;
import org.example.post.controller.PostController;
import org.example.global.exception.GlobalExceptionHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:post-service-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.show-sql=false",
                "spring.jpa.open-in-view=false"
        }
)
class PostServiceIntegrationTest {
    @Autowired
    private PostService service;

    @Autowired
    private PostRepository repository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbc;

    private Long memberId;

    @BeforeEach
    void clearPosts() {
        repository.deleteAll();
        memberRepository.deleteAll();
        memberId = memberService.create("작성자").getId();
    }

    // 테스트에 트랜잭션을 붙이지 않아 서비스가 커밋한 결과를 새 조회로 확인합니다.
    @Test
    void baseEntityPersistsCreationTimeForBothDomains() {
        var memberCreatedAt = memberRepository.findById(memberId).orElseThrow().getCreatedAt();
        assertNotNull(memberCreatedAt);

        Post post = service.create("제목", "본문", Category.DAILY, memberId);
        var postCreatedAt = repository.findById(post.getId()).orElseThrow().getCreatedAt();
        assertNotNull(postCreatedAt);

        service.update(post.getId(), "수정 제목", "수정 본문", Category.QUESTION);
        assertEquals(postCreatedAt, repository.findById(post.getId()).orElseThrow().getCreatedAt());
        assertEquals(memberCreatedAt, memberRepository.findById(memberId).orElseThrow().getCreatedAt());
    }

    @Test
    void memberAndPostApisUseExistingResponseFormat() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(
                new MemberController(memberService),
                new PostController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/api/v1/members")
                        .contentType("application/json").content("{\"name\":\"작성자\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("작성자"));

        Post post = service.create("제목", "본문", Category.DAILY, memberId);
        mvc.perform(get("/api/v1/posts").param("size", "1").param("category", "DAILY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts[0].name").value("작성자"))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.nextCursor").isEmpty());
        mvc.perform(get("/api/v1/posts").param("cursor", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
        mvc.perform(get("/api/v1/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("작성자"))
                .andExpect(jsonPath("$.data.member").doesNotExist());

        mvc.perform(delete("/api/v1/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        assertFalse(repository.existsById(post.getId()));
    }

    @Test
    void membersWithSameNameHaveDifferentIds() {
        Long anotherId = memberService.create("작성자").getId();
        assertNotEquals(memberId, anotherId);
        assertEquals("작성자", memberService.findById(anotherId).getName());
    }

    @Test
    void deleteMemberRemovesOnlyTheirPosts() {
        Post own = service.create("제목", "본문", Category.DAILY, memberId);
        Long anotherId = memberService.create("작성자").getId();
        Post other = service.create("다른 글", "본문", Category.DAILY, anotherId);

        memberService.delete(memberId);

        assertFalse(memberRepository.existsById(memberId));
        assertFalse(repository.existsById(own.getId()));
        assertTrue(repository.existsById(other.getId()));
        assertTrue(memberRepository.existsById(anotherId));
        assertThrows(MemberNotFoundException.class, () -> memberService.delete(memberId));
    }

    @Test
    void postRequiresExistingMemberAndReturnsNameOutsideTransaction() {
        assertThrows(MemberNotFoundException.class,
                () -> service.create("제목", "본문", Category.DAILY, -1L));
        assertThrows(IllegalArgumentException.class,
                () -> service.create("제목", "본문", Category.DAILY, null));
        Post post = service.create("제목", "본문", Category.DAILY, memberId);
        assertEquals("작성자", service.getDetail(post.getId()).getName());
        assertEquals("작성자", service.findAll(null, 10, null).posts().getFirst().getName());
    }

    @Test
    void createPersistsPostWithGeneratedId() {
        Post created = service.create(" 제목 ", " 본문 ", Category.DAILY, memberId);

        assertTrue(created.getId() > 0);
        Post stored = repository.findById(created.getId()).orElseThrow();
        assertEquals("제목", stored.getTitle());
        assertEquals("본문", stored.getContent());
        assertEquals(Category.DAILY, stored.getCategory());
        assertEquals("작성자", stored.getAuthor());
        assertNotNull(stored.getCreatedAt());
        assertEquals(0, stored.getViewCount());
    }

    @Test
    void updateCommitsChangesThroughDirtyChecking() {
        Post created = service.create("제목", "본문", Category.DAILY, memberId);

        service.update(created.getId(), "수정 제목", "수정 본문", Category.QUESTION);

        Post stored = repository.findById(created.getId()).orElseThrow();
        assertEquals("수정 제목", stored.getTitle());
        assertEquals("수정 본문", stored.getContent());
        assertEquals(Category.QUESTION, stored.getCategory());
        assertEquals("작성자", stored.getAuthor());
        assertNotNull(stored.getUpdatedAt());
        assertEquals(0, stored.getViewCount());
    }

    @Test
    void detailPersistsViewCountWhileOrdinaryReadsDoNotIncreaseIt() {
        Post created = service.create("제목", "본문", Category.DAILY, memberId);

        service.findById(created.getId());
        service.findAll(null, 10, null);
        assertEquals(0, repository.findById(created.getId()).orElseThrow().getViewCount());

        service.getDetail(created.getId());
        service.getDetail(created.getId());

        assertEquals(2, repository.findById(created.getId()).orElseThrow().getViewCount());
    }

    @Test
    void cursorFiltersCategoryAndContinuesAfterDeletedPost() {
        Post first = service.create("첫 글", "본문", Category.DAILY, memberId);
        service.create("질문", "본문", Category.QUESTION, memberId);
        Post third = service.create("다음 글", "본문", Category.DAILY, memberId);

        var page = service.findAll(null, 1, Category.DAILY);
        assertEquals(List.of(third.getId()), page.posts().stream().map(Post::getId).toList());
        assertTrue(page.hasNext());
        service.delete(third.getId());
        service.create("새 글", "본문", Category.DAILY, memberId);

        var next = service.findAll(page.nextCursor(), 1, Category.DAILY);
        assertEquals(List.of(first.getId()), next.posts().stream().map(Post::getId).toList());
        assertFalse(next.hasNext());
        assertNull(next.nextCursor());
    }

    @Test
    void cursorOrdersByCreationTimeAndUsesIdForTies() {
        Post first = service.create("첫 글", "본문", Category.DAILY, memberId);
        Post second = service.create("둘째 글", "본문", Category.DAILY, memberId);
        Post third = service.create("셋째 글", "본문", Category.DAILY, memberId);
        var time = java.time.LocalDateTime.of(2026, 1, 1, 12, 0);
        jdbc.update("UPDATE post SET created_at = ? WHERE id = ?", time, first.getId());
        jdbc.update("UPDATE post SET created_at = ? WHERE id = ?", time, second.getId());
        jdbc.update("UPDATE post SET created_at = ? WHERE id = ?", time.minusDays(1), third.getId());

        var page = service.findAll(null, 1, null);
        assertEquals(second.getId(), page.posts().getFirst().getId());
        var next = service.findAll(page.nextCursor(), 1, null);
        assertEquals(first.getId(), next.posts().getFirst().getId());
        var last = service.findAll(next.nextCursor(), 1, null);
        assertEquals(third.getId(), last.posts().getFirst().getId());
        assertFalse(last.hasNext());
        assertNull(last.nextCursor());
    }

    @Test
    void cursorValidatesInputAndHandlesEmptyList() {
        assertTrue(service.findAll(null, 10, null).posts().isEmpty());
        assertFalse(service.findAll(null, 10, null).hasNext());
        assertNull(service.findAll(null, 10, null).nextCursor());
        assertThrows(IllegalArgumentException.class, () -> service.findAll("invalid", 10, null));
        assertThrows(IllegalArgumentException.class, () -> service.findAll("", 10, null));
        assertThrows(IllegalArgumentException.class, () -> service.findAll(null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> service.findAll(null, 101, null));
    }
    @Test
    void deleteRemovesPostFromDatabase() {
        Post created = service.create("제목", "본문", Category.DAILY, memberId);

        service.delete(created.getId());

        assertFalse(repository.existsById(created.getId()));
        assertThrows(PostNotFoundException.class, () -> service.delete(created.getId()));
    }

    @Test
    void missingPostStillRaisesDomainException() {
        assertThrows(PostNotFoundException.class, () -> service.findById(-1L));
        assertThrows(PostNotFoundException.class, () -> service.getDetail(-1L));
        assertThrows(PostNotFoundException.class,
                () -> service.update(-1L, "제목", "본문", Category.DAILY));
        assertThrows(PostNotFoundException.class, () -> service.delete(-1L));
    }
}
