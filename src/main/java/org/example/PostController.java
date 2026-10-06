package org.example;

import java.util.Scanner;

public class PostController {

    private final Post model;
    private final PostView view;
    private final Scanner scanner;

    public PostController(
            Post model,
            PostView view,
            Scanner scanner
    ) {
        this.model = model;
        this.view = view;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            view.showMenu();
            int command = readInt("선택: ");

            try {
                switch (command) {
                    case 1 -> createPost();
                    case 2 -> view.showPosts(model.findAll());
                    case 3 -> readPost();
                    case 4 -> updatePost();
                    case 5 -> deletePost();
                    case 6 -> {
                        view.showMessage("프로그램을 종료합니다.");
                        return;
                    }
                    default -> view.showMessage("잘못된 선택입니다.");
                }
            } catch (IllegalArgumentException e) {
                view.showMessage(e.getMessage());
            }
        }
    }

    private void createPost() {
        String title = readText("제목: ");
        String content = readText("본문: ");

        model.create(title, content);
        view.showMessage("게시글이 작성되었습니다.");
    }

    private void readPost() {
        int number = readInt("조회할 게시글 번호: ");

        Post.PostData post = model.findByNumber(number);
        view.showPost(post);
    }

    private void updatePost() {
        int number = readInt("수정할 게시글 번호: ");

        // 게시글이 존재하는지 먼저 확인합니다.
        model.findByNumber(number);

        String title = readText("새로운 제목: ");
        String content = readText("새로운 본문: ");

        model.update(number, title, content);
        view.showMessage("게시글이 수정되었습니다.");
    }

    private void deletePost() {
        int number = readInt("삭제할 게시글 번호: ");

        model.delete(number);
        view.showMessage("게시글이 삭제되었습니다.");
    }

    private String readText(String prompt) {
        view.showPrompt(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt).trim());
            } catch (NumberFormatException e) {
                view.showMessage("숫자를 입력해주세요.");
            }
        }
    }
}