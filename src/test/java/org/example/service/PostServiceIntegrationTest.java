package org.example.service;

import org.example.domain.Category;
import org.example.domain.Post;
import org.example.exception.PostNotFoundException;
import org.example.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    @BeforeEach
    void clearPosts() {
        repository.deleteAll();
    }

    // 테스트에 트랜잭션을 붙이지 않아 서비스가 커밋한 결과를 새 조회로 확인합니다.
    @Test
    void createPersistsPostWithGeneratedId() {
        Post created = service.create(" 제목 ", " 본문 ", Category.DAILY, " 작성자 ");

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
        Post created = service.create("제목", "본문", Category.DAILY, "작성자");

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
        Post created = service.create("제목", "본문", Category.DAILY, "작성자");

        service.findById(created.getId());
        service.findAll(0, 10, null);
        assertEquals(0, repository.findById(created.getId()).orElseThrow().getViewCount());

        service.getDetail(created.getId());
        service.getDetail(created.getId());

        assertEquals(2, repository.findById(created.getId()).orElseThrow().getViewCount());
    }

    @Test
    void listPreservesIdOrderAndFiltersBeforePagination() {
        Post first = service.create("첫 글", "본문", Category.DAILY, "작성자");
        Post second = service.create("질문", "본문", Category.QUESTION, "작성자");
        Post third = service.create("다음 글", "본문", Category.DAILY, "작성자");

        assertEquals(List.of(first.getId(), second.getId(), third.getId()),
                service.findAll(0, 10, null).stream().map(Post::getId).toList());
        assertEquals(List.of(third.getId()),
                service.findAll(1, 1, Category.DAILY).stream().map(Post::getId).toList());
        assertTrue(service.findAll(2, 1, Category.DAILY).isEmpty());
    }

    @Test
    void deleteRemovesPostFromDatabase() {
        Post created = service.create("제목", "본문", Category.DAILY, "작성자");

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
