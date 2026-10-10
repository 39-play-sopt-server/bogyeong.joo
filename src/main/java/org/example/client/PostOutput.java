package org.example.client;

import org.example.controller.ApiResponse;
import org.example.domain.Category;
import org.example.domain.Post;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostOutput {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void printMenu() {
        printMessage("\n=== 게시판 ===");
        printMessage("1. 게시글 작성");
        printMessage("2. 게시글 목록 조회");
        printMessage("3. 게시글 단건 조회");
        printMessage("4. 게시글 수정");
        printMessage("5. 게시글 삭제");
        printMessage("6. 종료");
    }

    public void printCategories() {
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            printMessage((i + 1) + ". " + categories[i].getLabel());
        }
    }

    public boolean printResult(ApiResponse<?> response) {
        if (response.isSuccess()) {
            printMessage(response.getMessage());
            return true;
        }
        printMessage("[" + response.getCode() + "] " + response.getMessage());
        return false;
    }

    public void printPosts(ApiResponse<List<Post>> response) {
        if (!printResult(response)) {
            return;
        }
        printMessage("\n=== 게시글 목록 ===");
        List<Post> posts = response.getData();
        if (posts.isEmpty()) {
            printMessage("게시글이 없습니다.");
            return;
        }
        for (Post post : posts) {
            printMessage("ID " + post.getId() + " | " + post.getCategory().getLabel()
                    + " | " + post.getTitle() + " | " + post.getAuthor());
        }
    }

    public void printPost(ApiResponse<Post> response) {
        if (!printResult(response)) {
            return;
        }
        Post post = response.getData();
        printMessage("\n=== 게시글 ===");
        printMessage("ID: " + post.getId());
        printMessage("제목: " + post.getTitle());
        printMessage("내용: " + post.getContent());
        printMessage("카테고리: " + post.getCategory().getLabel());
        printMessage("작성자: " + post.getAuthor());
        printMessage("작성 시각: " + post.getCreatedAt().format(dateFormat));
        if (post.getUpdatedAt() != null) {
            printMessage("수정 시각: " + post.getUpdatedAt().format(dateFormat));
        }
    }

    public void printPrompt(String message) {
        System.out.print(message);
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
