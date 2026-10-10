package org.example.client;

import org.example.controller.ApiResponse;
import org.example.domain.Category;
import org.example.domain.Post;
import java.util.List;

public class PostView {
    private final PostInput input;
    private final PostOutput output;

    public PostView(PostInput input, PostOutput output) {
        this.input = input;
        this.output = output;
    }

    public void printMenu() {
        output.printMenu();
    }

    public int readCommand() {
        while (true) {
            output.printPrompt("선택: ");
            try {
                return input.readCommand();
            } catch (NumberFormatException e) {
                output.printMessage("숫자를 입력해주세요.");
            }
        }
    }

    public String readTitle() {
        output.printPrompt("제목: ");
        return input.readText();
    }

    public String readContent() {
        output.printPrompt("내용: ");
        return input.readText();
    }

    public String readAuthor() {
        output.printPrompt("작성자: ");
        return input.readText();
    }

    public long readPostNumber(String message) {
        while (true) {
            output.printPrompt(message);
            try {
                return input.readPostNumber();
            } catch (NumberFormatException e) {
                output.printMessage("숫자를 입력해주세요.");
            }
        }
    }

    public Category readCategory() {
        output.printCategories();
        while (true) {
            output.printPrompt("카테고리 선택: ");
            try {
                return input.readCategory();
            } catch (NumberFormatException e) {
                output.printMessage("숫자를 입력해주세요.");
            } catch (IllegalArgumentException e) {
                output.printMessage(e.getMessage());
            }
        }
    }

    public boolean printResult(ApiResponse<?> response) {
        return output.printResult(response);
    }

    public void printPosts(ApiResponse<List<Post>> response) {
        output.printPosts(response);
    }

    public void printPost(ApiResponse<Post> response) {
        output.printPost(response);
    }

    public void printMessage(String message) {
        output.printMessage(message);
    }
}
