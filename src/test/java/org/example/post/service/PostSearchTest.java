package org.example.post.service;

import org.example.global.apiPayload.exception.BusinessException;
import org.example.member.entity.Member;
import org.example.member.repository.MemberRepository;
import org.example.post.entity.Post;
import org.example.post.entity.enums.Category;
import org.example.post.exception.PostErrorCode;
import org.example.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:post-search;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@Transactional
class PostSearchTest {
    @Autowired PostService service;
    @Autowired PostRepository posts;
    @Autowired MemberRepository members;

    @Test
    void optionalFiltersCombineAndResultsStayNewestFirst() {
        Member first = members.save(new Member("first"));
        Member second = members.save(new Member("second"));
        Post older = save("세미나 후기", Category.DAILY, first);
        Post newer = save("세미나 질문", Category.QUESTION, first);
        Post other = save("다른 제목", Category.DAILY, second);

        assertEquals(List.of(other.getId(), newer.getId(), older.getId()), ids(service.search(null, null, null)));
        assertEquals(List.of(newer.getId(), older.getId()), ids(service.search(" 세미나 ", null, null)));
        assertEquals(List.of(newer.getId(), older.getId()), ids(service.search(null, first.getId(), null)));
        assertEquals(List.of(other.getId(), older.getId()), ids(service.search(null, null, Category.DAILY)));
        assertEquals(List.of(older.getId()), ids(service.search("세미나", first.getId(), Category.DAILY)));
        assertTrue(service.search("없는 검색어", null, null).isEmpty());
        assertTrue(service.search(null, Long.MAX_VALUE, null).isEmpty());
        assertEquals("first", service.search("세미나", first.getId(), Category.DAILY).getFirst().getName());
        assertEquals(0, posts.findById(older.getId()).orElseThrow().getViewCount());
    }

    @Test
    void wildcardAndEscapeCharactersAreSearchedLiterallyInTitleOnly() {
        Member member = members.save(new Member("writer"));
        Post percent = save("100% 할인", Category.DAILY, member);
        Post underscore = save("a_b", Category.DAILY, member);
        Post exclamation = save("알림!", Category.DAILY, member);
        posts.saveAndFlush(new Post("일반 제목", "100% a_b 알림!", Category.DAILY, member));

        assertEquals(List.of(percent.getId()), ids(service.search("%", null, null)));
        assertEquals(List.of(underscore.getId()), ids(service.search("_", null, null)));
        assertEquals(List.of(exclamation.getId()), ids(service.search("!", null, null)));
    }

    @Test
    void blankKeywordKeepsExistingBusinessError() {
        for (String keyword : List.of("", " ", "\t\n")) {
            BusinessException error = assertThrows(BusinessException.class,
                    () -> service.search(keyword, null, null));
            assertEquals(PostErrorCode.SEARCH_KEYWORD_REQUIRED, error.getErrorCode());
        }
    }

    private Post save(String title, Category category, Member member) {
        return posts.saveAndFlush(new Post(title, "본문", category, member));
    }

    private List<Long> ids(List<Post> result) {
        return result.stream().map(Post::getId).toList();
    }
}
