package org.example;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private final List<PostData> posts = new ArrayList<>();

    public record PostData(String title, String content) {
        public PostData {
            validate(title, "제목");
            validate(content, "본문");
        }

        private static void validate(String value, String fieldName) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                        fieldName + "은 비어 있을 수 없습니다."
                );
            }
        }
    }

    public void create(String title, String content) {
        posts.add(new PostData(title, content));
    }

    public List<PostData> findAll() {
        return List.copyOf(posts);
    }

    public PostData findByNumber(int number) {
        return posts.get(toIndex(number));
    }

    public void update(int number, String title, String content) {
        int index = toIndex(number);
        PostData updatedPost = new PostData(title, content);

        posts.set(index, updatedPost);
    }

    public void delete(int number) {
        posts.remove(toIndex(number));
    }

    private int toIndex(int number) {
        if (number < 1 || number > posts.size()) {
            throw new IllegalArgumentException(
                    "존재하지 않는 게시글입니다."
            );
        }

        return number - 1;
    }
}