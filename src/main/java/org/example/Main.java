package org.example;

import org.example.client.PostView;
import org.example.client.PostInput;
import org.example.client.PostOutput;
import org.example.controller.PostController;
import org.example.domain.Category;
import org.example.repository.InMemoryPostRepository;
import org.example.service.PostIdGenerator;
import org.example.service.PostService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 시작할 때 필요한 객체를 직접 만들고 연결합니다.
        PostService service = new PostService(new InMemoryPostRepository(), new PostIdGenerator());
        PostController controller = new PostController(service);

        try (Scanner scanner = new Scanner(System.in)) {
            PostView view = new PostView(new PostInput(scanner), new PostOutput());
            run(controller, view);
        }
    }

    private static void run(PostController controller, PostView view) {
        while (true) {
            view.printMenu();
            int command = view.readCommand();
            switch (command) {
                case 1 -> createPost(controller, view);
                case 2 -> readPosts(controller, view);
                case 3 -> readPost(controller, view);
                case 4 -> updatePost(controller, view);
                case 5 -> deletePost(controller, view);
                case 6 -> {
                    view.printMessage("프로그램을 종료합니다.");
                    return;
                }
                default -> view.printMessage("잘못된 입력입니다.");
            }
        }
    }

    private static void createPost(PostController controller, PostView view) {
        String title = view.readTitle();
        String content = view.readContent();
        Category category = view.readCategory();
        String author = view.readAuthor();
        view.printPost(controller.create(title, content, category, author));
    }

    private static void readPosts(PostController controller, PostView view) {
        view.printPosts(controller.findAll());
    }

    private static void readPost(PostController controller, PostView view) {
        long id = view.readPostNumber("조회할 게시글 ID: ");
        view.printPost(controller.findById(id));
    }

    private static void updatePost(PostController controller, PostView view) {
        long id = view.readPostNumber("수정할 게시글 ID: ");
        if (!view.printResult(controller.findById(id))) {
            return;
        }
        String title = view.readTitle();
        String content = view.readContent();
        Category category = view.readCategory();
        view.printPost(controller.update(id, title, content, category));
    }

    private static void deletePost(PostController controller, PostView view) {
        long id = view.readPostNumber("삭제할 게시글 ID: ");
        view.printResult(controller.delete(id));
    }
}
