package org.example;

import java.util.List;

public class PostView {

    public void showMenu() {
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
    }

    public void showPrompt(String message) {
        System.out.print(message);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showPosts(List<Post.PostData> posts) {
        showMessage("\n=== 게시글 목록 ===");

        if (posts.isEmpty()) {
            showMessage("게시글이 없습니다.");
            return;
        }

        for (int i = 0; i < posts.size(); i++) {
            showMessage((i + 1) + ". " + posts.get(i).title());
        }
    }

    public void showPost(Post.PostData post) {
        showMessage("\n=== 게시글 ===");
        showMessage("제목: " + post.title());
        showMessage("본문: " + post.content());
    }
}